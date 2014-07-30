package org.cru.godtools.domain.authentication;

import org.cru.godtools.domain.AbstractServiceTest;
import org.cru.godtools.domain.TestClockImpl;
import org.cru.godtools.domain.TestSqlConnectionProducer;
import org.cru.godtools.domain.properties.GodToolsProperties;
import org.cru.godtools.domain.properties.GodToolsPropertiesFactory;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.sql2o.Connection;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.UUID;

/**
 * Created by ryancarlson on 4/3/14.
 */
public class AuthorizationServiceTest extends AbstractServiceTest
{
	public static final UUID TEST_AUTHORIZATION_ID = UUID.randomUUID();

	@Inject
	private AuthorizationService authorizationService;

	@Deployment
	public static JavaArchive createDeployment()
	{
		return ShrinkWrap.create(JavaArchive.class)
				.addClasses(AuthorizationService.class,
						Connection.class,
						TestSqlConnectionProducer.class,
						GodToolsPropertiesFactory.class,
						GodToolsProperties.class,
						TestClockImpl.class)
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");

	}

	@BeforeMethod
	public void setup()
	{
		try
		{
			authorizationService.sqlConnection.getJdbcConnection().setAutoCommit(false);
		}
		catch(SQLException e)
		{
			/*yawn*/
		}
		 AuthorizationServiceTestMockData.persistAuthorization(authorizationService);
	}

	@AfterMethod
	public void cleanup()
	{
		try
		{
			authorizationService.sqlConnection.getJdbcConnection().rollback();
		}
		catch(SQLException e)
		{
			/*yawn*/
		}
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
