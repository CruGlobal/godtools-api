package org.cru.godtools.api.v2;

import com.google.common.base.Optional;
import org.ccci.util.time.Clock;
import org.cru.godtools.api.translations.GodToolsTranslation;
import org.cru.godtools.api.v2.functions.TranslationDraft;
import org.cru.godtools.api.v2.functions.TranslationPackager;
import org.cru.godtools.domain.authentication.AuthorizationRecord;
import org.cru.godtools.domain.authentication.AuthorizationService;
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
import java.util.List;

@Path("/v2/drafts")
public class DraftResource
{

	@Inject
	AuthorizationService authService;

	@Inject
	TranslationDraft translationDraft;

	@Inject
	Clock clock;

	private Logger log = Logger.getLogger(getClass());

	@GET
	@Path("/{language}")
	@Produces("application/zip")
	public Response getTranslations(@PathParam("language") String languageCode,
									@HeaderParam("Authorization") String authTokenHeader,
									@QueryParam("Authorization") String authTokenParam) throws IOException
	{
		log.info("Requesting all drafts for language: " + languageCode);

		AuthorizationRecord.checkAccessToDrafts(authService.getAuthorizationRecord(authTokenParam, authTokenHeader), clock.currentDateTime());

		TranslationPackager packager = new TranslationPackager();

		return Response
				.ok(packager.compress(translationDraft.retrieve(languageCode)))
				.build();
	}

	@GET
	@Path("/{language}/{package}")
	@Produces("application/zip")
	public Response getTranslation(@PathParam("language") String languageCode,
								   @PathParam("package") String packageCode,
								   @HeaderParam("Authorization") String authTokenHeader,
								   @QueryParam("Authorization") String authTokenParam) throws IOException
	{
		log.info("Requesting draft translation for package: " + packageCode + " and language: " + languageCode);

		AuthorizationRecord.checkAccessToDrafts(authService.getAuthorizationRecord(authTokenParam, authTokenHeader), clock.currentDateTime());

		TranslationPackager packager = new TranslationPackager();

		Optional<GodToolsTranslation> godToolsTranslationOptional = translationDraft.retrieve(languageCode, packageCode);

		if(godToolsTranslationOptional.isPresent())
		{
			return Response
					.ok(packager.compress(godToolsTranslationOptional.get()))
					.build();
		}
		else
		{
			return Response
					.status(Response.Status.NOT_FOUND)
					.build();
		}
	}
}
