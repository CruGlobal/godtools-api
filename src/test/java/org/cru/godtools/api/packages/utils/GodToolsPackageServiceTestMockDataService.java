package org.cru.godtools.api.packages.utils;

import org.cru.godtools.api.images.domain.Image;
import org.cru.godtools.api.images.domain.ImageService;
import org.cru.godtools.api.images.domain.ReferencedImage;
import org.cru.godtools.api.images.domain.ReferencedImageService;
import org.cru.godtools.api.languages.Language;
import org.cru.godtools.api.languages.LanguageService;
import org.cru.godtools.api.packages.GodToolsPackage;
import org.cru.godtools.api.packages.GodToolsPackageService;
import org.cru.godtools.api.packages.GodToolsPackageServiceTest;
import org.cru.godtools.api.packages.domain.*;
import org.cru.godtools.api.packages.domain.Package;
import org.cru.godtools.api.translations.domain.Translation;
import org.cru.godtools.api.translations.domain.TranslationService;
import org.cru.godtools.migration.ImageReader;
import org.cru.godtools.tests.XmlDocumentFromFile;
import org.testng.Assert;

/**
 * Created by ryancarlson on 4/2/14.
 */
public class GodToolsPackageServiceTestMockDataService
{

	public void persistPackage(LanguageService languageService,
							   PackageService packageService,
							   TranslationService translationService,
							   VersionService versionService,
							   PageService pageService,
							   ImageService imageService,
							   ReferencedImageService referencedImageService)
	{
		persistLanguage(languageService);
		persistPackage(packageService);
		persistTranslation(translationService);
		persistVersion(versionService);
		persistPage(pageService);
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

	private void persistVersion(VersionService versionService)
	{
		Version version = new Version();
		version.setId(GodToolsPackageServiceTest.VERSION_ID);
		version.setTranslationId(GodToolsPackageServiceTest.TRANSLATION_ID);
		version.setVersionNumber(1);
		version.setMinimumInterpreterVersion(1);
		version.setReleased(true);
		version.setPackageStructure(XmlDocumentFromFile.get("/test_file_1.xml"));
		version.setPackageStructureHash(ShaGenerator.calculateHash(version.getPackageStructure()));

		versionService.insert(version);
	}

	private void persistPage(PageService pageService)
	{
		Page page = new Page();
		page.setId(GodToolsPackageServiceTest.PAGE_ID);
		page.setVersionId(GodToolsPackageServiceTest.VERSION_ID);
		page.setXmlContent(XmlDocumentFromFile.get("/test_file_1.xml"));
		page.setPageHash(ShaGenerator.calculateHash(page.getXmlContent()));

		pageService.insert(page);
	}

	private void persistImage(ImageService imageService)
	{
		Image image = new Image();
		image.setId(GodToolsPackageServiceTest.IMAGE_ID);
		image.setResolution("High");
		image.setImageContent(ImageReader.read("/test_image_1.png"));
		image.setImageHash(ShaGenerator.calculateHash(image.getImageContent()));

		imageService.insert(image);
	}

	private void  persistReferencedImage(ReferencedImageService referencedImageService)
	{
		ReferencedImage referencedImage = new ReferencedImage();
		referencedImage.setImageId(GodToolsPackageServiceTest.IMAGE_ID);
		referencedImage.setPageId(GodToolsPackageServiceTest.PAGE_ID);
		referencedImage.setVersionId(GodToolsPackageServiceTest.VERSION_ID);

		referencedImageService.insert(referencedImage);
	}

	public void validateEnglishKgpPackage(GodToolsPackage englishKgpPackage)
	{
		Assert.assertNotNull(englishKgpPackage);

		Assert.assertEquals(englishKgpPackage.getLanguageCode(), "en");
		Assert.assertEquals(englishKgpPackage.getPackageCode(), "kgp");
		Assert.assertEquals(englishKgpPackage.getPackageXmlHash(), ShaGenerator.calculateHash(englishKgpPackage.getPackageXml()));

		Assert.assertEquals(englishKgpPackage.getImages().size(), 1);
		Assert.assertEquals(englishKgpPackage.getImages().iterator().next().getId(), GodToolsPackageServiceTest.IMAGE_ID);

		Assert.assertEquals(englishKgpPackage.getPageFiles().size(), 1);
		Assert.assertEquals(englishKgpPackage.getPageFiles().get(0).getId(), GodToolsPackageServiceTest.PAGE_ID);
	}
}
