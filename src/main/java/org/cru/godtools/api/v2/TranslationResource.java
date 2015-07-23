package org.cru.godtools.api.v2;

import org.ccci.util.time.Clock;
import org.cru.godtools.api.v2.functions.TranslationDraft;
import org.cru.godtools.domain.authentication.AuthorizationRecord;
import org.cru.godtools.domain.authentication.AuthorizationService;
import org.cru.godtools.s3.AmazonS3GodToolsConfig;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

@Path("/v2/translations")
public class TranslationResource
{
	@Inject
	AuthorizationService authService;

	@Inject
	TranslationDraft translationDraft;

	@Inject
	Clock clock;

	private Logger log = Logger.getLogger(this.getClass());

	@GET
	@Path("/{language}")
	public Response getAllPackagesForLanguage(@PathParam("language") String languageCode) throws MalformedURLException
	{
		log.info("Requesting all packages for language: " + languageCode);

		return Response
				.status(Response.Status.MOVED_PERMANENTLY)
				.header("location", AmazonS3GodToolsConfig.getLanguagesRedirectUrl(languageCode))
				.build();
	}

	@GET
	@Path("/{language}/{package}")
	public Response getPackage(@PathParam("language") String languageCode,
							   @PathParam("package") String packageCode) throws MalformedURLException
	{
		log.info("Requesting package " + packageCode + " for language: " + languageCode);

		return Response
				.status(Response.Status.MOVED_PERMANENTLY)
				.header("location", AmazonS3GodToolsConfig.getLanguagesRedirectUrl(languageCode, packageCode))
				.build();
	}

	@POST
	@Path("/{language}/{package}")
	public Response createTranslation(@PathParam("language") String languageCode,
									  @PathParam("package") String packageCode,
									  @HeaderParam("Authorization") String authTokenHeader,
									  @QueryParam("Authorization") String authTokenParam) throws URISyntaxException
	{
		log.info("Creating new translation for package: " + packageCode + " and language: " + languageCode);

		AuthorizationRecord.checkAccessToDrafts(authService.getAuthorizationRecord(authTokenParam, authTokenHeader), clock.currentDateTime());

		translationDraft.create(languageCode, packageCode);

		return Response
				.created(new URI("/drafts" + languageCode + "/" + packageCode))
				.build();
	}

	@PUT
	@Path("/{language}/{package}")
	public Response updateTranslation(@PathParam("language") String languageCode,
									  @PathParam("package") String packageCode,
									  @HeaderParam("Authorization") String authTokenHeader,
									  @QueryParam("Authorization") String authTokenParam,
									  @QueryParam("publish") String publish)
	{
		log.info("Updating translation for package: " + packageCode + " and language: " + languageCode);

		AuthorizationRecord.checkAccessToDrafts(authService.getAuthorizationRecord(authTokenParam, authTokenHeader), clock.currentDateTime());

		if(Boolean.parseBoolean(publish))
		{
			translationDraft.publish(languageCode, packageCode);
		}

		return Response.accepted().build();
	}
}
