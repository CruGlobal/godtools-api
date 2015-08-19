package org.cru.godtools.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import org.cru.godtools.api.meta.MetaResults;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;

/**
 * Created by ryancarlson on 7/11/15.
 */
public class GodToolsS3Client
{
	@Inject
	AmazonS3Client s3Client;

	private Logger log = Logger.getLogger(this.getClass());

	private static final String GODTOOLS_BUCKET = "cru-godtools";

	public S3Object getMetaFile(MediaType mediaType)
	{
		String metaKey = AmazonS3GodToolsConfig.getMetaKeyV2(mediaType);

		log.info(String.format("Getting meta info file w/ key %s", metaKey));

		try
		{
			return s3Client.getObject(new GetObjectRequest(AmazonS3GodToolsConfig.BUCKET_NAME, metaKey));
		}
		catch(AmazonS3Exception amazonException)
		{
			if(amazonException.getStatusCode() == 404)
			{
				throw new NotFoundException();
			}

			else throw amazonException;
		}
	}

	public S3Object getTranslationZippedFolder(String languageCode)
	{
		String key = AmazonS3GodToolsConfig.getTranslationsKeyV2(languageCode);

		log.info(String.format("Getting translation file w/ key %s", key));

		return s3Client.getObject(new GetObjectRequest(AmazonS3GodToolsConfig.BUCKET_NAME, key));
	}

	public S3Object getTranslationZippedFolder(String languageCode, String packageCode)
	{
		String key = AmazonS3GodToolsConfig.getTranslationsKeyV2(languageCode, packageCode);

		log.info(String.format("Getting translation file w/ key %s", key));

		return s3Client.getObject(new GetObjectRequest(AmazonS3GodToolsConfig.BUCKET_NAME, key));
	}

	public S3Object getPackagesZippedFolder(String languageCode)
	{
		String packagesKey = AmazonS3GodToolsConfig.getPackagesKeyV2(languageCode);

		log.info(String.format("Getting packages file w/ key %s", packagesKey));

		return s3Client.getObject(new GetObjectRequest(AmazonS3GodToolsConfig.BUCKET_NAME, packagesKey));
	}

	public void pushPackagesZippedFolder(String languageCode, InputStream compressedTranslation)
	{
		String packagesKey = AmazonS3GodToolsConfig.getPackagesKeyV2(languageCode);

		log.info(String.format("Pushing packages file w/ key %s", packagesKey));

		ObjectMetadata metadata = new ObjectMetadata();

		PutObjectRequest putObjectRequest = new PutObjectRequest(GODTOOLS_BUCKET, packagesKey, compressedTranslation, metadata)
				.withCannedAcl(CannedAccessControlList.PublicRead); // God Tools packages are meant to be downloaded w/o authz

		s3Client.putObject(putObjectRequest);
	}

	/**
	 * Pushes an XML and a JSON serialized version of the meta file
	 */
	public void pushMetaFile(MetaResults metaResults)
	{
		log.info("Pushing meta files");

		s3Client.putObject(buildMetaPutRequest(MediaType.APPLICATION_XML_TYPE, metaResults.asXmlStream()));

		s3Client.putObject(buildMetaPutRequest(MediaType.APPLICATION_JSON_TYPE, metaResults.asJsonStream()));
	}

	private PutObjectRequest buildMetaPutRequest(MediaType mediaType, InputStream metaResultsStream)
	{
		return new PutObjectRequest(GODTOOLS_BUCKET,
				AmazonS3GodToolsConfig.getMetaKeyV2(mediaType),
				metaResultsStream,
				new ObjectMetadata())
				.withCannedAcl(CannedAccessControlList.PublicRead);
	}

	public void pushTranslationsZippedFile(String languageCode, String packageCode, InputStream languageFile)
	{
		String translationKey = AmazonS3GodToolsConfig.getTranslationsAndPackageKeyV2(languageCode, packageCode);

		log.info(String.format("pushing %s (text only) file for language %s", packageCode, languageCode));

		ObjectMetadata metadata = new ObjectMetadata();

		PutObjectRequest putObjectRequest = new PutObjectRequest(GODTOOLS_BUCKET, translationKey, languageFile, metadata)
				.withCannedAcl(CannedAccessControlList.PublicRead);

		s3Client.putObject(putObjectRequest);
	}
}
