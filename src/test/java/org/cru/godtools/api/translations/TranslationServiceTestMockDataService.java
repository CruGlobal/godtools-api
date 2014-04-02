package org.cru.godtools.api.translations;

import org.cru.godtools.api.languages.Language;
import org.cru.godtools.api.languages.LanguageService;
import org.cru.godtools.api.packages.domain.Package;
import org.cru.godtools.api.packages.domain.PackageService;
import org.testng.Assert;

/**
 * Created by ryancarlson on 4/2/14.
 */
public class TranslationServiceTestMockDataService
{

	public void persistLanguage(LanguageService languageService)
	{
		Language language = new Language();
		language.setId(TranslationServiceTest.TEST_LANGUAGE_ID);

		languageService.insert(language);
	}

	public void persistPackage(PackageService packageService)
	{
		Package gtPackage = new Package();
		gtPackage.setId(TranslationServiceTest.TEST_PACKAGE_ID);

		packageService.insert(gtPackage);
	}

	public void persistTranslation(TranslationService translationService)
	{
		Translation translation = new Translation();
		translation.setId(TranslationServiceTest.TEST_TRANSLATION_ID);
		translation.setPackageId(TranslationServiceTest.TEST_PACKAGE_ID);
		translation.setLanguageId(TranslationServiceTest.TEST_LANGUAGE_ID);

		translationService.insert(translation);
	}

	public void validateTranslation(Translation translation)
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
