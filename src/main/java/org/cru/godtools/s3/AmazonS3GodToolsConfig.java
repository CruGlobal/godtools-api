package org.cru.godtools.s3;

/**
 * Created by ryancarlson on 7/11/15.
 */
public class AmazonS3GodToolsConfig
{
	public static final String BUCKET_NAME = "cru-godtools";

	public static String getMetaKey(String languageCode, String packageCode)
	{
		return "meta/current/meta.xml";
	}

	public static String getPackagesKey(String languageCode, String packageCode)
	{
		return "packages/current/" + languageCode + "/all/" + languageCode + ".zip";
	}

	public static String getLanguagesKey(String languageCode, String packagesCode)
	{
		return "languages/current/" + languageCode + "/all/" + languageCode + ".zip";
	}
}
