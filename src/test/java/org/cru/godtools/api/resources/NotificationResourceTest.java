package org.cru.godtools.api.resources;

import junit.framework.Assert;
import org.cru.godtools.domain.*;
import org.cru.godtools.utils.collections.*;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.*;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.*;
import org.junit.runner.*;

import javax.inject.Inject;
import javax.transaction.*;
import javax.ws.rs.core.Response;

/**
 * Created by matthewfrederick on 1/5/15.
 */
@RunWith(Arquillian.class)
public class NotificationResourceTest
{
	@Deployment
	public static WebArchive createDeployment()
	{
		return ShrinkWrap.create(WebArchive.class)
				.addClasses(Sql2oTestClassCollection.getClasses())
				.addClasses(GodToolsPackageServiceTestClassCollection.getClasses())
				.addClasses(NotificationResource.class, TestClockImpl.class)
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
	}

	@Inject
	NotificationResource notificationResource;

	@Inject
	UserTransaction userTransaction;


	@BeforeClass
	public void initializeDatabase()
	{
		UnittestDatabaseBuilder.build();
	}

	@Before
	public void setup() throws SystemException, NotSupportedException
	{
		userTransaction.begin();
	}

	@After
	public void cleanup() throws SystemException
	{
		userTransaction.rollback();
	}

	@Test
	public void testRegisterDevice()
	{
		Response response = notificationResource.registerDevice("123", "456", null);
		Assert.assertNotNull(response);
	}
}
