package org.cru.godtools.api.packages.utils;

import org.cru.godtools.domain.packages.Package;
import org.cru.godtools.domain.images.Image;
import org.cru.godtools.domain.images.ImageService;
import org.cru.godtools.domain.images.ReferencedImage;
import org.cru.godtools.domain.images.ReferencedImageService;
import org.cru.godtools.domain.languages.Language;
import org.cru.godtools.domain.languages.LanguageService;
import org.cru.godtools.api.packages.GodToolsPackage;
import org.cru.godtools.api.packages.GodToolsPackageServiceTest;
import org.cru.godtools.api.utilities.ImageReader;
import org.cru.godtools.domain.packages.PackageService;
import org.cru.godtools.domain.translations.Translation;
import org.cru.godtools.domain.translations.TranslationService;

/**
 * Created by ryancarlson on 4/2/14.
 */
public class GodToolsPackageServiceTestMockDataService
{

	public void persistPackage(LanguageService languageService,
							   PackageService packageService,
							   TranslationService translationService,
							   ImageService imageService,
							   ReferencedImageService referencedImageService)
	{
		persistLanguage(languageService);
		persistPackage(packageService);
		persistTranslation(translationService);
		persistImage(imageService);
		persistReferencedImage(referencedImageService);
	}

	private void persistLanguage(LanguageService languageService)
	{
		Language language = new Language();
		language.setId(GodToolsPackageServiceTest.LANGUAGE_ID);
		language.setCode("en");
		language.setName("English");

		languageService.insert(language);
	}

	private void persistPackage(PackageService packageService)
	{
		Package gtPackage = new Package();
		gtPackage.setId(GodToolsPackageServiceTest.PACKAGE_ID);
		gtPackage.setCode("kgp");
		gtPackage.setName("Knowing God Personally");

		packageService.insert(gtPackage);
	}

	private void persistTranslation(TranslationService translationService)
	{
		Translation translation = new Translation();
		translation.setId(GodToolsPackageServiceTest.TRANSLATION_ID);
		translation.setPackageId(GodToolsPackageServiceTest.PACKAGE_ID);
		translation.setLanguageId(GodToolsPackageServiceTest.LANGUAGE_ID);

		translationService.insert(translation);
	}

	private void persistImage(ImageService imageService)
	{
		Image image = new Image();
		image.setId(GodToolsPackageServiceTest.IMAGE_ID);
		image.setResolution("High");
		image.setImageContent(ImageReader.read("/test_image_1.png"));

		imageService.insert(image);
	}

	private void  persistReferencedImage(ReferencedImageService referencedImageService)
	{
		ReferencedImage referencedImage = new ReferencedImage();
		referencedImage.setImageId(GodToolsPackageServiceTest.IMAGE_ID);

		referencedImageService.insert(referencedImage);
	}

	public void validateEnglishKgpPackage(GodToolsPackage englishKgpPackage)
	{

	}
}
