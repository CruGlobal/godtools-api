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
import org.cru.godtools.domain.packages.PackageStructure;
import org.cru.godtools.domain.packages.PackageStructureService;
import org.cru.godtools.domain.translations.Translation;
import org.cru.godtools.domain.translations.TranslationService;
import org.cru.godtools.tests.XmlDocumentFromFile;

/**
 * Created by ryancarlson on 4/2/14.
 */
public class GodToolsPackageServiceTestMockData
{

	public static void persistPackage(LanguageService languageService,
							   PackageService packageService,
							   PackageStructureService packageStructureService,
							   TranslationService translationService,
							   ImageService imageService,
							   ReferencedImageService referencedImageService)
	{
		persistLanguage(languageService);
		persistPackage(packageService);
		persistPackageStructure(packageStructureService);
		persistTranslation(translationService);
		persistImage(imageService);
		persistReferencedImage(referencedImageService);

		persistIcon(imageService);
		persistReferencedImageIcon(referencedImageService);
	}

	private static void persistLanguage(LanguageService languageService)
	{
		Language language = new Language();
		language.setId(GodToolsPackageServiceTest.LANGUAGE_ID);
		language.setCode("en");
		language.setName("English");

		languageService.insert(language);
	}

	private static void persistPackage(PackageService packageService)
	{
		Package gtPackage = new Package();
		gtPackage.setId(GodToolsPackageServiceTest.PACKAGE_ID);
		gtPackage.setCode("kgp");
		gtPackage.setName("Knowing God Personally");

		packageService.insert(gtPackage);
	}

	private static void persistTranslation(TranslationService translationService)
	{
		Translation translation = new Translation();
		translation.setId(GodToolsPackageServiceTest.TRANSLATION_ID);
		translation.setPackageId(GodToolsPackageServiceTest.PACKAGE_ID);
		translation.setLanguageId(GodToolsPackageServiceTest.LANGUAGE_ID);
		translation.setVersionNumber(1);
		translation.setReleased(true);
		translationService.insert(translation);
	}

	private static void persistImage(ImageService imageService)
	{
		Image image = new Image();
		image.setId(GodToolsPackageServiceTest.IMAGE_ID);
		image.setResolution("High");
		image.setImageContent(ImageReader.read("/test_image_1.png"));
		image.setFilename("test_image_1.png");
		imageService.insert(image);
	}

	private static void persistIcon(ImageService imageService)
	{
		Image image = new Image();
		image.setId(GodToolsPackageServiceTest.ICON_ID);
		image.setResolution("High");
		image.setImageContent(ImageReader.read("/test_image_1.png"));
		image.setFilename("icon.png");
		imageService.insert(image);
	}

	private static void persistReferencedImage(ReferencedImageService referencedImageService)
	{
		ReferencedImage referencedImage = new ReferencedImage();
		referencedImage.setImageId(GodToolsPackageServiceTest.IMAGE_ID);
		referencedImage.setPackageStructureId(GodToolsPackageServiceTest.PACKAGE_STRUCTURE_ID);
		referencedImageService.insert(referencedImage);
	}

	private static void persistReferencedImageIcon(ReferencedImageService referencedImageService)
	{
		ReferencedImage referencedImage = new ReferencedImage();
		referencedImage.setImageId(GodToolsPackageServiceTest.ICON_ID);
		referencedImage.setPackageStructureId(GodToolsPackageServiceTest.PACKAGE_STRUCTURE_ID);
		referencedImageService.insert(referencedImage);
	}

	private static void persistPackageStructure(PackageStructureService packageStructureService)
	{
		PackageStructure packageStructure = new PackageStructure();
		packageStructure.setId(GodToolsPackageServiceTest.PACKAGE_STRUCTURE_ID);
		packageStructure.setPackageId(GodToolsPackageServiceTest.PACKAGE_ID);
		packageStructure.setVersionNumber(1);
		packageStructure.setXmlContent(XmlDocumentFromFile.get("/test_file_1.xml"));
		packageStructureService.insert(packageStructure);
	}

	public static void validateEnglishKgpPackage(GodToolsPackage englishKgpPackage)
	{

	}
}
