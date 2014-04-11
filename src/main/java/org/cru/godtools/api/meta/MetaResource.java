package org.cru.godtools.api.meta;

import org.cru.godtools.api.authentication.AuthorizationService;
import org.cru.godtools.api.utilities.ErrorResponse;
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

@Path("/meta/{language}{package : (/package)?}")
public class MetaResource
{

    @Inject
    MetaService metaService;
    @Inject
    AuthorizationService authService;

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public Response getMetaInfo(@PathParam("language") String languageCode,
                                @PathParam("package") String packageCode,
                                @QueryParam("interpreter") Integer minimumInterpreterVersionParam,
                                @HeaderParam("interpreter") Integer minimumInterpreterVersionHeader,
                                @QueryParam("authorization") String authCodeParam,
                                @HeaderParam("authorization") String authCodeHeader) throws ParserConfigurationException, SAXException, IOException
    {
        authService.checkAuthorization(authCodeParam, authCodeHeader);
        Integer interpreterVersion = getMinimumInterpreterVersion(minimumInterpreterVersionParam, minimumInterpreterVersionHeader);

        if(interpreterVersion == null)
        {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("parameter or header \"interpreter\" is required"))
                    .build();
        }

        return Response.ok(metaService.getMetaResults(languageCode, packageCode, interpreterVersion)).build();
    }

    private Integer getMinimumInterpreterVersion(Integer minimumInterpreterVersionParam, Integer minimumInterpreterVersionHeader)
    {
        return minimumInterpreterVersionParam == null ? minimumInterpreterVersionHeader : minimumInterpreterVersionParam;
    }
}
