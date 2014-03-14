package org.cru.godtools.api.packages;

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

/**
 * Created by ryancarlson on 3/14/14.
 */

@Path("/packages")
public class PackageResource
{

    @Inject
    MockPackageService packageService;

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public Response getPackages(@QueryParam("language") String languageCode,
                                @QueryParam("package") String packageCode,
                                @QueryParam("interpreter-version") String minimumInterpreterVersion,
                                @QueryParam("compressed") String compressed,
                                @QueryParam("encoding") String encoding,
                                @QueryParam("revision-number") Integer revisionNumber,
                                @QueryParam("segment") String segment,
                                @QueryParam("screen-size") String screenSize,
                                @HeaderParam("authentication") String authCode) throws ParserConfigurationException, SAXException, IOException
    {

        return Response.ok(packageService.getPackage(languageCode,packageCode)).build();
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