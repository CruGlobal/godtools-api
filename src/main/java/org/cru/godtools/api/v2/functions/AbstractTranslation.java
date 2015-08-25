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
import org.cru.godtools.domain.packages.*;
import org.cru.godtools.domain.translations.Translation;
import org.cru.godtools.domain.translations.TranslationService;

import javax.inject.Inject;
import java.util.List;
import java.util.Set;

public abstract class AbstractTranslation
{
	static final Set<String> PACKAGE_CODES = Sets.newHashSet("kgp", "fourlaws", "satisfied");

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
		org.cru.godtools.domain.packages.Package gtPackage = packageService.selectByCode(packageCode);
		Language language = languageService.getOrCreateLanguage(new LanguageCode(languageCode));

		// try to load out the latest version of translation for this package/language combo
		Translation currentTranslation = translationService.selectByLanguageIdPackageIdVersionNumber(language.getId(),
				gtPackage.getId(),
				determineVersion());

		if(currentTranslation == null) return Optional.absent();

		PackageStructure packageStructure = packageStructureService.selectByPackageId(gtPackage.getId());
		List<PageStructure> pageStructureList = pageStructureService.selectByTranslationId(currentTranslation.getId());
		List<TranslationElement> translationElementList = translationElementService.selectByTranslationId(currentTranslation.getId());
		List<Image> imageList = Lists.newArrayList();

		for(ReferencedImage referencedImage : referencedImageService.selectByPackageStructureId(packageStructure.getId()))
		{
			imageList.add(imageService.selectById(referencedImage.getImageId()));
		}

		return Optional.fromNullable(
				GodToolsTranslation.assembleFromComponents(gtPackage,
						language,
						currentTranslation,
						packageStructure,
						pageStructureList,
						translationElementList,
						imageList,
						imageService.selectByFilename(
								Image.buildFilename(packageCode, "icon@2x.png"))));
	}

	private GodToolsVersion determineVersion()
	{
		return (this instanceof DraftTranslation) ? GodToolsVersion.DRAFT_VERSION : GodToolsVersion.LATEST_PUBLISHED_VERSION;
	}
}
