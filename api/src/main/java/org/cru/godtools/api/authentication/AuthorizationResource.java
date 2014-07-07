package org.cru.godtools.api.authentication;

import org.ccci.util.time.Clock;
import org.joda.time.DateTime;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

/**
 * Created by ryancarlson on 5/12/14.
 */
@Path("/auth")
public class AuthorizationResource
{

	@Inject
	Clock clock;

	@POST
	@Path("/{code}")
	public Response requestTranslatorStatus(@PathParam("code") String accessCode)
	{
		DateTime currentTime = clock.currentDateTime();


		return Response.ok().build();
	}
}
