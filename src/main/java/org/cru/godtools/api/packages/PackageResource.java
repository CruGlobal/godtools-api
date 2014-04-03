package org.cru.godtools.api.packages;

import org.cru.godtools.api.authentication.AuthorizationService;
import org.cru.godtools.api.packages.domain.PixelDensity;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.xml.sax.SAXException;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * Created by ryancarlson on 3/14/14.
 */

@Path("/packages")
public class PackageResource
{

    @Inject
    GodToolsResponseAssemblyProcess packageProcess;
    @Inject
    AuthorizationService authService;

    @GET
    @Produces({"application/zip", "application/xml"})
    public Response getPackages(@QueryParam("language") String languageCode,
                                @QueryParam("package") String packageCode,
                                @QueryParam("interpreter") Integer minimumInterpreterVersionParam,
                                @HeaderParam("interpreter") Integer minimumInterpreterVersionHeader,
                                @QueryParam("compressed") String compressed,
                                @QueryParam("revision-number") Integer revisionNumber,
                                @QueryParam("density") String desiredPixelDensity,
                                @HeaderParam("authorization") String authTokenHeader,
                                @QueryParam("authorization") String authTokenParam) throws ParserConfigurationException, SAXException, IOException
    {
        authService.checkAuthorization(authTokenParam, authTokenHeader);

        return packageProcess
                .forLanguage(languageCode)
                .forPackage(packageCode)
                .compressed(Boolean.parseBoolean(compressed))
                .atRevisionNumber(revisionNumber)
                .withPixelDensity(PixelDensity.getEnumWithFallback(desiredPixelDensity, PixelDensity.HIGH))
                .forMinimumInterpreterVersion(minimumInterpreterVersionHeader == null ? minimumInterpreterVersionParam : minimumInterpreterVersionHeader)
                .buildResponse();
    }

    @POST
    @Consumes("multipart/form-data")
    @Produces(MediaType.APPLICATION_XML)
    public Response createNewPackage(@HeaderParam("authorization") String authTokenHeader,
									 @QueryParam("authorization") String authTokenParam,
									 MultipartFormDataInput input)
    {
		authService.checkAuthorization(authTokenParam, authTokenHeader);

		NewPackagePostData newPackagePostData = new NewPackagePostData(input);

		for(NewPackage newPackage : newPackagePostData.newPackageSet)
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
    public Response updatePackage(@QueryParam("finished") String isFinished,
                                  @HeaderParam("authentication") String authCode)
    {

        return Response.noContent().build();
    }
}
