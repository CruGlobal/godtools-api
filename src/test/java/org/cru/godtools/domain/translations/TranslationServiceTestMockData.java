package org.cru.godtools.domain.translations;

import org.cru.godtools.api.services.*;
import org.cru.godtools.domain.languages.Language;
import org.cru.godtools.domain.packages.Package;
import org.testng.Assert;

/**
 * Created by ryancarlson on 4/2/14.
 */
public class TranslationServiceTestMockData
{

	public static void persistLanguage(LanguageService languageService)
	{
		Language language = new Language();
		language.setId(TranslationServiceTest.TEST_LANGUAGE_ID);

		languageService.insert(language);
	}

	public static void persistPackage(PackageService packageService)
	{
		Package gtPackage = new Package();
		gtPackage.setId(TranslationServiceTest.TEST_PACKAGE_ID);

		packageService.insert(gtPackage);
	}

	public static void persistTranslation(TranslationService translationService)
	{
		Translation translation = new Translation();
		translation.setId(TranslationServiceTest.TEST_TRANSLATION_ID);
		translation.setPackageId(TranslationServiceTest.TEST_PACKAGE_ID);
		translation.setLanguageId(TranslationServiceTest.TEST_LANGUAGE_ID);

		translationService.insert(translation);
	}

	public static void validateTranslation(Translation translation)
	{
		Assert.assertNotNull(translation);
		Assert.assertEquals(translation.getId(), TranslationServiceTest.TEST_TRANSLATION_ID);
		Assert.assertEquals(translation.getLanguageId(), TranslationServiceTest.TEST_LANGUAGE_ID);
		Assert.assertEquals(translation.getPackageId(), TranslationServiceTest.TEST_PACKAGE_ID);
	}

	public void modifyTranslation()
	{

	}


}
