package org.cru.godtools.domain.authentication;

import com.google.common.base.Strings;
import org.ccci.util.time.Clock;
import org.sql2o.Connection;

import javax.inject.Inject;
import java.util.UUID;

/**
 * Created by ryancarlson on 3/26/14.
 */
public class AuthorizationService
{
    Connection sqlConnection;
    Clock clock;

    @Inject
    public AuthorizationService(Connection sqlConnection, Clock clock)
    {
        this.sqlConnection = sqlConnection;
        this.clock = clock;
    }

    public void checkAuthorization(String authTokenParam, String authTokenHeader)
    {
		AuthorizationRecord authRecord = getAuthenticationRecord(authTokenParam, authTokenHeader);

        if(authRecord != null && authRecord.isCurrentlyActive(clock.currentDateTime())) return;

		else throw new UnauthorizedException();
    }

	public boolean canAccessOrCreateDrafts(String authTokenParam, String authTokenHeader)
	{
		AuthorizationRecord authRecord = getAuthenticationRecord(authTokenParam, authTokenHeader);

		return authRecord.hasDraftAccess();
	}

	private AuthorizationRecord getAuthenticationRecord(String authTokenParam, String authTokenHeader)
	{
		String authToken = authTokenHeader == null ? authTokenParam : authTokenHeader;

		if(Strings.isNullOrEmpty(authToken)) throw new UnauthorizedException();

		return sqlConnection.createQuery(AuthenticationQueries.selectByAuthToken)
				.setAutoDeriveColumnNames(true)
				.addParameter("authToken", authToken)
				.executeAndFetchFirst(AuthorizationRecord.class);
	}

	public void recordNewAuthorization(AuthorizationRecord authenticationRecord)
	{
		sqlConnection.createQuery(AuthenticationQueries.insert)
				.addParameter("id", authenticationRecord.getId())
				.addParameter("username", authenticationRecord.getUsername())
				.addParameter("grantedTimestamp", clock.currentDateTime())
				.addParameter("authToken", authenticationRecord.getAuthToken())
                .addParameter("deviceId", authenticationRecord.getDeviceId())
				.executeUpdate();
	}

    public AccessCodeRecord getAccessCode(String accessCode)
    {
       return sqlConnection.createQuery(AuthenticationQueries.findAccessCode)
                .setAutoDeriveColumnNames(true)
                .addParameter("accessCode", accessCode)
                .executeAndFetchFirst(AccessCodeRecord.class);
    }

    public String createAuthToken()
    {
        String authToken = AuthTokenGenerator.generate();
        return authToken;
    }

    public UUID createUUID()
    {
        return UUID.randomUUID();
    }

    private class AuthenticationQueries
    {
        static final String selectByAuthToken = "SELECT * FROM auth_tokens WHERE auth_token = :authToken";
		static final String insert = "INSERT INTO auth_tokens(id, username, granted_timestamp, auth_token, device_id) VALUES(:id, :username, :grantedTimestamp, :authToken, :deviceId)";
        static final String findAccessCode = "SELECT * FROM access_codes WHERE access_code  = :accessCode";
    }
}
