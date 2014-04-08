package org.cru.godtools.api.translations;

import org.cru.godtools.api.authentication.AuthorizationService;
import org.cru.godtools.api.packages.GodToolsResponseBuilder;
import org.cru.godtools.api.packages.domain.Version;
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

/**
 * Created by ryancarlson on 4/8/14.
 */
@Path("/translations")
public class TranslationResource
{
	@Inject
	AuthorizationService authService;
	@Inject
	GodToolsResponseBuilder responseBuilder;

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
	@Consumes("multipart/form-data")
	@Produces(MediaType.APPLICATION_XML)
	public Response createNewTranslation(@HeaderParam("authorization") String authTokenHeader,
										 @QueryParam("authorization") String authTokenParam,
										 MultipartFormDataInput input)
	{
		authService.checkAuthorization(authTokenParam, authTokenHeader);

		NewTranslationPostData newPackagePostData = new NewTranslationPostData(input);

		for(NewTranslation newPackage : newPackagePostData.getNewPackageSet())
		{
			System.out.println("Found a package!");
			for(String key : newPackage.keySet())
			{
				System.out.println("Including file: " + key);
			}
		}
		/*
		- consume zip file
		- identify contents.xml
		- parse packages

		 */
		return Response.created(null).build();
	}

	@PUT
	@Consumes(MediaType.APPLICATION_XML)
	public Response updateTranslation(@QueryParam("finished") String isFinished,
									  @HeaderParam("authentication") String authCode)
	{

		return Response.noContent().build();
	}

}
