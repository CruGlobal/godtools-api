package org.cru.godtools.domain.authentication;

/**
 * Created by ryancarlson on 4/3/14.
 */
public class AuthorizationServiceTestMockData
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
