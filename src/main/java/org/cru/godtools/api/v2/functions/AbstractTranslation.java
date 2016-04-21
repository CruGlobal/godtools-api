package org.cru.godtools.api.v2.functions;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.cru.godtools.api.translations.GodToolsTranslation;
import org.cru.godtools.domain.GodToolsVersion;
import org.cru.godtools.domain.images.Image;
import org.cru.godtools.domain.images.ImageService;
import org.cru.godtools.domain.images.ReferencedImage;
import org.cru.godtools.domain.images.ReferencedImageService;
import org.cru.godtools.domain.languages.Language;
import org.cru.godtools.domain.languages.LanguageCode;
import org.cru.godtools.domain.languages.LanguageService;

import org.cru.godtools.domain.packages.Package;
import org.cru.godtools.domain.packages.PackageService;
import org.cru.godtools.domain.packages.PackageStructure;
import org.cru.godtools.domain.packages.PackageStructureService;
import org.cru.godtools.domain.packages.PageStructure;
import org.cru.godtools.domain.packages.PageStructureService;
import org.cru.godtools.domain.packages.TranslationElement;
import org.cru.godtools.domain.packages.TranslationElementService;
import org.cru.godtools.domain.translations.Translation;
import org.cru.godtools.domain.translations.TranslationService;
import org.cru.godtools.translate.client.TranslationDownload;
import org.cru.godtools.translate.client.TranslationResults;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public abstract class AbstractTranslation
{
	static final Set<String> PACKAGE_CODES = Sets.newHashSet("kgp", "fourlaws", "satisfied", "new");

	final Logger logger = Logger.getLogger(getClass());

	@Inject
	TranslationService translationService;
	@Inject
	LanguageService languageService;
	@Inject
	PackageService packageService;
	@Inject
	PackageStructureService packageStructureService;
	@Inject
	PageStructureService pageStructureService;
	@Inject
	TranslationElementService translationElementService;
	@Inject
	ReferencedImageService referencedImageService;
	@Inject
	ImageService imageService;

	@Inject
	TranslationDownload translationDownload;

	public List<GodToolsTranslation> retrieve(String languageCode)
	{
		List<GodToolsTranslation> godToolsTranslationList = Lists.newArrayList();

		for(String packageCode : PACKAGE_CODES)
		{
			Optional<GodToolsTranslation> godToolsTranslationOptional = retrieve(languageCode, packageCode);

			if(godToolsTranslationOptional.isPresent())
			{
				godToolsTranslationList.add(godToolsTranslationOptional.get());
			}
		}

		return godToolsTranslationList;
	}

	public Optional<GodToolsTranslation> retrieve(String languageCode, String packageCode)
	{
		return retrieve(languageCode, packageCode, true);
	}

	public Optional<GodToolsTranslation> retrieve(String languageCode, String packageCode, boolean withImages)
	{
		org.cru.godtools.domain.packages.Package gtPackage = packageService.selectByCode(packageCode);
		Language language = languageService.selectByLanguageCode(new LanguageCode(languageCode));

		if(language == null) return Optional.absent();

		// try to load out the latest version of translation for this package/language combo
		Translation currentTranslation = translationService.selectByLanguageIdPackageIdVersionNumber(language.getId(),
				gtPackage.getId(),
				determineVersion());

		if(currentTranslation == null) return Optional.absent();

		if(this instanceof DraftTranslation)
		{
			downloadLatestTranslations(gtPackage,language,currentTranslation);
		}

		PackageStructure packageStructure = packageStructureService.selectByPackageId(gtPackage.getId());
		List<PageStructure> pageStructureList = pageStructureService.selectByTranslationId(currentTranslation.getId());
		List<TranslationElement> translationElementList = translationElementService.selectByTranslationId(currentTranslation.getId());

		List<Image> imageList = Lists.newArrayList();

		if(withImages)
		{
			for (ReferencedImage referencedImage : referencedImageService.selectByPackageStructureId(packageStructure.getId()))
			{
				imageList.add(imageService.selectById(referencedImage.getImageId()));
			}
		}

		return Optional.fromNullable(
				GodToolsTranslation.assembleFromComponents(gtPackage,
						language,
						currentTranslation,
						packageStructure,
						pageStructureList,
						translationElementList,
						imageList,
						withImages ? imageService.selectByFilename(Image.buildFilename(packageCode, "icon@2x.png")) : null));
	}

	protected void downloadLatestTranslations(Package gtPackage, Language language, Translation currentTranslation)
	{
		for(PageStructure pageStructure : pageStructureService.selectByTranslationId(currentTranslation.getId()))
		{
			logger.info(String.format("Downloading page with ID %s from OneSky for %s in %s", pageStructure.getId(), gtPackage.getCode(), language.getName()));

			TranslationResults downloadedTranslations = translationDownload.doDownload(gtPackage.getTranslationProjectId(),
					language.getPath(),
					pageStructure.getFilename());

			for(UUID translatedElementId : downloadedTranslations.keySet())
			{
				translationElementService.update(translatedElementId,
						currentTranslation.getId(),
						downloadedTranslations.get(translatedElementId));
			}
		}

		logger.info(String.format("Downloading \"package page\" with filename %s%s from Onesky for %s in %s",
				gtPackage.getCode(),
				".xml",
				gtPackage.getName(),
				language.getName()));

		TranslationResults downloadedTranslations = translationDownload.doDownload(gtPackage.getTranslationProjectId(),
				language.getPath(),
				gtPackage.getCode().concat(".xml"));

		for(UUID translatedElementId : downloadedTranslations.keySet())
		{
			translationElementService.update(translatedElementId,
					currentTranslation.getId(),
					downloadedTranslations.get(translatedElementId));
		}
	}

	private GodToolsVersion determineVersion()
	{
		return (this instanceof DraftTranslation) ? GodToolsVersion.DRAFT_VERSION : GodToolsVersion.LATEST_PUBLISHED_VERSION;
	}
}
