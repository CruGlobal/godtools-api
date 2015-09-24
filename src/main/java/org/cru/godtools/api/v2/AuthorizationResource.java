package org.cru.godtools.api.v2;

import org.ccci.util.time.Clock;
import org.cru.godtools.domain.authentication.AccessCodeRecord;
import org.cru.godtools.domain.authentication.AuthTokenGenerator;
import org.cru.godtools.domain.authentication.AuthorizationRecord;
import org.cru.godtools.domain.authentication.AuthorizationService;
import org.cru.godtools.domain.authentication.UnauthorizedException;
import org.jboss.logging.Logger;
import org.xml.sax.SAXException;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.UUID;

@Path("/v2/auth")
public class AuthorizationResource
{
	@Inject
	Clock clock;

	@Inject
	AuthorizationService authorizationService;

	Logger log = Logger.getLogger(getClass());

	@POST
	@Path("/{translatorCode}")
	public Response getDraftAccessToken(@PathParam("translatorCode") String translatorCode) throws ParserConfigurationException, SAXException,IOException
	{
		log.info("Requesting authorization token with translator access");

		AccessCodeRecord accessCodeRecord = authorizationService.getAccessCode(translatorCode);

		AuthorizationRecord authorizationRecord = createNewAuthorization();

		if(accessCodeRecord == null || !accessCodeRecord.isCurrentlyActive(clock.currentDateTime()))
		{
			log.info("Authorization with translator access code was invalid.");
			log.info("Provided code: " + translatorCode);

			// If the incorrect translator code is given, a 401 is returned
			throw new UnauthorizedException();
		}

		authorizationRecord.setDraftAccess(true);
		authorizationRecord.setAdmin(false);

		log.info(String.format("Saving authorization record with translator access w/ ID %s",
				authorizationRecord.getId()));

		authorizationService.recordNewAuthorization(authorizationRecord);

		return Response.noContent()
				.header("Authorization", authorizationRecord.getAuthToken())
				.build();
	}

	@POST
	@Path("/admin")
	public Response getAdministratorToken(String adminPassphrase)
	{
		log.info("Requesting authorization token with translator access");

		AccessCodeRecord accessCodeRecord = authorizationService.getAccessCode(adminPassphrase);

		if(accessCodeRecord == null ||
				!accessCodeRecord.isCurrentlyActive(clock.currentDateTime()) ||
				!accessCodeRecord.isAdmin())
		{
			log.info("Authorization with admin passphrase was invalid.");
			log.info("Provided passphrase: " + adminPassphrase);

			// If the incorrect translator code is given, a 401 is returned
			throw new UnauthorizedException();
		}

		AuthorizationRecord authorizationRecord = createNewAuthorization();

		log.info(String.format("Saving authorization record with admin access w/ ID %s",
				authorizationRecord.getId()));

		authorizationService.recordNewAuthorization(authorizationRecord);

		return Response.noContent()
				.header("Authorization", authorizationRecord.getAuthToken())
				.build();
	}

	private AuthorizationRecord createNewAuthorization()
	{
		AuthorizationRecord authorizationRecord = new AuthorizationRecord();
		authorizationRecord.setId(UUID.randomUUID());
		authorizationRecord.setAuthToken(AuthTokenGenerator.generate());
		authorizationRecord.setAdmin(true);
		authorizationRecord.setDraftAccess(true);
		authorizationRecord.setGrantedTimestamp(clock.currentDateTime());
		authorizationRecord.setRevokedTimestamp(clock.currentDateTime().plusHours(4));

		return authorizationRecord;
	}
}
