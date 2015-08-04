package org.cru.godtools.domain.authentication;

import org.cru.godtools.domain.model.*;
import org.cru.godtools.domain.services.*;

/**
 * Created by ryancarlson on 4/3/14.
 */
public class AuthorizationMockData
{
	public static void persistAuthorization(AuthorizationService authorizationService)
	{
		AuthorizationRecord authenticationRecord = new AuthorizationRecord();
		authenticationRecord.setId(AuthorizationServiceTest.TEST_AUTHORIZATION_ID);
		authenticationRecord.setAuthToken("a");
		authenticationRecord.setUsername("unittest-user");

		authorizationService.recordNewAuthorization(authenticationRecord);
	}
}
