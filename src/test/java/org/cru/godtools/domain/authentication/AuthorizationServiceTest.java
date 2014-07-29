package org.cru.godtools.domain.authentication;

import org.cru.godtools.domain.AbstractServiceTest;
import org.cru.godtools.domain.TestClockImpl;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.UUID;

/**
 * Created by ryancarlson on 4/3/14.
 */
public class AuthorizationServiceTest extends AbstractServiceTest
{
	private AuthorizationServiceTestMockDataService mockData;
	private AuthorizationService authorizationService;

	public static final UUID TEST_AUTHORIZATION_ID = UUID.randomUUID();

	@BeforeClass
	@Override
	public void setup()
	{
		super.setup();

		authorizationService = new AuthorizationService(sqlConnection, new TestClockImpl());

		mockData = new AuthorizationServiceTestMockDataService();
		mockData.persistAuthorization(authorizationService);
	}

	@Test
	public void testCheckAuthorizationFromParam()
	{
		authorizationService.getAuthorizationRecord("a", null);
	}

	@Test
	public void testCheckAuthorizationFromHeader()
	{
		authorizationService.getAuthorizationRecord(null, "a");
	}

	@Test(expectedExceptions = UnauthorizedException.class)
	public void testCheckAuthorizationFromParamFailed()
	{
		try
		{
			AuthorizationRecord.checkAuthorization(authorizationService.getAuthorizationRecord("b", null), new TestClockImpl().currentDateTime());
		}
		catch(UnauthorizedException exception)
		{
			Assert.assertEquals(exception.getResponse().getStatus(), 401);
			throw exception;
		}
	}

	@Test(expectedExceptions = UnauthorizedException.class)
	public void testCheckAuthorizationFromHeaderFailed()
	{
		try
		{
			AuthorizationRecord.checkAuthorization(authorizationService.getAuthorizationRecord(null, "b"), new TestClockImpl().currentDateTime());
		}
		catch(UnauthorizedException exception)
		{
			Assert.assertEquals(exception.getResponse().getStatus(), 401);
			throw exception;
		}
	}
}
