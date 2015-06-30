package org.cru.godtools.api.authorization;

import org.ccci.util.time.Clock;
import org.cru.godtools.domain.Simply;
import org.cru.godtools.domain.authentication.AccessCodeRecord;
import org.cru.godtools.domain.authentication.AuthTokenGenerator;
import org.cru.godtools.domain.authentication.AuthorizationRecord;
import org.cru.godtools.api.services.AuthorizationService;
import org.cru.godtools.domain.authentication.UnauthorizedException;
import org.jboss.logging.Logger;
import org.xml.sax.SAXException;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by ryancarlson on 5/12/14.
 */
@Path("/auth")
public class AuthorizationResource
{

	@Inject
	Clock clock;

    @Inject
	AuthorizationService authorizationService;

	Logger log = Logger.getLogger(AuthorizationResource.class);

	@GET
	@Path("/status")
	public Response requestAuthStatus(@HeaderParam("Authorization") String authTokenParam,
									  @QueryParam("Authorization") String authTokenHeader)
	{
		// method will throw an UnauthorizedException (401) if:
		// - authToken is not provided in one of two parameters,
		// - authToken is invalid
		// - authToken has been revoked
		authorizationService.getAuthorizationRecord(authTokenParam, authTokenHeader);

		// if we get here, assume all is good and a 204 no content is fine.
		return  Response.noContent().build();
	}

    @POST
    public Response getAuthorizationToken(@HeaderParam("deviceId") String deviceIdHeader,
                                            @QueryParam("deviceId") String deviceIdParam) throws ParserConfigurationException, SAXException, IOException
    {
		log.info("Requesting basic authorization token");

	    String device = deviceIdHeader == null ? deviceIdParam : deviceIdHeader;
		AuthorizationRecord authorizationRecord = createNewAuthorization();
	    authorizationRecord.setDeviceId(device);
		authorizationRecord.setDraftAccess(false);

		log.info("Saving basic authorization record:");
		Simply.logObject(authorizationRecord, AuthorizationResource.class);

		authorizationService.recordNewAuthorization(authorizationRecord);

		return Response.noContent()
				.header("Authorization", authorizationRecord.getAuthToken())
				.build();
	}

	@POST
	@Path("/{translatorCode}")
	public Response getAuthorizationToken(@PathParam("translatorCode") String translatorCode,
                                @HeaderParam("deviceId") String deviceIdHeader,
                                @QueryParam("deviceId") String deviceIdParam) throws ParserConfigurationException, SAXException,IOException
	{
		log.info("Requesting authorization token with translator access");

		AccessCodeRecord accessCodeRecord = authorizationService.getAccessCode(translatorCode);

		String device = deviceIdHeader == null ? deviceIdParam : deviceIdHeader;
		AuthorizationRecord authorizationRecord = createNewAuthorization();
		authorizationRecord.setDeviceId(device);

		if(accessCodeRecord == null || !accessCodeRecord.isCurrentlyActive(clock.currentDateTime()))
		{
			log.info("Authorization with translator access code was invalid.");
			log.info("Provided code: " + translatorCode);
			log.info("Device ID: " + device);

			// If the incorrect translator code is given, a 401 is returned
			throw new UnauthorizedException();
		}

		authorizationRecord.setDraftAccess(true);

		log.info("Saving authorization record with translator access:");
		Simply.logObject(authorizationRecord, AuthorizationResource.class);

        authorizationService.recordNewAuthorization(authorizationRecord);

        return Response.noContent()
                .header("Authorization", authorizationRecord.getAuthToken())
                .build();
	}

	@POST
	@Path("/admin")
	public Response getAdministratorToken(String adminPassphrase,
										  @HeaderParam("deviceId") String deviceIdHeader,
										  @QueryParam("deviceId") String deviceIdParam) throws ParserConfigurationException, SAXException,IOException
	{
		log.info("Requesting authorization token with translator access");

		AccessCodeRecord accessCodeRecord = authorizationService.getAccessCode(adminPassphrase);

		String device = deviceIdHeader == null ? deviceIdParam : deviceIdHeader;

		if(accessCodeRecord == null || !accessCodeRecord.isCurrentlyActive(clock.currentDateTime()))
		{
			log.info("Authorization with admin passphrase was invalid.");
			log.info("Provided passphrase: " + adminPassphrase);
			log.info("Device ID: " + device);

			// If the incorrect translator code is given, a 401 is returned
			throw new UnauthorizedException();
		}

		AuthorizationRecord authorizationRecord = createNewAuthorization();
		authorizationRecord.setDeviceId(device);
		authorizationRecord.setAdmin(accessCodeRecord.isAdmin());
		authorizationRecord.setDraftAccess(true);

		log.info("Saving authorization record with admin access:");
		Simply.logObject(authorizationRecord, AuthorizationResource.class);

		authorizationService.recordNewAuthorization(authorizationRecord);

		return Response.noContent()
				.header("Authorization", authorizationRecord.getAuthToken())
				.build();
	}

	public AuthorizationRecord createNewAuthorization()
	{
		AuthorizationRecord authorizationRecord = new AuthorizationRecord();
		authorizationRecord.setAuthToken(AuthTokenGenerator.generate());
		authorizationRecord.setId(UUID.randomUUID());

		return authorizationRecord;
	}
}
