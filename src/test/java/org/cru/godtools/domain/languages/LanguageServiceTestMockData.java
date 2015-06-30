package org.cru.godtools.domain.languages;

import org.cru.godtools.domain.services.*;
import org.testng.Assert;

import java.util.List;
import java.util.UUID;

/**
 * Created by ryancarlson on 4/2/14.
 */
public class LanguageServiceTestMockData
{

	public static void persistLanguages(LanguageService languageService)
	{
		Language language = new Language();
		language.setId(LanguageServiceTest.TEST_LANGUAGE_ID);
		language.setCode("fr");
		language.setName("French - Nantais");
		language.setLocale("nt");
		language.setSubculture("hipster");

		languageService.insert(language);

		Language language2 = new Language();
		language2.setId(LanguageServiceTest.TEST_LANGUAGE2_ID);
		language2.setCode("en");
		language2.setName("English");

		languageService.insert(language2);
	}

	public static Language getNonExistantLanguage()
	{
		Language nonExistantLanguage = new Language();
		nonExistantLanguage.setId(UUID.randomUUID());
		nonExistantLanguage.setCode("fr");
		return nonExistantLanguage;
	}

	public static void validateLanguages(List<Language> languages)
	{
		for(Language language : languages)
		{
			if(language.getId().equals(LanguageServiceTest.TEST_LANGUAGE_ID)) validateLanguage(language);
			else if(language.getId().equals(LanguageServiceTest.TEST_LANGUAGE2_ID)) validateLanguage2(language);
			else Assert.fail("Unknown language..");
		}
	}
	public static void validateLanguage(Language language)
	{
		Assert.assertNotNull(language);
		Assert.assertEquals(language.getId(), LanguageServiceTest.TEST_LANGUAGE_ID);
		Assert.assertEquals(language.getCode(), "fr");
		Assert.assertEquals(language.getName(), "French - Nantais");
		Assert.assertEquals(language.getLocale(), "nt");
		Assert.assertEquals(language.getSubculture(), "hipster");
	}

	public static void validateLanguage2(Language language2)
	{
		Assert.assertNotNull(language2);
		Assert.assertEquals(language2.getId(), LanguageServiceTest.TEST_LANGUAGE2_ID);
		Assert.assertEquals(language2.getCode(), "en");
		Assert.assertEquals(language2.getName(), "English");
	}
}
