package org.cru.godtools.s3;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.URL;

/**
 * Created by matthewfrederick on 8/14/15.
 */
public class AmazonS3GodToolsConfigTest extends Arquillian
{
	public static final String lang = "en";
	public static final String pack = "kgp";
	private static final String BASE_URL = "https://s3.amazonaws.com/cru-godtools/";


	@Deployment
	public static WebArchive createDeployment()
	{
		return ShrinkWrap.create(WebArchive.class)
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
	}

	@Test
	public void testGetPackagesKeyV2()
	{
		String key = AmazonS3GodToolsConfig.getPackagesKeyV2(lang);
		Assert.assertEquals(key, "packages/current/en/all/en.zip");
	}

	@Test
	public void testGetLanguagesKeyV2()
	{
		String key = AmazonS3GodToolsConfig.getLanguagesKeyV2(lang);
		Assert.assertEquals(key, "translations/current/en/all/en.zip");
	}

	@Test
	public void testGetLanguageAndPackageKeyV2()
	{
		String key = AmazonS3GodToolsConfig.getLanguageAndPackageKeyV2(lang, pack);
		Assert.assertEquals(key, "translations/current/en/kgp.zip");
	}

	@Test
	public void testGetPackagesRedirectUrl()
	{
		try
		{
			URL key = AmazonS3GodToolsConfig.getPackagesRedirectUrl(lang);
			URL expected = new URL(BASE_URL + "packages/en/all.zip");
			Assert.assertEquals(key, expected);

			key = AmazonS3GodToolsConfig.getPackagesRedirectUrl(lang, pack);
			expected = new URL(BASE_URL + "packages/en/kgp.zip");
			Assert.assertEquals(key, expected);
		}
		catch (Exception e)
		{
			Assert.fail(e.getMessage(), e);
		}
	}

	@Test
	public void tetGetMetaRedirectUrl()
	{
		try
		{
			URL key = AmazonS3GodToolsConfig.getMetaRedirectUrl();
			URL expected = new URL(BASE_URL + "meta/all.xml");
			Assert.assertEquals(key, expected);
		}
		catch (Exception e)
		{
			Assert.fail(e.getMessage(), e);
		}
	}

	@Test
	public void testGetLanguagesRedirectUrl()
	{
		try
		{
			URL key = AmazonS3GodToolsConfig.getLanguagesRedirectUrl(lang);
			URL expected = new URL(BASE_URL + "translations/en/all.zip");
			Assert.assertEquals(key, expected);

			key = AmazonS3GodToolsConfig.getLanguagesRedirectUrl(lang, pack);
			expected = new URL(BASE_URL + "translations/en/kgp.zip");
			Assert.assertEquals(key, expected);
		}
		catch (Exception e)
		{
			Assert.fail(e.getMessage(), e);
		}
	}
}
