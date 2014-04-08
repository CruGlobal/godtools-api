package org.cru.godtools.api.translations;

import com.google.common.collect.Sets;
import org.cru.godtools.api.languages.Language;
import org.cru.godtools.api.languages.LanguageService;
import org.cru.godtools.api.packages.domain.*;
import org.cru.godtools.api.packages.domain.Package;
import org.cru.godtools.api.packages.utils.LanguageCode;
import org.cru.godtools.api.translations.domain.Translation;
import org.cru.godtools.api.translations.domain.TranslationService;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import java.util.List;
import java.util.Set;

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
		Language language = languageService.selectByLanguageCode(languageCode);
		Package gtPackage = packageService.selectByCode(packageCode);
		Translation translation = translationService.selectByLanguageIdPackageId(language.getId(), gtPackage.getId());

		Version version = getVersion(revisionNumber, minimumInterpreterVersion, translation);
		List<Page> pages = pageService.selectByVersionId(version.getId());

		return new GodToolsTranslation(version.getPackageStructure(),
				pages,
				languageCode.toString(),
				packageCode);
	}

	private Version getVersion(Integer revisionNumber, Integer minimumInterpreterVersion, Translation translation)
	{
		if(minimumInterpreterVersion == null)
		{
			return revisionNumber != null ? versionService.selectSpecificVersionForTranslation(translation.getId(), revisionNumber)
					: versionService.selectLatestVersionForTranslation(translation.getId());
		}
		else
		{
			return revisionNumber != null ? versionService.selectSpecificVersionForTranslation(translation.getId(), revisionNumber, minimumInterpreterVersion)
					: versionService.selectLatestVersionForTranslation(translation.getId(), minimumInterpreterVersion);
		}
	}

	/**
	 * Retrieves all packages for specified language at specified revision
	 *
	 *
	 * @param languageCode
	 * @param revisionNumber
	 * @return

	 */
	public Set<GodToolsTranslation> getTranslationsForLanguage(LanguageCode languageCode,
													   Integer revisionNumber,
													   Integer minimumInterpreterVersion)
	{
		Set<GodToolsTranslation> translations = Sets.newHashSet();

		Language language = languageService.selectByLanguageCode(languageCode);
		List<Translation> translationsForLanguage = translationService.selectByLanguageId(language.getId());

		for(Translation translation : translationsForLanguage)
		{
			try
			{
				Package gtPackage = packageService.selectById(translation.getPackageId());
				translations.add(getTranslation(languageCode, gtPackage.getCode(), revisionNumber, minimumInterpreterVersion));
			}
			//if the desired revision doesn't exist.. that's fine, just continue on to the next translation.
			catch(NotFoundException e){ continue; }
		}

		return translations;
	}
}
