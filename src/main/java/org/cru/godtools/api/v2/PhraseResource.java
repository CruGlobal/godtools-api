package org.cru.godtools.api.v2;

import com.google.common.base.Optional;
import org.ccci.util.time.Clock;
import org.cru.godtools.api.translations.GodToolsTranslation;
import org.cru.godtools.api.v2.functions.PublishedTranslation;
import org.cru.godtools.domain.authentication.AuthorizationRecord;
import org.cru.godtools.domain.authentication.AuthorizationService;
import org.cru.godtools.domain.packages.PageStructure;
import org.cru.godtools.domain.packages.TranslationElement;
import org.cru.godtools.domain.packages.TranslationElementService;
import org.cru.godtools.domain.translations.TranslationService;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/v2/packages/{package}/pages/{pageName}/phrases")
public class PhraseResource
{
	@Inject
	AuthorizationService authorizationService;

	@Inject
	Clock clock;

	@Inject
	PublishedTranslation publishedTranslation;

	@Inject
	TranslationElementService translationElementService;

	@Inject
	TranslationService translationService;

	/**
	 * English is the base language for God Tools.  Phrases should always be manipulated relative to English
	 */
	private static final String BASE_LANGUAGE_CODE = "en";

	private static final Logger logger = Logger.getLogger(PhraseResource.class);

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response listPhrasesForPage(@HeaderParam("Authorization") String authorization,
									   @PathParam("package") String packageCode,
									   @PathParam("pageName") String pageName)
	{
		logger.info(String.format("Listing phrases w/ authorization %s", authorization));

		AuthorizationRecord.checkAdminAccess(authorizationService.getAuthorizationRecord(null, authorization), clock.currentDateTime());

		logger.info(String.format("Listing phrases, admin validated.  Listing phrases for %s-%s", packageCode, pageName));

		Optional<GodToolsTranslation> translationOptional = publishedTranslation.retrieve(BASE_LANGUAGE_CODE, packageCode, false);

		if(!translationOptional.isPresent())
		{
			logger.warn("Something weird... no published English translation for: " + packageCode);

			return Response.status(Response.Status.BAD_REQUEST).build();
		}

		Optional<PageStructure> pageOptional = translationOptional.get().getPage(pageName);

		if(!pageOptional.isPresent())
		{
			logger.warn(String.format("Listing phrases, Page %s not found English translation for %s", pageName, packageCode));

			return Response.status(Response.Status.NOT_FOUND).build();
		}

		List<TranslationElement> translationElements = translationElementService.selectByTranslationIdPageStructureId(translationOptional.get().getTranslation().getId(),
				pageOptional.get().getId());

		return Response
				.ok(translationElements)
				.build();
	}

	@POST
	public Response addPhraseToPackage()
	{
		return Response.status(Response.Status.NOT_IMPLEMENTED).build();
	}

	@DELETE
	@Path("/{id}")
	public Response removePhraseFromPackage()
	{
		return Response.status(Response.Status.NOT_IMPLEMENTED).build();
	}
}
