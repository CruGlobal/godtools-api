package org.cru.godtools.domain.services;

import com.google.common.base.Optional;
import org.cru.godtools.domain.authentication.*;
import org.sql2o.Connection;

/**
 * Created by ryancarlson on 3/26/14.
 */
public interface AuthorizationService
{
    Optional<AuthorizationRecord> getAuthorizationRecord(String authTokenParam, String authTokenHeader);

	void recordNewAuthorization(AuthorizationRecord authenticationRecord);

    AccessCodeRecord getAccessCode(String accessCode);

	Connection getSqlConnection();

}
