package org.cru.godtools.api.meta;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import org.ccci.util.time.Clock;
import org.cru.godtools.domain.authentication.AuthorizationRecord;
import org.cru.godtools.domain.authentication.AuthorizationService;
import org.cru.godtools.s3.AmazonS3GodToolsConfig;
import org.jboss.logging.Logger;
import org.xml.sax.SAXException;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by ryancarlson on 3/14/14.
 */

@Path("/meta")
public class MetaResource
{
	@Inject
	AuthorizationService authService;

	@Inject
	Clock clock;

	private Logger log = Logger.getLogger(MetaResource.class);

	@GET
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response getAllMetaInfo(@QueryParam("interpreter") Integer minimumInterpreterVersionParam,
										@HeaderParam("interpreter") Integer minimumInterpreterVersionHeader,
										@QueryParam("Authorization") String authCodeParam,
										@HeaderParam("Authorization") String authCodeHeader) throws ParserConfigurationException, SAXException, IOException
	{
		log.info("Getting all meta info");

		AuthorizationRecord.checkAuthorization(authService.getAuthorizationRecord(authCodeParam, authCodeHeader), clock.currentDateTime());

		AmazonS3 s3Client = new AmazonS3Client(new ProfileCredentialsProvider());

		S3Object object = s3Client.getObject(new GetObjectRequest(AmazonS3GodToolsConfig.BUCKET_NAME,
				AmazonS3GodToolsConfig.getMetaKey(null, null)));

		InputStream metaStream = (InputStream) object.getObjectContent();

		return Response
				.ok(metaStream)
				.type("application/zip")
				.build();
	}

	@GET
	@Path("/{language}")
	@Produces(MediaType.APPLICATION_XML)
	public Response getLanguageMetaInfo(@PathParam("language") String languageCode,
								@QueryParam("interpreter") Integer minimumInterpreterVersionParam,
								@HeaderParam("interpreter") Integer minimumInterpreterVersionHeader,
								@QueryParam("Authorization") String authCodeParam,
								@HeaderParam("Authorization") String authCodeHeader)
	{
		log.info("Getting all meta info for language: " + languageCode);

		AuthorizationRecord.checkAuthorization(authService.getAuthorizationRecord(authCodeParam, authCodeHeader), clock.currentDateTime());

		AmazonS3 s3Client = new AmazonS3Client(new ProfileCredentialsProvider());

		S3Object object = s3Client.getObject(new GetObjectRequest(AmazonS3GodToolsConfig.BUCKET_NAME,
				AmazonS3GodToolsConfig.getMetaKey(languageCode, null)));

		InputStream metaStream = (InputStream) object.getObjectContent();

		return Response
				.ok(metaStream)
				.type("application/zip")
				.build();
	}

	@GET
	@Path("/{language}/{package}")
	@Produces(MediaType.APPLICATION_XML)
	public Response getLanguageAndPackageMetaInfo(@PathParam("language") String languageCode,
								@PathParam("package") String packageCode,
								@QueryParam("interpreter") Integer minimumInterpreterVersionParam,
								@HeaderParam("interpreter") Integer minimumInterpreterVersionHeader,
								@QueryParam("Authorization") String authCodeParam,
								@HeaderParam("Authorization") String authCodeHeader) throws ParserConfigurationException, SAXException, IOException
	{
		log.info("Getting all meta info for package: " + packageCode + " language: " + languageCode);

		AuthorizationRecord.checkAuthorization(authService.getAuthorizationRecord(authCodeParam, authCodeHeader), clock.currentDateTime());

		AmazonS3 s3Client = new AmazonS3Client(new ProfileCredentialsProvider());

		S3Object object = s3Client.getObject(new GetObjectRequest(AmazonS3GodToolsConfig.BUCKET_NAME,
				AmazonS3GodToolsConfig.getMetaKey(languageCode, packageCode)));

		InputStream metaStream = (InputStream) object.getObjectContent();

		return Response
				.ok(metaStream)
				.type("application/zip")
				.build();
	}
}
