package org.cru.godtools.api.packages;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.inject.Inject;

/**
 * First pass at writing Arquillian tests
 *
 * Created by ryancarlson on 7/30/14.
 */
public class PackageResourceTest extends Arquillian
{

	@Deployment
	public static JavaArchive createDeployment()
	{
		return ShrinkWrap.create(JavaArchive.class)
				.addClass(GodToolsPackage.class)
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
	}

	@Inject
	GodToolsPackage godToolsPackage;

	@Test
	public void testOne()
	{
		Assert.assertEquals(godToolsPackage.testMethod("Ryan"), "Hello Ryan".toString());
	}
}
