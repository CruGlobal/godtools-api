package org.cru.godtools.domain.authentication;

import com.google.common.base.Optional;
import org.ccci.util.time.Clock;
import org.sql2o.Connection;
import org.jboss.logging.Logger;
import javax.inject.Inject;

public class AuthorizationService
{
	@Inject
	Connection sqlConnection;

	@Inject
	Clock clock;

	Logger log = Logger.getLogger(AuthorizationService.class);

	public boolean hasDraftAccess(String authCodeParam, String authCodeHeader)
	{
		Optional<AuthorizationRecord> authorizationRecord = getAuthorizationRecord(authCodeParam, authCodeHeader);
		return authorizationRecord.isPresent() &&
				authorizationRecord.get().hasDraftAccess(clock.currentDateTime());
	}

	public boolean hasAdminAccess(String authCodeParam, String authCodeHeader)
	{
		Optional<AuthorizationRecord> authorizationRecord = getAuthorizationRecord(authCodeParam, authCodeHeader);
		return authorizationRecord.isPresent() &&
				authorizationRecord.get().hasAdminAccess(clock.currentDateTime());
	}

	public Optional<AuthorizationRecord> getAuthorizationRecord(String authTokenParam, String authTokenHeader)
	{
		String authToken = authTokenHeader == null ? authTokenParam : authTokenHeader;

		log.info("Getting authorization for: " + authToken);

		AuthorizationRecord authorizationRecord = sqlConnection.createQuery(AuthenticationQueries.selectByAuthToken)
				.setAutoDeriveColumnNames(true)
				.addParameter("authToken", authToken)
				.executeAndFetchFirst(AuthorizationRecord.class);

		return Optional.fromNullable(authorizationRecord);
	}

	public void recordNewAuthorization(AuthorizationRecord authenticationRecord)
	{
		sqlConnection.createQuery(AuthenticationQueries.insert)
				.addParameter("id", authenticationRecord.getId())
				.addParameter("username", authenticationRecord.getUsername())
				.addParameter("grantedTimestamp", clock.currentDateTime())
				.addParameter("authToken", authenticationRecord.getAuthToken())
				.addParameter("deviceId", authenticationRecord.getDeviceId())
				.addParameter("draftAccess", authenticationRecord.hasDraftAccess())
				.addParameter("admin", authenticationRecord.isAdmin())
				.addParameter("revokedTimestamp", authenticationRecord.getRevokedTimestamp())
				.executeUpdate();
	}

	public AccessCodeRecord getAccessCode(String accessCode)
	{
	   return sqlConnection.createQuery(AuthenticationQueries.findAccessCode)
				.setAutoDeriveColumnNames(true)
				.addParameter("accessCode", accessCode)
				.executeAndFetchFirst(AccessCodeRecord.class);
	}

	public void updateAdminRecordExpiration(AuthorizationRecord authorizationRecord, int hoursToAdd)
	{
		if(authorizationRecord.isAdmin())
		{
			sqlConnection.createQuery(AuthenticationQueries.updateExpiration)
					.addParameter("id", authorizationRecord.getId())
					.addParameter("revokedTimestamp", clock.currentDateTime().plusHours(hoursToAdd))
					.executeUpdate();

			log.info(String.format("%s hours added to authorization record", String.valueOf(hoursToAdd)));
		}
		else
		{
			log.info("No time added to authorization record");
		}
	}

	private class AuthenticationQueries
	{
		static final String selectByAuthToken = "SELECT * FROM auth_tokens WHERE auth_token = :authToken";
		static final String insert = "INSERT INTO auth_tokens(id, username, granted_timestamp, auth_token, device_id, draft_access, admin, revoked_timestamp) VALUES(:id, :username, :grantedTimestamp, :authToken, :deviceId, :draftAccess, :admin, :revokedTimestamp)";
		static final String findAccessCode = "SELECT * FROM access_codes WHERE access_code  = :accessCode";
		static final String updateExpiration = "UPDATE auth_tokens SET revoked_timestamp = :revokedTimestamp WHERE id = :id";
	}
}
