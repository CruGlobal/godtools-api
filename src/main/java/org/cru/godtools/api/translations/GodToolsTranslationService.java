package org.cru.godtools.api.translations;

import com.google.common.collect.Sets;
import org.cru.godtools.api.languages.Language;
import org.cru.godtools.api.languages.LanguageService;
import org.cru.godtools.api.packages.domain.*;
import org.cru.godtools.api.packages.domain.Package;
import org.cru.godtools.api.packages.utils.LanguageCode;
import org.cru.godtools.api.translations.domain.Translation;
import org.cru.godtools.api.translations.domain.TranslationService;
import org.cru.godtools.api.utilities.ResourceNotFoundException;
import org.w3c.dom.Document;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by ryancarlson on 4/8/14.
 */
public class GodToolsTranslationService
{
	protected PackageService packageService;
	protected VersionService versionService;
	protected TranslationService translationService;
	protected LanguageService languageService;
	protected PageService pageService;

	public GodToolsTranslationService(){}

	@Inject
	public GodToolsTranslationService(PackageService packageService,
								  VersionService versionService,
								  TranslationService translationService,
								  LanguageService languageService,
								  PageService pageService)
	{
		this.packageService = packageService;
		this.versionService = versionService;
		this.translationService = translationService;
		this.languageService = languageService;
		this.pageService = pageService;
	}

	/**
	 * Retrieves a specific package in a specific language at a specific revision if revision number is passed, or the latest version if null.
	 *
	 *
	 * @param languageCode
	 * @param packageCode
	 * @param revisionNumber
	 * @return
	 */
	public GodToolsTranslation getTranslation(LanguageCode languageCode,
									  String packageCode,
									  Integer revisionNumber,
									  Integer minimumInterpreterVersion)
	{
		Translation translation = getTranslation(packageCode, languageCode);

		Version version = getVersion(revisionNumber, minimumInterpreterVersion, translation);
		List<Page> pages = pageService.selectByVersionId(version.getId());

		return new GodToolsTranslation(version.getPackageStructure(),
				pages,
				languageCode.toString(),
				packageCode);
	}

	/**
	 * Retrieves all packages for specified language at specified revision
	 *
	 *
	 * @param languageCode
	 * @return

	 */
	public Set<GodToolsTranslation> getTranslationsForLanguage(LanguageCode languageCode, Integer minimumInterpreterVersion)
	{
		Set<GodToolsTranslation> translations = Sets.newHashSet();

		Language language = languageService.selectByLanguageCode(languageCode);
		List<Translation> translationsForLanguage = translationService.selectByLanguageId(language.getId());

		for(Translation translation : translationsForLanguage)
		{
			try
			{
				Package gtPackage = packageService.selectById(translation.getPackageId());
				translations.add(getTranslation(languageCode, gtPackage.getCode(), Version.LATEST_VERSION_NUMBER, minimumInterpreterVersion));
			}
			//if the desired revision doesn't exist.. that's fine, just continue on to the next translation.
			catch(NotFoundException e){ continue; }
		}

		return translations;
	}

	public Version getCurrentDraftVersion(String packageCode, LanguageCode languageCode)
	{
		Version latestVersion = versionService.selectLatestVersionForTranslation(getOrCreateTranslation(packageCode, languageCode).getId());
		if(latestVersion.isReleased()) throw new ResourceNotFoundException(Version.class);
		else return latestVersion;
	}

	/**
	 * Used to create a new translation version in "draft" status.
	 */
	public Version createNewVersion(String packageCode, LanguageCode languageCode)
	{
		Translation translation = getOrCreateTranslation(packageCode, languageCode);
		Version version = new Version();
		version.setId(UUID.randomUUID());
		version.setReleased(false);
		version.setTranslationId(translation.getId());
		version.setMinimumInterpreterVersion(1);  //TODO: yeah figure this out sometime

		try
		{
			version.setDraftVersionNumber(versionService.selectLatestVersionForTranslation(translation.getId()));
		}
		catch(ResourceNotFoundException e)
		{
			version.setVersionNumber(1);
		}

		versionService.insert(version);

		return version;
	}


	public void saveDraftPage(Document document, String filename, Version currentDraftVersion)
	{
		Page draftPage = pageService.selectByFilenameAndVersionId(filename, currentDraftVersion.getId());

		if(draftPage != null)
		{
			draftPage.setXmlContent(document);
			pageService.update(draftPage);
		}
		else
		{
			Page newDraftPage = new Page();
			newDraftPage.setId(UUID.randomUUID());
			newDraftPage.setVersionId(currentDraftVersion.getId());
			newDraftPage.setFilename(filename);
			newDraftPage.setXmlContent(document);
			newDraftPage.calculateHash();

			pageService.insert(newDraftPage);
		}
	}

	/**
	 * Used to update a translation version which has not yet been pushed live
	 */
	public void updateVersion(Version currentDraftVersion)
	{
		versionService.update(currentDraftVersion);
	}

	private Translation getTranslation(String packageCode, LanguageCode languageCode)
	{
		Language language = languageService.selectByLanguageCode(languageCode);
		Package gtPackage = packageService.selectByCode(packageCode);

		return translationService.selectByLanguageIdPackageId(language.getId(), gtPackage.getId());
	}

	private Translation getOrCreateTranslation(String packageCode, LanguageCode languageCode)
	{
		Language language = getOrCreateLanguage(languageCode);
		Package gtPackage = getOrCreatePackage(packageCode);

		try
		{
			return getTranslation(packageCode, languageCode);
		}
		catch(ResourceNotFoundException e)
		{
			Translation newTranslation = new Translation();
			newTranslation.setId(UUID.randomUUID());
			newTranslation.setPackageId(gtPackage.getId());
			newTranslation.setLanguageId(language.getId());
			translationService.insert(newTranslation);

			return newTranslation;
		}
	}

	private Package getOrCreatePackage(String packageCode)
	{
		try
		{
			return packageService.selectByCode(packageCode);
		}
		catch(ResourceNotFoundException e)
		{
			Package gtPackage = new Package();
			gtPackage.setId(UUID.randomUUID());
			gtPackage.setCode(packageCode);
			packageService.insert(gtPackage);

			return gtPackage;
		}
	}

	private Version getVersion(Integer versionNumber, Integer minimumInterpreterVersion, Translation translation)
	{
		if(minimumInterpreterVersion == null)
		{
			return versionNumber.equals(Version.LATEST_VERSION_NUMBER) ? versionService.selectLatestVersionForTranslation(translation.getId()) :
					versionService.selectSpecificVersionForTranslation(translation.getId(), versionNumber);
		}
		else
		{
			return versionNumber.equals(Version.LATEST_VERSION_NUMBER) ? versionService.selectLatestVersionForTranslation(translation.getId(), minimumInterpreterVersion) :
					versionService.selectSpecificVersionForTranslation(translation.getId(), versionNumber, minimumInterpreterVersion);
		}
	}

	private Language getOrCreateLanguage(LanguageCode languageCode)
	{
		try
		{
			return languageService.selectByLanguageCode(languageCode);
		}
		catch(ResourceNotFoundException e)
		{
			Language newLanguage = new Language();
			newLanguage.setId(UUID.randomUUID());
			newLanguage.setFromLanguageCode(languageCode);
			languageService.insert(newLanguage);

			return newLanguage;
		}
	}

}
