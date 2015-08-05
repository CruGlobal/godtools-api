package org.cru.godtools.domain.services.mockdata;

import org.cru.godtools.domain.model.*;
import org.cru.godtools.domain.model.Package;
import org.cru.godtools.domain.services.*;
import org.cru.godtools.domain.services.sql2o.*;
import org.testng.Assert;

/**
 * Created by ryancarlson on 4/2/14.
 */
public class TranslationMockData
{

	public static Language persistLanguage(LanguageService languageService)
	{
		Language language = new Language();
		language.setId(TranslationServiceTest.TEST_LANGUAGE_ID);

		languageService.insert(language);

		return language;
	}

	public static Package persistPackage(PackageService packageService)
	{
		Package gtPackage = new Package();
		gtPackage.setId(TranslationServiceTest.TEST_PACKAGE_ID);

		packageService.insert(gtPackage);

		return gtPackage;
	}

	public static Translation persistTranslation(TranslationService translationService, Language language, Package gtPackage)
	{
		Translation translation = new Translation();
		translation.setId(TranslationServiceTest.TEST_TRANSLATION_ID);
		translation.setPackage(gtPackage);
		translation.setLanguage(language);

		translationService.insert(translation);

		return translation;
	}

	public static void validateTranslation(Translation translation)
	{
		Assert.assertNotNull(translation);
		Assert.assertEquals(translation.getId(), TranslationServiceTest.TEST_TRANSLATION_ID);
		Assert.assertEquals(translation.getLanguage().getId(), TranslationServiceTest.TEST_LANGUAGE_ID);
		Assert.assertEquals(translation.getPackage().getId(), TranslationServiceTest.TEST_PACKAGE_ID);
	}

	public void modifyTranslation()
	{

	}


}
