package org.cru.godtools.api.authentication;

/**
 * Created by ryancarlson on 4/3/14.
 */
public class AuthorizationServiceTestMockDataService
{
	public void persistAuthorization(AuthorizationService authorizationService)
	{
		AuthenticationRecord authenticationRecord = new AuthenticationRecord();
		authenticationRecord.setId(AuthorizationServiceTest.TEST_AUTHORIZATION_ID);
		authenticationRecord.setAuthToken("a");
		authenticationRecord.setUsername("unittest-user");

		authorizationService.recordNewAuthorization(authenticationRecord);
	}
}
