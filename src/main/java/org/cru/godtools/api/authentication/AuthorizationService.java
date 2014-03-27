package org.cru.godtools.api.authentication;

import com.google.common.base.Strings;
import org.ccci.util.time.Clock;
import org.sql2o.Connection;

import javax.inject.Inject;

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

    public void authorizeUser(String authTokenParam, String authTokenHeader)
    {
        String authToken = authTokenHeader == null ? authTokenParam : authTokenHeader;

        if(Strings.isNullOrEmpty(authToken)) throw new UnauthorizedException();

        AuthenticationRecord authRecord = sqlConnection.createQuery(AuthenticationQueries.selectByAuthToken)
                .setAutoDeriveColumnNames(true)
                .addParameter("authToken", authToken)
                .executeAndFetchFirst(AuthenticationRecord.class);

        if(authRecord == null || !authRecord.isCurrentlyActive(clock.currentDateTime())) throw new UnauthorizedException();
    }

    private class AuthenticationQueries
    {
        static final String selectByAuthToken = "SELECT * FROM auth_tokens WHERE auth_token = :authToken";
    }
}
