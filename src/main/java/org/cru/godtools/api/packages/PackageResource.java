package org.cru.godtools.api.packages;

import org.cru.godtools.api.authentication.AuthenticationService;
import org.cru.godtools.api.packages.exceptions.LanguageNotFoundException;
import org.cru.godtools.api.packages.exceptions.MissingVersionException;
import org.cru.godtools.api.packages.exceptions.NoTranslationException;
import org.cru.godtools.api.packages.exceptions.PackageNotFoundException;
import org.cru.godtools.api.packages.utils.LanguageCode;
import org.xml.sax.SAXException;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.security.InvalidParameterException;

/**
 * Created by ryancarlson on 3/14/14.
 */

@Path("/packages")
public class PackageResource
{

    @Inject
    GodToolsResponseAssemblyProcess packageProcess;
    @Inject
    AuthenticationService authenticator;

    @GET
    @Produces({"application/zip", "application/xml"})
    public Response getPackages(@QueryParam("language") String languageCode,
                                @QueryParam("package") String packageCode,
                                @QueryParam("interpreter-version") Integer minimumInterpreterVersion,
                                @QueryParam("compressed") String compressed,
                                @QueryParam("revision-number") Integer revisionNumber,
                                @QueryParam("screen-size") String screenSize,
                                @HeaderParam("authorization") String authCodeHeader,
                                @QueryParam("auth_token") String authCodeParam) throws ParserConfigurationException, SAXException, IOException
    {
        authenticator.authorizeUser(authCodeHeader == null ? authCodeParam : authCodeHeader);

        return packageProcess
                .forLanguage(languageCode)
                .forPackage(packageCode)
                .compressed(Boolean.parseBoolean(compressed))
                .atRevisionNumber(revisionNumber)
                .forMinimumInterpreterVersion(minimumInterpreterVersion)
                .buildResponse();
    }

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Response createNewPackage(@HeaderParam("authentication") String authCode)
    {

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
