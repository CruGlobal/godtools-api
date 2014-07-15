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
import java.util.UUID;

/**
 * Created by ryancarlson on 5/12/14.
 */
@Path("/auth")
@Produces(MediaType.APPLICATION_XML)
public class AuthorizationResource
{

	@Inject
	Clock clock;

    @Inject AuthorizationService authorizationService;

    @POST
    public Response requestTranslatorStatusWithoutCode(@HeaderParam("deviceId") String deviceIdHeader,
                                            @QueryParam("deviceId") String deviceIdParam) throws ParserConfigurationException, SAXException, IOException
    {
        String code = "";
        return requestTranslatorStatus(code, deviceIdHeader, deviceIdParam);
    }

	@POST
	@Path("/{code}")
	public Response requestTranslatorStatus(@PathParam("code") String code,
                                @HeaderParam("deviceId") String deviceIdHeader,
                                @QueryParam("deviceId") String deviceIdParam) throws ParserConfigurationException, SAXException,IOException
	{
		DateTime currentTime = clock.currentDateTime();

        AccessCodeRecord accessCodeRecord = authorizationService.getAccessCode(code);

        AuthorizationRecord authorizationRecord = new AuthorizationRecord();

        authorizationRecord.setAuthToken(AuthTokenGenerator.generate());

        String device = deviceIdHeader == null ? deviceIdParam : deviceIdHeader;

        authorizationRecord.setDeviceId(device);

        authorizationRecord.setId(UUID.randomUUID());

        if(accessCodeRecord != null && accessCodeRecord.isCurrentlyActive(currentTime))
        {
            authorizationRecord.setDraftAccess(true);
        }
        else
        {
            authorizationRecord.setDraftAccess(false);
        }

        authorizationService.recordNewAuthorization(authorizationRecord);

        return Response.noContent()
                .header("authToken", authorizationRecord.getAuthToken())
                .build();
	}


    @GET
    @Path("/status")
    @Produces(MediaType.APPLICATION_XML)
    public Response requestAuthStatus(@HeaderParam("authorization") String authTokenParam,
                               @QueryParam("authorization") String authTokenHeader) throws ParserConfigurationException, SAXException,IOException
    {
        authorizationService.checkAuthorization(authTokenParam, authTokenHeader);
        return  Response.noContent().build();
    }
}
