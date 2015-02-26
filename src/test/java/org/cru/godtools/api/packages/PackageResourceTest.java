package org.cru.godtools.api.packages;

import org.ccci.util.xml.XmlDocumentSearchUtilities;
import org.cru.godtools.api.packages.utils.FileZipper;
import org.cru.godtools.api.translations.model.ContentsFile;
import org.cru.godtools.api.translations.model.ResourceElement;
import org.cru.godtools.domain.TestSqlConnectionProducer;
import org.cru.godtools.domain.UnittestDatabaseBuilder;
import org.cru.godtools.domain.authentication.AuthorizationService;
import org.cru.godtools.tests.AbstractFullPackageServiceTest;
import org.cru.godtools.tests.GodToolsPackageServiceTestClassCollection;
import org.cru.godtools.tests.Sql2oTestClassCollection;
import org.cru.godtools.utils.NonClosingZipInputStream;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
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
				.addClasses(PackageResource.class, AuthorizationService.class, FileZipper.class, GodToolsPackageRetrieval.class)
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
	}

	@Inject
	PackageResource packageResource;

	@BeforeClass
	public void initializeDatabase()
	{
		UnittestDatabaseBuilder.build();
	}

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

		validateContentsXml((ContentsFile)response.getEntity());
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

		validateContentsXml((ContentsFile)response.getEntity());
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

		NonClosingZipInputStream zipInputStream = new NonClosingZipInputStream(new ZipInputStream(byteArrayInputStream));

		ZipEntry zipEntry;

		while ((zipEntry = zipInputStream.getNextEntry())!=null)
		{
			if(zipEntry.getName().equals("contents.xml"))
			{
//				validateContentsXml(builder.parse(new InputSource(zipInputStream)));
			}
			else if(zipEntry.getName().equals("1a108ca6462c5a5fb990fd2f0af377330311d0bf.xml"))
			{
				validatePackageConfigXml(builder.parse(new InputSource(zipInputStream)));
			}
			else if(zipEntry.getName().equals("0fb0b56d1ab3b03bd3587f1f0e3e6c4c1852d729.xml"))
			{
				validatePageXml(builder.parse(new InputSource(zipInputStream)));
			}
		}

		zipInputStream.forceClose();
	}

	private void validateContentsXml(ContentsFile xmlContentsFile)
	{
		Assert.assertEquals(xmlContentsFile.getResourceSet().size(), 1);

		ResourceElement firstResource = xmlContentsFile.getResourceSet().iterator().next();

		Assert.assertEquals(firstResource.getLanguage(), "en");
		Assert.assertEquals(firstResource.getPackageCode(), "kgp");
		Assert.assertEquals(firstResource.getStatus(), "live");
		Assert.assertEquals(firstResource.getConfig(), TRANSLATION_ID + ".xml");
		Assert.assertEquals(firstResource.getIcon(), "646dbcad0e235684c4b89c0b82fc7aa8ba3a87b5.png");
	}

	private void validatePackageConfigXml(Document xmlPackageConfigFile)
	{
		List<Element> packageNameElements = XmlDocumentSearchUtilities.findElements(xmlPackageConfigFile, "packagename");
		Assert.assertEquals(packageNameElements.size(), 1);
		Assert.assertEquals(packageNameElements.get(0).getTextContent(), "Knowing God Personally");

		List<Element> pageElements = XmlDocumentSearchUtilities.findElements(xmlPackageConfigFile, "page");
		Assert.assertEquals(pageElements.size(), 1);
		Assert.assertEquals(pageElements.get(0).getAttribute("filename"), "0fb0b56d1ab3b03bd3587f1f0e3e6c4c1852d729.xml");
		Assert.assertEquals(pageElements.get(0).getTextContent(), "Home");

	}

	private void validatePageXml(Document xmlPageFile)
	{
		List<Element> headingElements = XmlDocumentSearchUtilities.findElements(xmlPageFile, "heading");
		Assert.assertEquals(headingElements.size(), 1);
		Assert.assertEquals(headingElements.get(0).getTextContent(), "KNOWING GOD");

		List<Element> subheadingElements = XmlDocumentSearchUtilities.findElements(xmlPageFile, "subheading");
		Assert.assertEquals(subheadingElements.size(), 1);
		Assert.assertEquals(subheadingElements.get(0).getTextContent(), "personally");

		List<Element> textElements = XmlDocumentSearchUtilities.findElements(xmlPageFile, "text");
		Assert.assertEquals(textElements.size(), 1);
		Assert.assertEquals(textElements.get(0).getTextContent(), "These four points explain how to enter into a personal relationship with God and experience the life for which you were created.");
	}
}
