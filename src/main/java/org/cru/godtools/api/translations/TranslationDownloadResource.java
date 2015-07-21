package org.cru.godtools.api.translations;

import com.amazonaws.services.s3.model.S3Object;
import org.ccci.util.time.Clock;
import org.cru.godtools.domain.authentication.AuthorizationRecord;
import org.cru.godtools.domain.authentication.AuthorizationService;
import org.cru.godtools.s3.GodToolsS3Client;
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

/**
 * Created by ryancarlson on 7/11/15.
 */
@Path("/translations")
public class TranslationDownloadResource
{

	@Inject
	AuthorizationService authService;

	@Inject
	GodToolsS3Client godToolsS3Client;

	@Inject
	Clock clock;

	private Logger log = Logger.getLogger(this.getClass());

	@GET
	@Path("/{language}")
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

		S3Object languagesZippedFolder = godToolsS3Client.getLanguagesZippedFolder(languageCode);

		return Response
				.ok(languagesZippedFolder.getObjectContent())
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
								   @HeaderParam("Authorization") String authTokenHeader,
								   @QueryParam("Authorization") String authTokenParam) throws IOException
	{
		AuthorizationRecord.checkAuthorization(authService.getAuthorizationRecord(authTokenParam, authTokenHeader), clock.currentDateTime());

		S3Object languagesZippedFolder = godToolsS3Client.getLanguagesZippedFolder(languageCode, packageCode);

		return Response
				.ok(languagesZippedFolder.getObjectContent())
				.type("application/zip")
				.build();

	}
}
