package org.cru.godtools.api.translations;

import com.google.common.collect.Sets;
import org.cru.godtools.api.languages.Language;
import org.cru.godtools.api.languages.LanguageService;
import org.cru.godtools.api.packages.domain.*;
import org.cru.godtools.api.packages.domain.Package;
import org.cru.godtools.api.packages.utils.GodToolsVersion;
import org.cru.godtools.api.packages.utils.LanguageCode;
import org.cru.godtools.api.translations.domain.Translation;
import org.cru.godtools.api.translations.domain.TranslationService;
import org.cru.godtools.onesky.io.TranslationDownload;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by ryancarlson on 4/8/14.
 */
public class GodToolsTranslationService
{
	protected PackageService packageService;
	protected TranslationService translationService;
	protected LanguageService languageService;
	protected PackageStructureService packageStructureService;
	protected PageStructureService pageStructureService;
	protected TranslationElementService translationElementService;

	private TranslationDownload translationDownload;

	public GodToolsTranslationService(){}

	@Inject
	public GodToolsTranslationService(PackageService packageService, TranslationService translationService, LanguageService languageService, PackageStructureService packageStructureService, PageStructureService pageStructureService, TranslationElementService translationElementService, TranslationDownload translationDownload)
	{
		this.packageService = packageService;
		this.translationService = translationService;
		this.languageService = languageService;
		this.packageStructureService = packageStructureService;
		this.pageStructureService = pageStructureService;
		this.translationElementService = translationElementService;
		this.translationDownload = translationDownload;
	}

	/**
	 * Retrieves a specific package in a specific language at a specific version.
	 *
	 *
	 * @param languageCode
	 * @param packageCode
	 * @param godToolsVersion
	 * @return
	 */
	public GodToolsTranslation getTranslation(LanguageCode languageCode,
									  String packageCode,
									  GodToolsVersion godToolsVersion,
									  Integer minimumInterpreterVersion)
	{
		Translation translation = getTranslation(packageCode, languageCode, godToolsVersion);
		Package gtPackage = getPackage(packageCode);
		PackageStructure packageStructure = packageStructureService.selectByPackageId(gtPackage.getId());
		List<PageStructure> pageStructures = pageStructureService.selectByPackageStructureId(packageStructure.getId());

		//draft translations are always updated
		if(!translation.isReleased()) updateTranslationFromTranslationTool(translation, pageStructures);

		List<TranslationElement> translationElementList = translationElementService.selectByTranslationId(translation.getId());

		return GodToolsTranslation.assembleFromComponents(packageStructure, pageStructures, translationElementList);
	}

	/**
	 * Retrieves the latest version of all packages for specified language.
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
				translations.add(getTranslation(languageCode, gtPackage.getCode(), GodToolsVersion.LATEST_VERSION, minimumInterpreterVersion));
			}
			//if the desired revision doesn't exist.. that's fine, just continue on to the next translation.
			catch(NotFoundException e){ continue; }
		}

		return translations;
	}

	public Translation setupNewTranslation(LanguageCode languageCode, String packageCode)
	{
		Package gtPackage = getPackage(packageCode);
		Language language = languageService.getOrCreateLanguage(languageCode);
		Translation latestVersionExistingTranslation = getTranslation(packageCode, languageCode, GodToolsVersion.LATEST_VERSION);

		int nextVersionNumber;

		if(latestVersionExistingTranslation == null)
		{
			nextVersionNumber = 1;
		}
		else if(latestVersionExistingTranslation.isReleased())
		{
			nextVersionNumber = latestVersionExistingTranslation.getVersionNumber() + 1;
		}
		else
		{
			throw new WebApplicationException("A draft version of this translation already exists.  See version " + latestVersionExistingTranslation.getVersionNumber());
		}

		Translation newTranslation = new Translation();
		newTranslation.setId(UUID.randomUUID());
		newTranslation.setLanguageId(language.getId());
		newTranslation.setPackageId(gtPackage.getId());
		newTranslation.setVersionNumber(nextVersionNumber);
		newTranslation.setReleased(false);

		translationService.insert(newTranslation);

		translationElementService.createTranslatableElements(translationService, newTranslation, gtPackage);

		return newTranslation;
	}

	public void updateTranslationsFromTranslationTool(LanguageCode languageCode, String packageCode)
	{
		Translation translation = getTranslation(packageCode, languageCode, GodToolsVersion.LATEST_VERSION);
		Package gtPackage = getPackage(packageCode);
		PackageStructure packageStructure = packageStructureService.selectByPackageId(gtPackage.getId());
		List<PageStructure> pageStructures = pageStructureService.selectByPackageStructureId(packageStructure.getId());

		updateTranslationFromTranslationTool(translation, pageStructures);
	}

	private void updateTranslationFromTranslationTool(Translation translation, List<PageStructure> pageStructures)
	{
		for(PageStructure pageStructure : pageStructures)
		{
			translationDownload.doDownload(translation.getId(), pageStructure.getId());
		}
	}

	private Translation getTranslation(String packageCode, LanguageCode languageCode, GodToolsVersion godToolsVersion)
	{
		Language language = languageService.selectByLanguageCode(languageCode);
		Package gtPackage = packageService.selectByCode(packageCode);

		return translationService.selectByLanguageIdPackageIdVersionNumber(language.getId(), gtPackage.getId(), godToolsVersion);
	}

	private Package getPackage(String packageCode)
	{
		return packageService.selectByCode(packageCode);
	}
}
