package org.cru.godtools.api.translations;

import com.google.common.collect.Sets;
import org.cru.godtools.api.images.domain.Image;
import org.cru.godtools.api.languages.Language;
import org.cru.godtools.api.languages.LanguageService;
import org.cru.godtools.api.packages.domain.*;
import org.cru.godtools.api.packages.domain.Package;
import org.cru.godtools.api.packages.utils.LanguageCode;
import org.cru.godtools.api.translations.domain.Translation;
import org.cru.godtools.api.translations.domain.TranslationService;
import org.cru.godtools.api.utilities.ResourceNotFoundException;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
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

	public GodToolsTranslationService(){}

	@Inject
	public GodToolsTranslationService(PackageService packageService,
								  VersionService versionService,
								  TranslationService translationService,
								  LanguageService languageService,
								  PackageStructureService packageStructureService,
								  PageStructureService pageStructureService,
								  TranslationElementService translationElementService)
	{
		this.packageService = packageService;
		this.translationService = translationService;
		this.languageService = languageService;
		this.packageStructureService = packageStructureService;
		this.pageStructureService = pageStructureService;
		this.translationElementService = translationElementService;
	}

	/**
	 * Retrieves a specific package in a specific language at a specific revision if revision number is passed, or the latest version if null.
	 *
	 *
	 * @param languageCode
	 * @param packageCode
	 * @param versionNumber
	 * @return
	 */
	public GodToolsTranslation getTranslation(LanguageCode languageCode,
									  String packageCode,
									  BigDecimal versionNumber,
									  Integer minimumInterpreterVersion)
	{
		Translation translation = getTranslation(packageCode, languageCode, versionNumber);
		Package gtPackage = getPackage(packageCode);
		PackageStructure packageStructure = packageStructureService.selectByPackageId(gtPackage.getId());
		List<PageStructure> pageStructures = pageStructureService.selectByPackageStructureId(packageStructure.getId());
		List<TranslationElement> translationElementList = translationElementService.selectByTranslationId(translation.getId());


		return null;
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

	private Translation getTranslation(String packageCode, LanguageCode languageCode, BigDecimal versionNumber)
	{
		Language language = languageService.selectByLanguageCode(languageCode);
		Package gtPackage = packageService.selectByCode(packageCode);

		return translationService.selectByLanguageIdPackageIdVersionNumber(language.getId(), gtPackage.getId(), versionNumber.remainder(BigDecimal.ONE).intValue());
	}

	private Package getPackage(String packageCode)
	{
		return packageService.selectByCode(packageCode);
	}
}
