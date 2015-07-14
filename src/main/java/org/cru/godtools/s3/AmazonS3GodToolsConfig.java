package org.cru.godtools.s3;

import com.google.common.base.Strings;

/**
 * Created by ryancarlson on 7/11/15.
 */
public class AmazonS3GodToolsConfig
{
	public static final String BUCKET_NAME = "cru-godtools";

	private static final String META = "meta/";
	private static final String PACKAGES = "packages/";
	private static final String LANGUAGES = "languages/";
	private static final String META_FILE = "meta";
	private static final String ALL = "all/";
	private static final String CURRENT = "current/";
	private static final String XML = ".xml";
	private static final String ZIP = ".zip";

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

	public static String getPackagesKey(String languageCode, String packageCode)
	{
		return PACKAGES + CURRENT + languageCode + "/" + ALL + languageCode + ZIP;
	}

	public static String getLanguagesKey(String languageCode, String packagesCode)
	{
		return LANGUAGES + CURRENT + languageCode + "/" + ALL + languageCode + ZIP;
	}
}
