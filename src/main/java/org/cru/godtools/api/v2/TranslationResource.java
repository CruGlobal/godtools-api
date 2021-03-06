package org.cru.godtools.api.v2;

import com.google.common.base.Optional;
import org.ccci.util.time.Clock;
import org.cru.godtools.api.translations.GodToolsTranslation;
import org.cru.godtools.api.v2.functions.DraftTranslation;
import org.cru.godtools.api.v2.functions.PublishedTranslation;
import org.cru.godtools.domain.authentication.AuthorizationRecord;
import org.cru.godtools.domain.authentication.AuthorizationService;
import org.cru.godtools.domain.languages.LanguageCode;
import org.cru.godtools.s3.AmazonS3GodToolsConfig;
import org.cru.godtools.s3.GodToolsS3Client;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
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
	PublishedTranslation publishedTranslation;

	@Inject
	DraftTranslation draftTranslation;

	@Inject
	GodToolsS3Client godToolsS3Client;

	@Inject
	Clock clock;

	private Logger log = Logger.getLogger(this.getClass());

	@GET
	@Path("/{language}")
	public Response getAllPackagesForLanguage(@PathParam("language") String languageCode) throws MalformedURLException
	{
		log.info("Requesting all packages for language: " + languageCode);

		return Response
				.status(Response.Status.NOT_FOUND)
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
				.header("location", AmazonS3GodToolsConfig.getTranslationsRedirectUrl(languageCode, packageCode))
				.build();
	}

	@GET
	@Path("/{language}/{package}/pages")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPages(@PathParam("language") String languageCode,
								@PathParam("package") String packageCode) throws MalformedURLException
	{
		log.info("Requesting pages for package: " + packageCode + " for language: " + languageCode);

		Optional<GodToolsTranslation> godToolsTranslationOptional = publishedTranslation.retrieve(languageCode, packageCode, false);

		if(!godToolsTranslationOptional.isPresent())
		{
			return Response
					.status(Response.Status.NOT_FOUND)
					.build();
		}

		return Response
				.ok(godToolsTranslationOptional.get().getPageStructureList())
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

		draftTranslation.create(new LanguageCode(languageCode), packageCode);

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
			draftTranslation.publish(languageCode, packageCode);
			publishedTranslation.pushToS3(languageCode, packageCode);
		}

		return Response.accepted().build();
	}
}
