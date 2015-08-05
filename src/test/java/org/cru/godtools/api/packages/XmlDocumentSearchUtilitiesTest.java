package org.cru.godtools.api.packages;

import org.ccci.util.xml.XmlDocumentSearchUtilities;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.List;

/**
 * References file src/test/resources/test_file_1.xml
 *
 * Created by ryancarlson on 4/2/14.
 */
public class XmlDocumentSearchUtilitiesTest
{

	@Test
	public void testFindElementsWithAttribute() throws ParserConfigurationException, SAXException, IOException
	{
		List<Element> packageElementsWithCodeAttribute = XmlDocumentSearchUtilities.findElementsWithAttribute(getDocument(), "package", "code");

		Assert.assertEquals(packageElementsWithCodeAttribute.size(), 2);

		for(Element element : packageElementsWithCodeAttribute)
		{
			Assert.assertTrue(element.getAttribute("code").equals("kgp") || element.getAttribute("code").equals("satisfied"));
		}
	}

	@Test
	public void testFindElements() throws ParserConfigurationException, SAXException, IOException
	{
		List<Element> elements = XmlDocumentSearchUtilities.findElements(getDocument(), "name");

		for(Element element : elements)
		{
			Assert.assertTrue(element.getTextContent().equals("Knowing God Personally") || element.getTextContent().equals("Satisfied?"));
		}
	}

	private Document getDocument() throws IOException, SAXException, ParserConfigurationException
	{
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		return builder.parse(this.getClass().getResourceAsStream("/test_file_1.xml"));
	}

}
