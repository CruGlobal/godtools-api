package org.cru.godtools.api.translations;

import org.ccci.util.time.Clock;
import org.cru.godtools.api.translations.config.Config;
import org.cru.godtools.domain.authentication.AuthorizationRecord;
import org.cru.godtools.domain.authentication.AuthorizationService;
import org.cru.godtools.domain.languages.LanguageCode;
import org.cru.godtools.domain.packages.PageStructure;
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
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created by ryancarlson on 7/28/14.
 */

@Path("/drafts")
public class DraftResource
{
	@Inject
	AuthorizationService authService;
	@Inject
	GodToolsTranslationRetrieval translationRetrievalProcess;
	@Inject
	GodToolsTranslationService godToolsTranslationService;
	@Inject private Clock clock;

	private Logger log = Logger.getLogger(DraftResource.class);
	static boolean BYPASS_ASYNC_UPDATE = false;

	@GET
	@Path("/{language}")
	@Produces({"application/zip", "application/xml", "application/json"})
	public Response getTranslations(@PathParam("language") String languageCode,
									@QueryParam("interpreter") Integer minimumInterpreterVersionParam,
									@HeaderParam("interpreter") Integer minimumInterpreterVersionHeader,
									@QueryParam("compressed") String compressed,
									@HeaderParam("Authorization") String authTokenHeader,
									@QueryParam("Authorization") String authTokenParam) throws IOException
	{
		log.info("Requesting all drafts for language: " + languageCode);

		AuthorizationRecord.checkAccessToDrafts(authService.getAuthorizationRecord(authTokenParam, authTokenHeader), clock.currentDateTime());

		return translationRetrievalProcess
				.setLanguageCode(languageCode)
				.setMinimumInterpreterVersion(minimumInterpreterVersionHeader == null ? minimumInterpreterVersionParam : minimumInterpreterVersionHeader)
				.setCompressed(Boolean.parseBoolean(compressed))
				.loadDrafts()
				.scheduleAsynchronousDraftUpdates()
				.buildResponse();
	}

	@GET
	@Path("/{language}/{package}")
	@Produces({"application/zip", "application/xml", "application/json"})
	public Response getTranslation(@PathParam("language") String languageCode,
								   @PathParam("package") String packageCode,
								   @QueryParam("interpreter") Integer minimumInterpreterVersionParam,
								   @HeaderParam("interpreter") Integer minimumInterpreterVersionHeader,
								   @QueryParam("compressed") String compressed,
								   @QueryParam("version") BigDecimal versionNumber,
								   @HeaderParam("Authorization") String authTokenHeader,
								   @QueryParam("Authorization") String authTokenParam) throws IOException
	{
		log.info("Requesting draft translation for package: " + packageCode + " and language: " + languageCode);

		AuthorizationRecord.checkAccessToDrafts(authService.getAuthorizationRecord(authTokenParam, authTokenHeader), clock.currentDateTime());

		return translationRetrievalProcess
				.setLanguageCode(languageCode)
				.setPackageCode(packageCode)
				.setMinimumInterpreterVersion(minimumInterpreterVersionHeader == null ? minimumInterpreterVersionParam : minimumInterpreterVersionHeader)
				.setCompressed(Boolean.parseBoolean(compressed))
				.loadDrafts()
				.scheduleAsynchronousDraftUpdates()
				.buildResponse();
	}

	@GET
	@Path("/{language}/{package}/config")
	@Produces({"application/xml", "application/json"})
	public Response getConfig(@PathParam("language") String languageCode,
							@PathParam("package") String packageCode,
							@QueryParam("interpreter") Integer minimumInterpreterVersionParam,
							@HeaderParam("interpreter") Integer minimumInterpreterVersionHeader,
							@QueryParam("compressed") String compressed,
							@HeaderParam("Authorization") String authTokenHeader,
							@QueryParam("Authorization") String authTokenParam) throws IOException
	{
		log.info("Requesting config.xml for package: " + packageCode + " and language: " + languageCode);

		AuthorizationRecord.checkAccessToDrafts(authService.getAuthorizationRecord(authTokenParam, authTokenHeader), clock.currentDateTime());

		// page already has latest translation elements applied, and images too.
		Config configFile = godToolsTranslationService.getConfig(packageCode, new LanguageCode(languageCode));

		return Response.ok(configFile).build();
	}

	@GET
	@Path("/{language}/{package}/pages/{pageId}")
	@Produces({"application/zip", "application/xml"})
	public Response getPage(@PathParam("language") String languageCode,
							@PathParam("package") String packageCode,
							@PathParam("pageId") UUID pageId,
							@QueryParam("interpreter") Integer minimumInterpreterVersionParam,
							@HeaderParam("interpreter") Integer minimumInterpreterVersionHeader,
							@QueryParam("compressed") String compressed,
							@HeaderParam("Authorization") String authTokenHeader,
							@QueryParam("Authorization") String authTokenParam) throws IOException
	{
		log.info("Requesting draft page update for package: " + packageCode + " and language: " + languageCode + " and page ID: " + pageId);

		AuthorizationRecord.checkAccessToDrafts(authService.getAuthorizationRecord(authTokenParam, authTokenHeader), clock.currentDateTime());

		// page already has latest translation elements applied, and images too.
		PageStructure draftPage = godToolsTranslationService.getPage(new LanguageCode(languageCode),pageId);

		return translationRetrievalProcess
				.setCompressed(Boolean.parseBoolean(compressed))
				.buildSinglePageResponse(draftPage);
	}
}
