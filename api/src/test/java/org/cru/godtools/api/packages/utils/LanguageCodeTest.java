package org.cru.godtools.api.packages.utils;

import org.cru.godtools.domain.languages.Language;
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
		LanguageCode languageCode = new LanguageCode("en_us_hipster");

		Assert.assertEquals(languageCode.toString(), "en_us_hipster");
		Assert.assertEquals(languageCode.getLanguageCode(), "en");
		Assert.assertEquals(languageCode.getLocaleCode(), "us");
		Assert.assertEquals(languageCode.getSubculture(), "hipster");
	}

	@Test
	public void testCodeWithLanguageLocale()
	{
		LanguageCode languageCode = new LanguageCode("fr_nt");

		Assert.assertEquals(languageCode.toString(), "fr_nt");
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
		LanguageCode languageCode = new LanguageCode("en_hipster");

		Assert.assertEquals(languageCode.toString(), "en_hipster");
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
		Assert.assertEquals(LanguageCode.fromLanguage(language).toString(), "en_us");

		language.setSubculture("hipster");
		Assert.assertEquals(LanguageCode.fromLanguage(language).toString(), "en_us_hipster");

		language.setLocale(null);
		Assert.assertEquals(LanguageCode.fromLanguage(language).toString(), "en_hipster");
	}

}
