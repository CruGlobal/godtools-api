package org.cru.godtools.api.packages;

import org.ccci.util.xml.XmlDocumentSearchUtilities;
import org.cru.godtools.api.packages.utils.FileZipper;
import org.cru.godtools.domain.TestSqlConnectionProducer;
import org.cru.godtools.domain.authentication.AuthorizationService;
import org.cru.godtools.tests.AbstractFullPackageServiceTest;
import org.cru.godtools.tests.GodToolsPackageServiceTestClassCollection;
import org.cru.godtools.tests.Sql2oTestClassCollection;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * First pass at writing Arquillian tests
 *
 * Created by ryancarlson on 7/30/14.
 */
public class PackageResourceTest extends AbstractFullPackageServiceTest
{

	@Deployment
	public static WebArchive createDeployment()
	{
		return ShrinkWrap.create(WebArchive.class)
				.addClasses(Sql2oTestClassCollection.getClasses())
				.addClasses(GodToolsPackageServiceTestClassCollection.getClasses())
				.addClasses(PackageResource.class, AuthorizationService.class, FileZipper.class, GodToolsPackageRetrievalProcess.class)
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
	}

	@Inject
	PackageResource packageResource;

	@BeforeMethod
	public void setup()
	{
		try
		{
			TestSqlConnectionProducer.getConnection().getJdbcConnection().setAutoCommit(false);
		}
		catch(SQLException e)
		{
			/*yawn*/
		}
		saveTestPackage();
	}

	@AfterMethod
	public void cleanup()
	{
		try
		{
			TestSqlConnectionProducer.getConnection().getJdbcConnection().rollback();
		}
		catch(SQLException e)
		{
			/*yawn*/
		}
	}

	/**
	 * Tests getting the English (en) version of Knowing God Personally (kgp).
	 *
	 * The unittest database has just one page persisted, but that should be enough
	 * to verify most of what we want to verify.
	 */
	@Test
	public void testGetPackage() throws Exception
	{
		Response response = packageResource.getPackage("en",
				"kgp",
				1,
				null,
				"false",
				new BigDecimal("1.1"),
				"High",
				"a",
				null);

		Assert.assertEquals(response.getStatus(), 200);

		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

		Document xmlContentsFile = builder.parse(new InputSource((ByteArrayInputStream)response.getEntity()));
		validateContentsXml(xmlContentsFile);
	}

	/**
	 * Tests getting all the English (en) translated packages.
	 *
	 * Currently there is just one, kgp.
	 *
	 * The unittest database has just one page persisted, but that should be enough
	 * to verify most of what we want to verify.
	 */
	@Test
	public void testGetPackagesForLanguage() throws Exception
	{
		Response response = packageResource.getAllPackagesForLanguage("en",
				1,
				null,
				"false",
				new BigDecimal("1.1"),
				"High",
				"a",
				null);

		Assert.assertEquals(response.getStatus(), 200);

		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

		Document xmlContentsFile = builder.parse(new InputSource((ByteArrayInputStream)response.getEntity()));
		validateContentsXml(xmlContentsFile);
	}

	/**
	 * Tests getting the English (en) version of Knowing God Personally (kgp).
	 *
	 * The unittest database has just one page persisted, but that should be enough
	 * to verify most of what we want to verify.
	 */
	@Test
	public void testGetZippedPackage() throws Exception
	{
		Response response = packageResource.getPackage("en",
				"kgp",
				1,
				null,
				"true",
				new BigDecimal("1.1"),
				"High",
				"a",
				null);

		Assert.assertEquals(response.getStatus(), 200);

		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

		ByteArrayInputStream byteArrayInputStream = (ByteArrayInputStream)response.getEntity();

		ZipInputStream zipInputStream = new ZipInputStream(byteArrayInputStream);

		ZipEntry zipEntry;

		while ((zipEntry = zipInputStream.getNextEntry())!=null)
		{
			if(zipEntry.getName().equals("contents.xml"))
			{
				validateContentsXml(builder.parse(new InputSource(zipInputStream)));
			}
			else if(zipEntry.getName().equals("0fb0b56d1ab3b03bd3587f1f0e3e6c4c1852d729.xml"))
			{
				Document xmlContentsFile = builder.parse(new InputSource(zipInputStream));
				"a".toString();
			}
			else if(zipEntry.getName().equals("1a108ca6462c5a5fb990fd2f0af377330311d0bf.xml"))
			{
				Document xmlContentsFile = builder.parse(new InputSource(zipInputStream));
				"a".toString();
			}
		}
	}

	private void validateContentsXml(Document xmlContentsFile)
	{
		List<Element> resourceElements = XmlDocumentSearchUtilities.findElements(xmlContentsFile, "resource");

		Assert.assertEquals(resourceElements.size(), 1);
		Assert.assertEquals(resourceElements.get(0).getAttribute("language"), "en");
		Assert.assertEquals(resourceElements.get(0).getAttribute("package"), "kgp");
		Assert.assertEquals(resourceElements.get(0).getAttribute("status"), "live");
		Assert.assertEquals(resourceElements.get(0).getAttribute("config"), "d8722200912bad3cbabbfaaa09192c7fd44f4f82.xml");
	}

}
