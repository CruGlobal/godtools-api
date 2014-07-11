package org.cru.godtools.domain.languages;

import org.cru.godtools.domain.AbstractServiceTest;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

/**
 * Created by ryancarlson on 3/20/14.
 */
public class LanguageServiceTest extends AbstractServiceTest
{
	LanguageServiceTestMockDataService mockData;

    LanguageService languageService;

	public static final UUID TEST_LANGUAGE_ID = UUID.randomUUID();
	public static final UUID TEST_LANGUAGE2_ID = UUID.randomUUID();

	@BeforeClass()
	public void setup()
	{
		super.setup();;

		languageService = new LanguageService(sqlConnection);

		mockData = new LanguageServiceTestMockDataService();

		mockData.persistLanguages(languageService);
	}

    @Test
    public void testSelectAllLanguages()
    {
		List<Language> languages = languageService.selectAllLanguages();

		Assert.assertEquals(languages.size(), 2);
		mockData.validateLanguages(languages);
    }

    @Test
    public void testSelectLanguageById()
    {
		Language language = languageService.selectLanguageById(TEST_LANGUAGE_ID);

		Assert.assertNotNull(language);
		mockData.validateLanguage(language);

		Language language2 = languageService.selectLanguageById(TEST_LANGUAGE_ID);

		Assert.assertNotNull(language2);
		mockData.validateLanguage(language2);
    }

    @Test
    public void testSelectLanguageByCode()
    {
		LanguageCode languageCode = new LanguageCode("fr_nt_hipster");
		Language language = languageService.selectByLanguageCode(languageCode);

		Assert.assertNotNull(language);
		mockData.validateLanguage(language);

		LanguageCode languageCode2 = new LanguageCode("fr_nt_hipster");
		Language language2 = languageService.selectByLanguageCode(languageCode2);

		Assert.assertNotNull(language2);
		mockData.validateLanguage(language2);
	}

	@Test()
	public void testSelectLanguageByCodeNotFound()
	{
		LanguageCode languageCode = new LanguageCode("fr");
		Assert.assertNull(languageService.selectByLanguageCode(languageCode));
	}


	@Test
	public void testLanguageExists()
	{
		Language language = languageService.selectLanguageById(TEST_LANGUAGE_ID);

		Assert.assertTrue(languageService.languageExists(language));

		Language language2 = languageService.selectLanguageById(TEST_LANGUAGE2_ID);

		Assert.assertTrue(languageService.languageExists(language2));

		Language nonExistantLanguage = mockData.getNonExistantLanguage();

		Assert.assertFalse(languageService.languageExists(nonExistantLanguage));
	}
}
