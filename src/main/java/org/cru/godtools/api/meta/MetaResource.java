package org.cru.godtools.api.meta;

import org.xml.sax.SAXException;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
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

@Path("/meta")
public class MetaResource
{

    @Inject
    MockMetaService metaService;

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public Response getMetaInfo(@QueryParam("language") String languageCode,
                                @QueryParam("package") String packageCode,
                                @HeaderParam("authentication") String authCode) throws ParserConfigurationException, SAXException, IOException
    {


        return Response.ok(metaService.getMetaResults(languageCode, packageCode)).build();
    }
}
