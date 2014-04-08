package org.cru.godtools.api.translations;

import org.cru.godtools.api.authentication.AuthorizationService;
import org.cru.godtools.api.packages.GodToolsGETResponseBuilder;
import org.cru.godtools.api.packages.domain.Version;
import org.cru.godtools.api.packages.utils.LanguageCode;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
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
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by ryancarlson on 4/8/14.
 */
@Path("/translations")
public class TranslationResource
{
	@Inject
	AuthorizationService authService;
	@Inject
	GodToolsGETResponseBuilder responseBuilder;
	@Inject
	GodToolsPOSTResponseBuilder postResponseBuilder;


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

		return responseBuilder
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
								   @QueryParam("revision-number") Integer revisionNumber,
								   @HeaderParam("authorization") String authTokenHeader,
								   @QueryParam("authorization") String authTokenParam) throws IOException
	{
		authService.checkAuthorization(authTokenParam, authTokenHeader);

		return responseBuilder
				.setLanguageCode(languageCode)
				.setPackageCode(packageCode)
				.setMinimumInterpreterVersion(minimumInterpreterVersionHeader == null ? minimumInterpreterVersionParam : minimumInterpreterVersionHeader)
				.setCompressed(Boolean.parseBoolean(compressed))
				.setVersionNumber(revisionNumber == null ? Version.LATEST_VERSION_NUMBER : revisionNumber)
				.loadTranslations()
				.buildResponse();
	}

	@POST
	@Path("/{language}/{package}")
	@Consumes("multipart/form-data")
	@Produces(MediaType.APPLICATION_XML)
	public Response createNewTranslation(@PathParam("language") String languageCode,
										 @PathParam("package") String packageCode,
										 @HeaderParam("authorization") String authTokenHeader,
										 @QueryParam("authorization") String authTokenParam,
										 MultipartFormDataInput input) throws URISyntaxException
	{
		authService.checkAuthorization(authTokenParam, authTokenHeader);

		NewTranslationPostData newPackagePostData = new NewTranslationPostData(input);

		for(NewTranslation newTranslation : newPackagePostData.getNewPackageSet())
		{
			return postResponseBuilder
					.setPackageCode(packageCode)
					.setLanguageCode(languageCode)
					.loadVersion()
					.saveTranslation(newTranslation)
					.buildResponse();
		}

		return Response.accepted().build();
	}

	@PUT
	@Consumes(MediaType.APPLICATION_XML)
	public Response updateTranslation(@QueryParam("finished") String isFinished,
									  @HeaderParam("authentication") String authCode)
	{

		return Response.noContent().build();
	}

}
