package org.cru.godtools.api.packages;

import org.cru.godtools.domain.model.Language;
import org.cru.godtools.domain.languages.LanguageCode;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by ryancarlson on 4/2/14.
 */
public class LanguageCodeTest
{

	@Test
	public void testCodeWithLanguageLocaleSubculture()
	{
		LanguageCode languageCode = new LanguageCode("en-us-hipster");

		Assert.assertEquals(languageCode.toString(), "en-us-hipster");
		Assert.assertEquals(languageCode.getLanguageCode(), "en");
		Assert.assertEquals(languageCode.getLocaleCode(), "us");
		Assert.assertEquals(languageCode.getSubculture(), "hipster");
	}

	@Test
	public void testCodeWithLanguageLocale()
	{
		LanguageCode languageCode = new LanguageCode("fr-nt");

		Assert.assertEquals(languageCode.toString(), "fr-nt");
		Assert.assertEquals(languageCode.getLanguageCode(), "fr");
		Assert.assertEquals(languageCode.getLocaleCode(), "nt");
		Assert.assertNull(languageCode.getSubculture());
	}

	@Test
	public void testCodeWithLanguage()
	{
		LanguageCode languageCode = new LanguageCode("es");

		Assert.assertEquals(languageCode.toString(), "es");
		Assert.assertEquals(languageCode.getLanguageCode(), "es");
		Assert.assertNull(languageCode.getLocaleCode());
		Assert.assertNull(languageCode.getSubculture());
	}

	@Test
	public void testCodeWithLanguageSubculture()
	{
		LanguageCode languageCode = new LanguageCode("en-hipster");

		Assert.assertEquals(languageCode.toString(), "en-hipster");
		Assert.assertEquals(languageCode.getLanguageCode(), "en");
		Assert.assertNull(languageCode.getLocaleCode());
		Assert.assertEquals(languageCode.getSubculture(), "hipster");
	}

	@Test
	public void testFromLanguageWithLanguageLocaleSubculture()
	{
		Language language = new Language();

		language.setCode("en");
		Assert.assertEquals(LanguageCode.fromLanguage(language).toString(), "en");

		language.setLocale("us");
		Assert.assertEquals(LanguageCode.fromLanguage(language).toString(), "en-us");

		language.setSubculture("hipster");
		Assert.assertEquals(LanguageCode.fromLanguage(language).toString(), "en-us-hipster");

		language.setLocale(null);
		Assert.assertEquals(LanguageCode.fromLanguage(language).toString(), "en-hipster");
	}

}
