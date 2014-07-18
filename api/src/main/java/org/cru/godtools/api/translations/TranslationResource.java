package org.cru.godtools.api.translations;

import org.cru.godtools.domain.authentication.AuthorizationService;
import org.cru.godtools.domain.authentication.UnauthorizedException;
import org.cru.godtools.domain.GodToolsVersion;
import org.cru.godtools.domain.languages.LanguageCode;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.math.BigDecimal;

/**
 * Contains RESTful endpoints for delivering GodTools "translation" resources.
 *  - "translation" include translation XML files, but not images
 *
 * For more information: https://github.com/CruGlobal/godtools-api/wiki/The-Translations-Endpoint
 *
 * Created by ryancarlson on 4/8/14.
 */
@Path("/translations")
public class TranslationResource
{
	@Inject
	AuthorizationService authService;
	@Inject
	GodToolsTranslationRetrievalProcess translationRetrievalProcess;
	@Inject
	GodToolsTranslationService godToolsTranslationService;


	@GET
	@Path("/{language}")
	@Produces({"application/zip", "application/xml"})
	public Response getTranslations(@PathParam("language") String languageCode,
									@QueryParam("interpreter") Integer minimumInterpreterVersionParam,
									@HeaderParam("interpreter") Integer minimumInterpreterVersionHeader,
									@QueryParam("compressed") String compressed,
									@HeaderParam("authorization") String authTokenHeader,
									@QueryParam("authorization") String authTokenParam) throws IOException
	{
		authService.checkAuthorization(authTokenParam, authTokenHeader);

		return translationRetrievalProcess
				.setLanguageCode(languageCode)
				.setMinimumInterpreterVersion(minimumInterpreterVersionHeader == null ? minimumInterpreterVersionParam : minimumInterpreterVersionHeader)
				.setCompressed(Boolean.parseBoolean(compressed))
				.setIncludeDrafts(authService.canAccessOrCreateDrafts(authTokenParam, authTokenHeader))
				.loadTranslations()
				.buildResponse();
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
								   @HeaderParam("authorization") String authTokenHeader,
								   @QueryParam("authorization") String authTokenParam) throws IOException
	{
		authService.checkAuthorization(authTokenParam, authTokenHeader);

		return translationRetrievalProcess
				.setLanguageCode(languageCode)
				.setPackageCode(packageCode)
				.setMinimumInterpreterVersion(minimumInterpreterVersionHeader == null ? minimumInterpreterVersionParam : minimumInterpreterVersionHeader)
				.setCompressed(Boolean.parseBoolean(compressed))
				.setIncludeDrafts(authService.canAccessOrCreateDrafts(authTokenParam, authTokenHeader))
				.setVersionNumber(versionNumber == null ? GodToolsVersion.LATEST_VERSION : new GodToolsVersion(versionNumber))
				.loadTranslations()
				.buildResponse();
	}

	@POST
	@Path("/{language}/{package}")
	public Response createTranslation(@PathParam("language") String languageCode,
									  @PathParam("package") String packageCode,
									  @QueryParam("interpreter") Integer minimumInterpreterVersionParam,
									  @HeaderParam("interpreter") Integer minimumInterpreterVersionHeader,
									  @HeaderParam("authorization") String authTokenHeader,
									  @QueryParam("authorization") String authTokenParam)
	{
		authService.checkAuthorization(authTokenParam, authTokenHeader);
		if(!authService.canAccessOrCreateDrafts(authTokenParam, authTokenHeader)) throw new UnauthorizedException();

		godToolsTranslationService.setupNewTranslation(new LanguageCode(languageCode), packageCode);

		return Response.accepted().build();
	}

	@PUT
	@Path("/{language}/{package}")
	public Response updateTranslation(@PathParam("language") String languageCode,
									  @PathParam("package") String packageCode,
									  @HeaderParam("authorization") String authTokenHeader,
									  @QueryParam("authorization") String authTokenParam,
									  @QueryParam("publish") String publish)
	{
		authService.checkAuthorization(authTokenParam, authTokenHeader);
		if(!authService.canAccessOrCreateDrafts(authTokenParam, authTokenHeader)) throw new UnauthorizedException();

		return Response.noContent().build();
	}
}
