package org.cru.godtools.api.translations;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import org.cru.godtools.domain.authentication.AuthorizationRecord;
import org.cru.godtools.domain.authentication.AuthorizationService;
import org.cru.godtools.s3.AmazonS3GodToolsConfig;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

/**
 * Created by ryancarlson on 7/11/15.
 */
public class TranslationDownloadResource
{

	@Inject
	AuthorizationService authService;

	private Logger log = Logger.getLogger(this.getClass());

	@GET
	@Path("/translations/{language}")
	@Produces({"application/zip"})
	public Response getTranslations(@PathParam("language") String languageCode,
									@QueryParam("interpreter") Integer minimumInterpreterVersionParam,
									@HeaderParam("interpreter") Integer minimumInterpreterVersionHeader,
									@QueryParam("compressed") String compressed,
									@HeaderParam("Authorization") String authTokenHeader,
									@QueryParam("Authorization") String authTokenParam) throws IOException
	{
		log.info("Requesting all translations for language: " + languageCode);

		AuthorizationRecord.checkAuthorization(authService.getAuthorizationRecord(authTokenParam, authTokenHeader), clock.currentDateTime());

		AmazonS3 s3Client = new AmazonS3Client(new ProfileCredentialsProvider());

		S3Object object = s3Client.getObject(new GetObjectRequest(AmazonS3GodToolsConfig.BUCKET_NAME,
				AmazonS3GodToolsConfig.getLanguagesKey(languageCode, null)));

		InputStream zippedLanguagesStream = (InputStream) object.getObjectContent();

		return Response
				.ok(zippedLanguagesStream)
				.type("application/zip")
				.build();
	}

	@GET
	@Path("/translations/{language}/{package}")
	@Produces({"application/zip"})
	public Response getTranslation(@PathParam("language") String languageCode,
								   @PathParam("package") String packageCode,
								   @QueryParam("interpreter") Integer minimumInterpreterVersionParam,
								   @HeaderParam("interpreter") Integer minimumInterpreterVersionHeader,
								   @QueryParam("compressed") String compressed,
								   @QueryParam("version") BigDecimal versionNumber,
								   @HeaderParam("Authorization") String authTokenHeader,
								   @QueryParam("Authorization") String authTokenParam) throws IOException
	{
		AuthorizationRecord.checkAuthorization(authService.getAuthorizationRecord(authTokenParam, authTokenHeader), clock.currentDateTime());

		AmazonS3 s3Client = new AmazonS3Client(new ProfileCredentialsProvider());

		S3Object object = s3Client.getObject(new GetObjectRequest(AmazonS3GodToolsConfig.BUCKET_NAME,
				AmazonS3GodToolsConfig.getLanguagesKey(languageCode, packageCode)));

		InputStream zippedLanguagesStream = (InputStream) object.getObjectContent();

		return Response
				.ok(zippedLanguagesStream)
				.type("application/zip")
				.build();
	}
}
