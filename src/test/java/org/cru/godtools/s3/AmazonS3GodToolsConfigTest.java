package org.cru.godtools.s3;

import org.testng.Assert;
import org.testng.annotations.Test;

import javax.ws.rs.core.MediaType;
import java.net.MalformedURLException;
import java.net.URL;


public class AmazonS3GodToolsConfigTest
{
	public static final String lang = "en";
	public static final String pack = "kgp";
	private static final String BASE_URL = "https://s3.amazonaws.com/cru-godtools/";

	@Test
	public void testGetPackagesKeyV2()
	{
		String key = AmazonS3GodToolsConfig.getPackagesKeyV2(lang);
		Assert.assertEquals(key, "packages/en/all.zip");
	}

	@Test
	public void testGetLanguagesKeyV2()
	{
		String key = AmazonS3GodToolsConfig.getTranslationsKeyV2(lang);
		Assert.assertEquals(key, "translations/en/all.zip");
	}

	@Test
	public void testGetLanguageAndPackageKeyV2()
	{
		String key = AmazonS3GodToolsConfig.getTranslationsAndPackageKeyV2(lang, pack);
		Assert.assertEquals(key, "translations/en/kgp.zip");
	}

	@Test
	public void testGetPackagesRedirectUrl() throws MalformedURLException
	{
		URL key = AmazonS3GodToolsConfig.getPackagesRedirectUrl(lang);
		URL expected = new URL(BASE_URL + "packages/en/all.zip");
		Assert.assertEquals(key, expected);

		key = AmazonS3GodToolsConfig.getPackagesRedirectUrl(lang, pack);
		expected = new URL(BASE_URL + "packages/en/kgp.zip");
		Assert.assertEquals(key, expected);
	}

	@Test
	public void tetGetMetaRedirectUrl() throws MalformedURLException
	{
		URL key = AmazonS3GodToolsConfig.getMetaRedirectUrl(MediaType.APPLICATION_XML_TYPE);
		URL expected = new URL(BASE_URL + "meta/all.xml");
		Assert.assertEquals(key, expected);
	}

	@Test
	public void testGetLanguagesRedirectUrl() throws MalformedURLException
	{
		URL key = AmazonS3GodToolsConfig.getTranslationsRedirectUrl(lang);
		URL expected = new URL(BASE_URL + "translations/en/all.zip");
		Assert.assertEquals(key, expected);

		key = AmazonS3GodToolsConfig.getTranslationsRedirectUrl(lang, pack);
		expected = new URL(BASE_URL + "translations/en/kgp.zip");
		Assert.assertEquals(key, expected);
	}
}
