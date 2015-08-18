package org.cru.godtools.s3;


import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by ryancarlson on 7/11/15.
 */
public class AmazonS3GodToolsConfig
{
	public static final String BUCKET_NAME = "cru-godtools";

	private static final String BASE_URL = "https://s3.amazonaws.com/cru-godtools/";

	private static final String META = "meta/";
	private static final String PACKAGES = "packages/";
	private static final String TRANSLATIONS = "translations/";
	private static final String ALL_FILE = "all";
	private static final String XML = ".xml";
	private static final String ZIP = ".zip";

	public static String getMetaKeyV2()
	{
		return META + ALL_FILE + XML;
	}

	/**
	 * e.g. packages/en/all.zip  OR
	 */
	public static String getPackagesKeyV2(String languageCode)
	{
		return PACKAGES + languageCode + "/" + ALL_FILE + ZIP;
	}

	/**
	 * e.g. translations/en/all.zip  OR
	 */
	public static String getTranslationsKeyV2(String languageCode)
	{
		return TRANSLATIONS + languageCode + "/" + ALL_FILE + ZIP;
	}

	/**
	 * e.g. translations/en/kgp.zip
	 */
	public static String getTranslationsAndPackageKeyV2(String languageCode, String packageCode)
	{
		return TRANSLATIONS + languageCode + "/" + packageCode + ZIP;
	}

	public static URL getPackagesRedirectUrl(String languageCode) throws MalformedURLException
	{
		return new URL(BASE_URL + PACKAGES + languageCode + "/" + ALL_FILE + ZIP);
	}

	public static URL getPackagesRedirectUrl(String languageCode, String packageCode) throws MalformedURLException
	{
		return new URL(BASE_URL + PACKAGES + languageCode + "/" +  packageCode + ZIP);
	}

	public static URL getMetaRedirectUrl() throws MalformedURLException
	{
		return new URL(BASE_URL + META + ALL_FILE + XML);
	}

	public static URL getTranslationsRedirectUrl(String languageCode) throws MalformedURLException
	{
		return new URL(BASE_URL + TRANSLATIONS + languageCode + "/" + ALL_FILE + ZIP);
	}

	public static URL getTranslationsRedirectUrl(String languageCode, String packageCode) throws MalformedURLException
	{
		return new URL(BASE_URL + TRANSLATIONS + languageCode + "/" + packageCode + ZIP);
	}
}
