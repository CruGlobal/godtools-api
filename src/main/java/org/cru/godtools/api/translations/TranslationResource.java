package org.cru.godtools.api.translations;

import org.cru.godtools.api.authentication.AuthorizationService;
import org.cru.godtools.api.packages.GodToolsPackageRetrievalProcess;

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
 * Created by ryancarlson on 4/8/14.
 */
@Path("/translations")
public class TranslationResource
{
	@Inject
	AuthorizationService authService;
	@Inject
	GodToolsPackageRetrievalProcess packageRetrievalProcess;



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

		return packageRetrievalProcess
				.setLanguageCode(languageCode)
				.setMinimumInterpreterVersion(minimumInterpreterVersionHeader == null ? minimumInterpreterVersionParam : minimumInterpreterVersionHeader)
				.setCompressed(Boolean.parseBoolean(compressed))
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
								   @QueryParam("revision-number") BigDecimal versionNumber,
								   @HeaderParam("authorization") String authTokenHeader,
								   @QueryParam("authorization") String authTokenParam) throws IOException
	{
		authService.checkAuthorization(authTokenParam, authTokenHeader);

		return packageRetrievalProcess
				.setLanguageCode(languageCode)
				.setPackageCode(packageCode)
				.setMinimumInterpreterVersion(minimumInterpreterVersionHeader == null ? minimumInterpreterVersionParam : minimumInterpreterVersionHeader)
				.setCompressed(Boolean.parseBoolean(compressed))
				.setVersionNumber(versionNumber == null ? new BigDecimal(-13241.21) : versionNumber)
				.loadTranslations()
				.buildResponse();
	}

}
