package org.cru.godtools.api.translations;

import org.cru.godtools.domain.authentication.AuthorizationService;
import org.cru.godtools.domain.authentication.UnauthorizedException;
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

/**
 * Created by ryancarlson on 7/28/14.
 */

@Path("/drafts")
public class DraftResource
{
	@Inject
	AuthorizationService authService;
	@Inject
	GodToolsTranslationRetrievalProcess translationRetrievalProcess;
	@Inject
	GodToolsTranslationService godToolsTranslationService;

	private Logger log = Logger.getLogger(DraftResource.class);

	@GET
	@Path("/{language}")
	@Produces({"application/zip", "application/xml"})
	public Response getTranslations(@PathParam("language") String languageCode,
									@QueryParam("interpreter") Integer minimumInterpreterVersionParam,
									@HeaderParam("interpreter") Integer minimumInterpreterVersionHeader,
									@QueryParam("compressed") String compressed,
									@HeaderParam("Authorization") String authTokenHeader,
									@QueryParam("Authorization") String authTokenParam) throws IOException
	{
		log.info("Requesting all drafts for language: " + languageCode);

		if(authService.canAccessOrCreateDrafts(authTokenParam, authTokenHeader))
		{
			return translationRetrievalProcess
					.setLanguageCode(languageCode)
					.setMinimumInterpreterVersion(minimumInterpreterVersionHeader == null ? minimumInterpreterVersionParam : minimumInterpreterVersionHeader)
					.setCompressed(Boolean.parseBoolean(compressed))
					.loadDrafts()
					.buildResponse();
		}
		else
		{
			throw new UnauthorizedException();
		}
	}

	@GET
	@Path("/{language}/{package}")
	@Produces({"application/zip", "application/xml"})
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

		if(authService.canAccessOrCreateDrafts(authTokenParam, authTokenHeader))
		{
			return translationRetrievalProcess
					.setLanguageCode(languageCode)
					.setPackageCode(packageCode)
					.setMinimumInterpreterVersion(minimumInterpreterVersionHeader == null ? minimumInterpreterVersionParam : minimumInterpreterVersionHeader)
					.setCompressed(Boolean.parseBoolean(compressed))
					.loadDrafts()
					.buildResponse();
		}
		else
		{
			throw new UnauthorizedException();
		}
	}
}
