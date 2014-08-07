package org.cru.godtools.api.translations;

import com.google.common.base.Throwables;
import org.ccci.util.xml.XmlDocumentSearchUtilities;
import org.cru.godtools.api.packages.utils.FileZipper;
import org.cru.godtools.domain.TestSqlConnectionProducer;
import org.cru.godtools.domain.UnittestDatabaseBuilder;
import org.cru.godtools.domain.authentication.AuthorizationService;
import org.cru.godtools.domain.authentication.UnauthorizedException;
import org.cru.godtools.domain.packages.PageStructureService;
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
import org.xml.sax.SAXException;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by ryancarlson on 7/31/14.
 */
public class TranslationResourceTest extends AbstractFullPackageServiceTest
{

	private DocumentBuilder documentBuilder;

	@Deployment
	public static WebArchive createDeployment()
	{
		return ShrinkWrap.create(WebArchive.class)
				.addClasses(Sql2oTestClassCollection.getClasses())
				.addClasses(GodToolsPackageServiceTestClassCollection.getClasses())
				.addClasses(DraftResource.class, TranslationResource.class, AuthorizationService.class, FileZipper.class, GodToolsTranslationRetrieval.class)
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
	}

	@Inject
	TranslationResource translationResource;

	// used to get list of pageStructure Ids
	@Inject
	PageStructureService pageStructureService;

	// used to validate results of createTranslation test
	@Inject
	DraftResource draftResource;

	@BeforeClass
	public void initializeDatabase()
	{
		UnittestDatabaseBuilder.build();
	}

	@BeforeClass
	public void createDocumentBuilder() throws ParserConfigurationException
	{
		documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
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

	@Test
	public void testGetTranslation() throws Exception
	{
		Response response = translationResource.getTranslation("en", "kgp", 1, null, "false", new BigDecimal("1.1"), "a", null);

		Assert.assertEquals(response.getStatus(), 200);

		validateContentsXml(documentBuilder.parse(new InputSource((ByteArrayInputStream)response.getEntity())));
	}

	/*
	 * Tests getting all the English (en) translations
	 *
	 * Currently there is just one, kgp.
	 *
	 * The unittest database has just one page persisted, but that should be enough
	 * to verify most of what we want to verify.
	 */
	@Test
	public void testGetPackagesForLanguage() throws Exception
	{
		Response response = translationResource.getTranslations("en",
				1,
				null,
				"false",
				"a",
				null);

		Assert.assertEquals(response.getStatus(), 200);

		validateContentsXml(documentBuilder.parse(new InputSource((ByteArrayInputStream)response.getEntity())));
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
		Response response = translationResource.getTranslation("en", "kgp", 1, null, "true", new BigDecimal("1.1"), "a", null);

		Assert.assertEquals(response.getStatus(), 200);

		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

		ByteArrayInputStream byteArrayInputStream = (ByteArrayInputStream)response.getEntity();

		NonClosingZipInputStream zipInputStream = new NonClosingZipInputStream(new ZipInputStream(byteArrayInputStream));

		ZipEntry zipEntry;

		while ((zipEntry = zipInputStream.getNextEntry())!=null)
		{
			if(zipEntry.getName().equals("contents.xml"))
			{
				validateContentsXml(builder.parse(new InputSource(zipInputStream)));
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

	@Test
	public void testCreateTranslationDifferentLanguage() throws URISyntaxException, IOException, SAXException
	{
		Response response = translationResource.createTranslation("fr",
				"kgp",
				1,
				null,
				"draft-access",
				null);

		Assert.assertEquals(response.getStatus(), 201);

		Response getDraftResponse = draftResource.getTranslation("fr",
				"kgp",
				1,
				null,
				"false",
				new BigDecimal("1.1"),
				"draft-access",
				null);

		Assert.assertEquals(getDraftResponse.getStatus(), 200);

		validateDraftXml(documentBuilder.parse(new InputSource((ByteArrayInputStream) getDraftResponse.getEntity())), "fr");
	}

	@Test
	public void testCreateTranslationSameLanguage() throws URISyntaxException, IOException, SAXException
	{
		Response response = translationResource.createTranslation("en",
				"kgp",
				1,
				null,
				"draft-access",
				null);

		Assert.assertEquals(response.getStatus(), 201);

		Response getDraftResponse = draftResource.getTranslation("en",
				"kgp",
				1,
				null,
				"false",
				new BigDecimal("1.2"),
				"draft-access",
				null);

		Assert.assertEquals(getDraftResponse.getStatus(), 200);

		validateDraftXml(documentBuilder.parse(new InputSource((ByteArrayInputStream) getDraftResponse.getEntity())), "en");
	}

	@Test(expectedExceptions = UnauthorizedException.class)
	public void testCreateTranslationUnauthorized() throws URISyntaxException, IOException, SAXException
	{
		translationResource.createTranslation("en",
				"kgp",
				1,
				null,
				"1",
				null);
	}

	@Test
	public void testPublishDraftTranslation() throws URISyntaxException, IOException, SAXException
	{
		Response response = translationResource.updateTranslation("en",
				"kgp",
				"draft-access",
				null,
				"true");

		Assert.assertEquals(response.getStatus(), 204);

		Response publishedTranslationResponse = translationResource.getTranslation("en",
				"kgp",
				1,
				null,
				"false",
				new BigDecimal("1.2"),
				"a",
				null);

		Assert.assertEquals(publishedTranslationResponse.getStatus(), 200);
		validateContentsXml(documentBuilder.parse(new InputSource((ByteArrayInputStream) publishedTranslationResponse.getEntity())));

	}

	@Test
	public void testGetAllPageStructures() throws Exception
	{
		Response response = translationResource.getAllPageStructures("en", "kgp", null, "a", null);
		Assert.assertEquals(response.getStatus(), 200);
		validatePageStructureXml(documentBuilder.parse(new InputSource((ByteArrayInputStream) response.getEntity())));
	}

	@Test
	public void testUpdateAndGetPageStructure() throws Exception
	{
		List<UUID> ids = pageStructureService.selectAllPageStructureIds();
		UUID page = ids.get(0);
		Document testDoc = createXMLDocument();

		Response updatePageStructureResponse = translationResource.updatePageStructure(testDoc, "en", "kgp", page ,"draft-access", null);
		Assert.assertEquals(updatePageStructureResponse.getStatus(), 204);

		Response getPageStructureResponse = translationResource.getPageStructure("en", "kgp", page, "draft-access", null);
		Assert.assertEquals(getPageStructureResponse.getStatus(), 200);
	}

	private void validatePageStructureXml(Document xmlPageStructureFile)
	{
		/*
		Since the database is rolled back each time a test is run, the only data stored is test data.
		This is why only one element will be returned when the test is run.
		 */

		List<Element> elements = XmlDocumentSearchUtilities.findElements(xmlPageStructureFile, "Page");

		Assert.assertEquals(elements.size(), 1);
		Assert.assertEquals(elements.get(0).getAttribute("ID"), PAGE_STRUCTURE_ID.toString());
		Assert.assertEquals(elements.get(0).getAttribute("Translation_ID"), TRANSLATION_ID.toString());
	}

	private void validateDraftXml(Document xmlDraftContentsFile, String languageCode)
	{
		List<Element> resourceElements = XmlDocumentSearchUtilities.findElements(xmlDraftContentsFile, "resource");

		Assert.assertEquals(resourceElements.size(), 1);
		Assert.assertEquals(resourceElements.get(0).getAttribute("language"), languageCode);
		Assert.assertEquals(resourceElements.get(0).getAttribute("package"), "kgp");
		Assert.assertEquals(resourceElements.get(0).getAttribute("status"), "draft");
		Assert.assertEquals(resourceElements.get(0).getAttribute("config"), "1a108ca6462c5a5fb990fd2f0af377330311d0bf.xml");
		Assert.assertEquals(resourceElements.get(0).getAttribute("icon"), "646dbcad0e235684c4b89c0b82fc7aa8ba3a87b5.png");

	}

	private void validateContentsXml(Document xmlContentsFile)
	{
		List<Element> resourceElements = XmlDocumentSearchUtilities.findElements(xmlContentsFile, "resource");

		Assert.assertEquals(resourceElements.size(), 1);
		Assert.assertEquals(resourceElements.get(0).getAttribute("language"), "en");
		Assert.assertEquals(resourceElements.get(0).getAttribute("package"), "kgp");
		Assert.assertEquals(resourceElements.get(0).getAttribute("status"), "live");
		Assert.assertEquals(resourceElements.get(0).getAttribute("config"), "1a108ca6462c5a5fb990fd2f0af377330311d0bf.xml");
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

	private Document createXMLDocument()
	{
		try
		{
			Document contents = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			Element root = contents.createElement("Test_Element");
			root.setAttribute("Test", "Result");
			contents.appendChild(root);

			return contents;

		} catch (ParserConfigurationException e)
		{
			throw Throwables.propagate(e);
		}
	}
}
