package org.cru.godtools.api.translations;

import org.cru.godtools.domain.packages.Package;
import org.cru.godtools.domain.images.Image;
import org.cru.godtools.domain.images.ImageService;
import org.cru.godtools.domain.images.ReferencedImage;
import org.cru.godtools.domain.images.ReferencedImageService;
import org.cru.godtools.domain.languages.Language;
import org.cru.godtools.domain.languages.LanguageService;
import org.cru.godtools.tests.AbstractFullPackageServiceTest;
import org.cru.godtools.utils.ImageReader;
import org.cru.godtools.domain.packages.PackageService;
import org.cru.godtools.domain.packages.PackageStructure;
import org.cru.godtools.domain.packages.PackageStructureService;
import org.cru.godtools.domain.packages.PageStructure;
import org.cru.godtools.domain.packages.PageStructureService;
import org.cru.godtools.domain.packages.TranslationElement;
import org.cru.godtools.domain.packages.TranslationElementService;
import org.cru.godtools.domain.translations.Translation;
import org.cru.godtools.domain.translations.TranslationService;
import org.cru.godtools.utils.XmlDocumentFromFile;

import java.util.UUID;

/**
 * Created by ryancarlson on 4/2/14.
 */
public class GodToolsTranslationServiceTestMockData
{	
	private static final String PACKAGE_CODE = "kgp";

	public static void persistPackage(LanguageService languageService,
							   PackageService packageService,
							   PackageStructureService packageStructureService,
							   PageStructureService pageStructureService,
							   TranslationElementService translationElementService,
							   TranslationService translationService,
							   ImageService imageService,
							   ReferencedImageService referencedImageService)
	{
		persistLanguage(languageService);
		persistPackage(packageService);
		persistPackageStructure(packageStructureService);
		persistTranslation(translationService);
		persistPageStructure(pageStructureService);
		persistTranslationElements(translationElementService);
		persistImage(imageService);
		persistReferencedImage(referencedImageService);

		persistIcon(imageService);
		persistReferencedImageIcon(referencedImageService);
	}



	private static void persistLanguage(LanguageService languageService)
	{
		Language language = new Language();
		language.setId(AbstractFullPackageServiceTest.LANGUAGE_ID);
		language.setCode("en");
		language.setName("English");

		languageService.insert(language);
	}

	private static void persistPackage(PackageService packageService)
	{
		Package gtPackage = new Package();
		gtPackage.setId(AbstractFullPackageServiceTest.PACKAGE_ID);
		gtPackage.setCode(PACKAGE_CODE);
		gtPackage.setName("Knowing God Personally");

		packageService.insert(gtPackage);
	}

	private static void persistTranslation(TranslationService translationService)
	{
		Translation translation = new Translation();
		translation.setId(AbstractFullPackageServiceTest.TRANSLATION_ID);
		translation.setPackageId(AbstractFullPackageServiceTest.PACKAGE_ID);
		translation.setLanguageId(AbstractFullPackageServiceTest.LANGUAGE_ID);
		translation.setVersionNumber(1);
		translation.setReleased(true);
		translationService.insert(translation);
	}

	private static void persistImage(ImageService imageService)
	{
		Image image = new Image();
		image.setId(AbstractFullPackageServiceTest.IMAGE_ID);
		image.setResolution("High");
		image.setImageContent(ImageReader.read("/test_image_1.png"));
		image.setFilename("test_image_1.png");
		imageService.insert(image);
	}

	private static void persistIcon(ImageService imageService)
	{
		Image image = new Image();
		image.setId(AbstractFullPackageServiceTest.ICON_ID);
		image.setResolution("High");
		image.setImageContent(ImageReader.read("/test_image_1.png"));
		image.setFilename(Image.buildFilename(PACKAGE_CODE, "icon@2x.png"));
		imageService.insert(image);
	}

	private static void persistReferencedImage(ReferencedImageService referencedImageService)
	{
		ReferencedImage referencedImage = new ReferencedImage();
		referencedImage.setImageId(AbstractFullPackageServiceTest.IMAGE_ID);
		referencedImage.setPackageStructureId(AbstractFullPackageServiceTest.PACKAGE_STRUCTURE_ID);
		referencedImageService.insert(referencedImage);
	}

	private static void persistReferencedImageIcon(ReferencedImageService referencedImageService)
	{
		ReferencedImage referencedImage = new ReferencedImage();
		referencedImage.setImageId(AbstractFullPackageServiceTest.ICON_ID);
		referencedImage.setPackageStructureId(AbstractFullPackageServiceTest.PACKAGE_STRUCTURE_ID);
		referencedImageService.insert(referencedImage);
	}

	private static void persistPackageStructure(PackageStructureService packageStructureService)
	{
		PackageStructure packageStructure = new PackageStructure();
		packageStructure.setId(AbstractFullPackageServiceTest.PACKAGE_STRUCTURE_ID);
		packageStructure.setPackageId(AbstractFullPackageServiceTest.PACKAGE_ID);
		packageStructure.setVersionNumber(1);
		packageStructure.setXmlContent(XmlDocumentFromFile.get("/package.xml"));
		packageStructureService.insert(packageStructure);
	}

	private static void persistPageStructure(PageStructureService pageStructureService)
	{
		PageStructure pageStructure = new PageStructure();
		pageStructure.setId(AbstractFullPackageServiceTest.PAGE_STRUCTURE_ID);
		pageStructure.setTranslationId(AbstractFullPackageServiceTest.TRANSLATION_ID);
		pageStructure.setFilename("page_1.xml");
		pageStructure.setXmlContent(XmlDocumentFromFile.get("/page_1.xml"));
		pageStructureService.insert(pageStructure);
	}


	/**
	 * The element IDs here are hard coded b/c they correspond with IDs in the file
	 * src/main/resources/page_1.xml
	 */
	private static void persistTranslationElements(TranslationElementService translationElementService)
	{
		TranslationElement elementOne = new TranslationElement();
		elementOne.setId(UUID.fromString("d32fce50-df42-4ab7-9815-e0a151213a01"));
		elementOne.setPageStructureId(AbstractFullPackageServiceTest.PAGE_STRUCTURE_ID);
		elementOne.setTranslationId(AbstractFullPackageServiceTest.TRANSLATION_ID);
		elementOne.setBaseText("KNOWING GOD");
		elementOne.setTranslatedText("KNOWING GOD");
		elementOne.setDisplayOrder(0);
		elementOne.setElementType("heading");
		elementOne.setPageName("page_1.xml");
		translationElementService.insert(elementOne);

		TranslationElement elementTwo = new TranslationElement();
		elementTwo.setId(UUID.fromString("6f2678f1-93d2-42d9-bca5-bc2e16593216"));
		elementTwo.setPageStructureId(AbstractFullPackageServiceTest.PAGE_STRUCTURE_ID);
		elementTwo.setTranslationId(AbstractFullPackageServiceTest.TRANSLATION_ID);
		elementTwo.setBaseText("personally");
		elementTwo.setTranslatedText("personally");
		elementTwo.setDisplayOrder(1);
		elementTwo.setElementType("subheading");
		elementTwo.setPageName("page_1.xml");
		translationElementService.insert(elementTwo);

		TranslationElement elementThree = new TranslationElement();
		elementThree.setId(UUID.fromString("5d1e91d4-6a9f-44cd-ac01-f45800ef1fd7"));
		elementThree.setPageStructureId(AbstractFullPackageServiceTest.PAGE_STRUCTURE_ID);
		elementThree.setTranslationId(AbstractFullPackageServiceTest.TRANSLATION_ID);
		elementThree.setBaseText("These four points explain how to enter into a personal relationship with God and experience the life for which you were created.");
		elementThree.setTranslatedText("These four points explain how to enter into a personal relationship with God and experience the life for which you were created.");
		elementThree.setDisplayOrder(2);
		elementThree.setElementType("text");
		elementThree.setPageName("page_1.xml");
		translationElementService.insert(elementThree);
	}
}
