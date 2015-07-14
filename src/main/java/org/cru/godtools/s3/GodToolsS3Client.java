package org.cru.godtools.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import org.jboss.logging.Logger;

import javax.inject.Inject;

/**
 * Created by ryancarlson on 7/11/15.
 */
public class GodToolsS3Client
{
	@Inject
	AmazonS3Client s3Client;

	private Logger log = Logger.getLogger(this.getClass());

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
		String metaKey = AmazonS3GodToolsConfig.getMetaKey(languageCode, packageCode);

		log.info(String.format("Getting meta info file w/ key %s", metaKey));

		return s3Client.getObject(new GetObjectRequest(AmazonS3GodToolsConfig.BUCKET_NAME,
				metaKey));
	}

	public S3Object getLanguagesZippedFolder(String languageCode)
	{
		return getLanguagesZippedFolder(languageCode, null);
	}

	public S3Object getLanguagesZippedFolder(String languageCode, String packageCode)
	{
		String languagesKey = AmazonS3GodToolsConfig.getLanguagesKey(languageCode, packageCode);

		log.info(String.format("Getting languages file w/ key %s", languagesKey));

		return s3Client.getObject(new GetObjectRequest(AmazonS3GodToolsConfig.BUCKET_NAME,
				languagesKey));
	}

	public S3Object getPackagesZippedFolder(String languageCode)
	{
		return getPackagesZippedFolder(languageCode, null);
	}

	public S3Object getPackagesZippedFolder(String languageCode, String packageCode)
	{
		String packagesKey = AmazonS3GodToolsConfig.getPackagesKey(languageCode, packageCode);

		log.info(String.format("Getting packages file w/ key %s", packagesKey));

		return s3Client.getObject(new GetObjectRequest(AmazonS3GodToolsConfig.BUCKET_NAME,
				packagesKey));
	}
}
