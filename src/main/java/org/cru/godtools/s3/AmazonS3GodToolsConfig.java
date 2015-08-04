package org.cru.godtools.s3;

import com.google.common.base.Strings;

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
	private static final String LANGUAGES = "languages/";
	private static final String META_FILE = "meta";
	private static final String ALL_FILE = "all";
	private static final String ALL = "all/";
	private static final String CURRENT = "current/";
	private static final String XML = ".xml";
	private static final String ZIP = ".zip";

	/**
	 * e.g. meta/current/en/kgp/meta.xml  OR
	 *      meta/current/en/meta.xml      OR
	 *      meta/current/meta.xml
	 */
	@Deprecated
	public static String getMetaKey(String languageCode, String packageCode)
	{
		if(!Strings.isNullOrEmpty(languageCode) && !Strings.isNullOrEmpty(packageCode))
		{
			return META + CURRENT + languageCode + "/" + packageCode + "/" + META_FILE + XML;
		}
		if(!Strings.isNullOrEmpty(languageCode))
		{
			return META + CURRENT + languageCode + "/" + META_FILE + XML;
		}

		return META + CURRENT + META_FILE + XML;
	}

	public static String getMetaKeyV2()
	{
		return META + ALL_FILE + XML;
	}

	/**
	 * e.g. packages/current/en/all/en.zip  OR
	 */
	@Deprecated
	public static String getPackagesKey(String languageCode, String packageCode)
	{
		return PACKAGES + CURRENT + languageCode + "/" + ALL + languageCode + ZIP;
	}

	/**
	 * e.g. languages/current/en/all/en.zip  OR
	 */
	@Deprecated
	public static String getLanguagesKey(String languageCode, String packagesCode)
	{
		return LANGUAGES + CURRENT + languageCode + "/" + ALL + languageCode + ZIP;
	}

	/**
	 * e.g. packages/current/en/all/en.zip  OR
	 */
	public static String getPackagesKeyV2(String languageCode)
	{
		return PACKAGES + languageCode + "/" + ALL_FILE + ZIP;
	}

	/**
	 * e.g. languages/current/en/all/en.zip  OR
	 */
	public static String getLanguagesKeyV2(String languageCode)
	{
		return LANGUAGES + languageCode + "/" + ALL_FILE + ZIP;
	}

	public static URL getPackagesRedirectUrl(String languageCode) throws MalformedURLException
	{
		return getPackagesRedirectUrl(languageCode, null);
	}

	public static URL getPackagesRedirectUrl(String languageCode, String packageCode) throws MalformedURLException
	{
		if(Strings.isNullOrEmpty(packageCode))
		{
			return new URL(BASE_URL + PACKAGES + languageCode + "/" + ALL_FILE + ZIP);
		}
		else
		{
			return new URL(BASE_URL + PACKAGES + languageCode + "/" +  packageCode + ZIP);
		}
	}

	public static URL getMetaRedirectUrl() throws MalformedURLException
	{
		return new URL(BASE_URL + META + ALL_FILE + XML);
	}

	public static URL getLanguagesRedirectUrl(String languageCode) throws MalformedURLException
	{
		return getPackagesRedirectUrl(languageCode, null);
	}

	public static URL getLanguagesRedirectUrl(String languageCode, String packageCode) throws MalformedURLException
	{
		if(Strings.isNullOrEmpty(packageCode))
		{
			return new URL(BASE_URL + LANGUAGES + languageCode + "/" + ALL_FILE + ZIP);
		}
		else
		{
			return new URL(BASE_URL + LANGUAGES + languageCode + "/" + packageCode + ZIP);
		}
	}
}