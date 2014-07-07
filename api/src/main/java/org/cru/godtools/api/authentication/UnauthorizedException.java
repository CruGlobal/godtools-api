package org.cru.godtools.api.authentication;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * Created by ryancarlson on 3/26/14.
 */
public class UnauthorizedException extends WebApplicationException
{
    @Override
    public Response getResponse()
    {
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }
}
