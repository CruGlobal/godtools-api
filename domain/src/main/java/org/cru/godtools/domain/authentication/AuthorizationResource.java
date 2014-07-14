package org.cru.godtools.domain.authentication;

import org.ccci.util.time.Clock;
import org.joda.time.DateTime;
import org.xml.sax.SAXException;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * Created by ryancarlson on 5/12/14.
 */
@Path("/auth")
@Produces(MediaType.APPLICATION_XML)
public class AuthorizationResource
{

	@Inject
	Clock clock;

    @Inject AuthorizationRecord authorizationRecord;
    @Inject AuthorizationService authorizationService;

	@POST
	@Path("/{code}")
	public Response requestTranslatorStatus(@PathParam("code") String code,
                                @HeaderParam("deviceId") String deviceIdHeader,
                                @QueryParam("deviceId") String deviceIdParam,
                                @HeaderParam("interpreter") Integer minimumInterpreterVersionParam,
                                @QueryParam("interpreter") Integer minimumInterpreterVersionHeader,
                                @HeaderParam("authorization") String authCodeParam,
                                @QueryParam("authorization") String authCodeHeader) throws ParserConfigurationException, SAXException,IOException
	{
		DateTime currentTime = clock.currentDateTime();

        AccessCodeRecord accessCodeRecord = authorizationService.getAccessCode(code);

        authorizationService.checkAuthorization(authCodeHeader, authCodeParam);

        if(accessCodeRecord != null && accessCodeRecord.isCurrentlyActive(currentTime))
        {
            authorizationRecord.setDraftAccess(true);
        }
        else
        {
            authorizationRecord.setDraftAccess(false);
        }

        return Response.ok()
                .header("UUID", authorizationRecord.getId())
                .header("username", authorizationRecord.getUsername())
                .header("authToken", authorizationRecord.getAuthToken())
                .header("grantedTimestamp", authorizationRecord.getGrantedTimestamp())
                .header("revokedTimestamp", authorizationRecord.getRevokedTimestamp())
                .header("deviceId", authorizationRecord.getDeviceId())
                .header("draftAccess", authorizationRecord.hasDraftAccess())
                .build();
	}


    @GET
    @Path("/status")
    @Produces(MediaType.APPLICATION_XML)
    public Response requestAuthStatus(@HeaderParam("interpreter") Integer minimumInterpreterVersionParam,
                                      @QueryParam("interpreter") Integer minimumInterpreterVersionHeader,
                                      @HeaderParam("authorization") String authCodeParam,
                                      @QueryParam("authorization") String authCodeHeader) throws ParserConfigurationException, SAXException,IOException
    {
        return  Response.ok().build();
    }
}
