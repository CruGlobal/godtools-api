package org.cru.godtools.api.services.Sql2oStandard;

import com.google.common.base.Optional;
import org.ccci.util.time.Clock;
import org.cru.godtools.api.services.AuthorizationService;
import org.cru.godtools.domain.authentication.AuthorizationRecord;
import org.cru.godtools.domain.authentication.AccessCodeRecord;
import org.sql2o.Connection;
import org.jboss.logging.Logger;

import javax.enterprise.inject.Default;
import javax.inject.Inject;

/**
 * Created by justinsturm on 6/29/15.
 */
@Default
public class Sql2oAuthorizationService implements AuthorizationService
{
    @Inject
    Connection sqlConnection;

    @Inject
    Clock clock;

    Logger log = Logger.getLogger(Sql2oAuthorizationService.class);

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
                .executeUpdate();
    }

    public AccessCodeRecord getAccessCode(String accessCode)
    {
        return sqlConnection.createQuery(AuthenticationQueries.findAccessCode)
                .setAutoDeriveColumnNames(true)
                .addParameter("accessCode", accessCode)
                .executeAndFetchFirst(AccessCodeRecord.class);
    }

    private class AuthenticationQueries
    {
        static final String selectByAuthToken = "SELECT * FROM auth_tokens WHERE auth_token = :authToken";
        static final String insert = "INSERT INTO auth_tokens(id, username, granted_timestamp, auth_token, device_id, draft_access, admin) VALUES(:id, :username, :grantedTimestamp, :authToken, :deviceId, :draftAccess, :admin)";
        static final String findAccessCode = "SELECT * FROM access_codes WHERE access_code  = :accessCode";
    }

    public Connection getSqlConnection()
    {
        return sqlConnection;
    }
}
