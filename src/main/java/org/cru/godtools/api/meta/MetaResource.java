package org.cru.godtools.api.meta;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by ryancarlson on 3/14/14.
 */

@Path("/meta")
public class MetaResource
{

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public Response getMetaInfo(@QueryParam("language") String languageCode,
                                @QueryParam("package") String packageCode,
                                @HeaderParam("authentication") String authCode)
    {


        return Response.ok().build();
    }
}
