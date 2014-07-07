package org.cru.godtools.api.packages.utils;

import org.cru.godtools.api.utilities.ImageReader;
import org.cru.godtools.tests.XmlDocumentFromFile;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by ryancarlson on 4/2/14.
 */
public class GodToolsPackageShaGeneratorTest
{

	@Test
	public void testCalculateHashOnFile() throws IOException, SAXException, ParserConfigurationException
	{

		Document testFile = XmlDocumentFromFile.get("/test_file_1.xml");

		Assert.assertNotNull(testFile);
		Assert.assertEquals(ShaGenerator.calculateHash(testFile), "89795d3c74cb8b1bc0211bc13413ba258e81ac3c");
	}

	@Test
	public void testCalculateHashOnImage() throws URISyntaxException, IOException
	{

		byte[] imageBytes = ImageReader.read(new File(this.getClass().getResource("/test_image_1.png").toURI()));

		Assert.assertEquals(ShaGenerator.calculateHash(imageBytes), "60b4fcbdfdd834a39bdcedf987a66c5f42a9143a");

	}
}
