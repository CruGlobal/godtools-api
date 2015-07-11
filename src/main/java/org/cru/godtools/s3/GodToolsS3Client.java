package org.cru.godtools.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

import javax.inject.Inject;

/**
 * Created by ryancarlson on 7/11/15.
 */
public class GodToolsS3Client
{
	@Inject
	AmazonS3Client s3Client;

	public S3Object getMetaFile()
	{
		return getMetaFile(null, null);
	}

	public S3Object getMetaFile(String languageCode)
	{
		return getMetaFile(languageCode, null);
	}

	public S3Object getMetaFile(String languageCode, String packageCode)
	{
		return s3Client.getObject(new GetObjectRequest(AmazonS3GodToolsConfig.BUCKET_NAME,
				AmazonS3GodToolsConfig.getMetaKey(languageCode, packageCode)));
	}

	public S3Object getLanguagesZippedFolder(String languageCode)
	{
		return s3Client.getObject(new GetObjectRequest(AmazonS3GodToolsConfig.BUCKET_NAME,
				AmazonS3GodToolsConfig.getLanguagesKey(languageCode, null)));
	}

	public S3Object getLanguagesZippedFolder(String languageCode, String packageCode)
	{
		return s3Client.getObject(new GetObjectRequest(AmazonS3GodToolsConfig.BUCKET_NAME,
				AmazonS3GodToolsConfig.getLanguagesKey(languageCode, packageCode)));
	}

	public S3Object getPackagesZippedFolder(String languageCode)
	{
		return getPackagesZippedFolder(languageCode, null);
	}

	public S3Object getPackagesZippedFolder(String languageCode, String packageCode)
	{
		return s3Client.getObject(new GetObjectRequest(AmazonS3GodToolsConfig.BUCKET_NAME,
				AmazonS3GodToolsConfig.getPackagesKey(languageCode, packageCode)));
	}
}
