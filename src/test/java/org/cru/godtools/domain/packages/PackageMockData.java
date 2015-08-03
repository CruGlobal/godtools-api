package org.cru.godtools.domain.packages;

import org.cru.godtools.domain.languages.*;
import org.cru.godtools.domain.services.*;
import org.testng.Assert;

/**
 * Created by ryancarlson on 4/2/14.
 */
public class PackageMockData
{
	public static Language persistLanguage(LanguageService languageService)
	{

		Language language = new Language();
		language.setId(PackageServiceTest.TEST_LANGUAGE_ID);
		language.setName("English");
		languageService.insert(language);

		return language;
	}

	public static void persistPackage(PackageService packageService, Language language)
	{
		Package gtPackage = new Package();
		gtPackage.setId(PackageServiceTest.TEST_PACKAGE_ID);
		gtPackage.setName("Test Package");
		gtPackage.setCode("tp");
		gtPackage.setDefaultLanguage(language);

		packageService.insert(gtPackage);

	}

	public static void validatePackage(Package gtPackage)
	{
		Assert.assertNotNull(gtPackage);
		Assert.assertEquals(gtPackage.getId(), PackageServiceTest.TEST_PACKAGE_ID);
		Assert.assertEquals(gtPackage.getName(), "Test Package");
		Assert.assertEquals(gtPackage.getCode(), "tp");
		Assert.assertEquals(gtPackage.getDefaultLanguage().getId(), PackageServiceTest.TEST_LANGUAGE_ID);
	}

	public static void validateOrphanLanguagePackage(Package gtPackage)
	{
		Assert.assertNotNull(gtPackage);
		Assert.assertEquals(gtPackage.getId(), PackageServiceTest.TEST_PACKAGE_ID);
		Assert.assertEquals(gtPackage.getName(), "Test Package");
		Assert.assertEquals(gtPackage.getCode(), "tp");
		Assert.assertNull(gtPackage.getDefaultLanguage());
	}
}
