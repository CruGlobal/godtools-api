package org.cru.godtools.domain;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerException;
import org.cru.godtools.domain.packages.PageStructure;
import org.cru.godtools.tests.AbstractFullPackageServiceTest;
import org.cru.godtools.utils.XmlDocumentFromFile;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.UUID;
import org.xml.sax.SAXException;

/**
 * Created by laelwatkins on 3/29/16.
 */
public class PageStructureTest
{
    /**
     * This test validations that the PageStructure.addXmlContent() can
     * add a node to the end of a document successfully.  The original XML document
     * and one with additions should be the same. No changes are made to the existing
     *
     * Expected outcome: AssertEquals true
     *
     * @throws Exception
     */
    @Test
    public void testAddXmlToEnd() throws Exception
    {
        PageStructure pageStructure = new PageStructure();
        pageStructure.setId(UUID.randomUUID());

        String xmlDocument = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<!-- NOTE: this structure does not represent a valid GodTools XML file, just here to provide test cases for various utilities -->\n" +
            "<languages>\n" +
                "<language code=\"en\">\n"+
                    "<package code=\"kgp\" >\n" +
                        "<name>Knowing God Personally</name>\n" +
                    "</package>\n" +
                    "<package code=\"satisfied\">\n " +
                        "<name>Satisfied?</name>\n"+
                    "</package>\n"+
                    "<package code=\"fsl\" >\n" +
                        "<name>4 Spiritual Laws</name>\n" +
                    "</package>\n" +
                    "<package code=\"laws\">\n " +
                        "<name>Laws</name>"+
                    "</package>\n"+
                "</language>\n"+
            "</languages>\n";

        Document additionsXmlDocument = createDocumentFromString(xmlDocument);

        pageStructure.setId(AbstractFullPackageServiceTest.PAGE_STRUCTURE_ID);
        pageStructure.setTranslationId(AbstractFullPackageServiceTest.TRANSLATION_ID);
        pageStructure.setDescription("test_file_1.xml");

        //Set the xmlContent and add additional xml
        pageStructure.setXmlContent(XmlDocumentFromFile.get("/test_file_1.xml"));
        pageStructure.addXmlContent(additionsXmlDocument);

        //convert the docs to strings to make the comparison easier in case
        //an xml element/tag is on the same line as it's sibling or child text
        String originalXML = domDocumentToString(pageStructure.getXmlContent());
        String additionsXML = domDocumentToString(additionsXmlDocument);

        boolean theDocumentsAreEqual = originalXML.equals(additionsXML);

        Assert.assertTrue(theDocumentsAreEqual);
    }

    /**
     * This test validations that the PageStructure.addXmlContent() can
     * insert a node to a document successfully when a first sibling node is present.  The PageStructure.xmlContent document
     * will now have the new nodes from the additionsXmlDocument. No updates are made to the existing
     * XML Document. Only Additions.
     *
     * Expected outcome: AssertEquals true
     *
     */
    @Test
    public void testAddXmlInsertAfterFirstSibling() throws Exception
    {
        PageStructure pageStructure = new PageStructure();
        pageStructure.setId(UUID.randomUUID());

        String xmlOriginal = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
        "<page color=\"#00759A\" watermark=\"Home_Watermark.png\">\n"+
        "<text color=\"#FFFFFF\" gtapi-trx-id=\"da76705f-187d-4083-8677-593a1b7c58fd\" size=\"136\" textalign=\"center\" translate=\"true\" w=\"300\" xoffset=\"40\" y=\"50\" />\n"+
        "<text color=\"#FFFFFF\" gtapi-trx-id=\"be73de97-89fe-4490-a686-220546e2592c\" modifier=\"bold\" size=\"156\" textalign=\"center\" translate=\"true\" w=\"300\" yoffset=\"-50\" />\n"+
        "<text color=\"#FFFFFF\" gtapi-trx-id=\"404edd90-1aff-4bb9-83a2-f0d15c46a6e8\" modifier=\"bold\" size=\"156\" textalign=\"center\" translate=\"true\" w=\"300\" xoffset=\"-34\" yoffset=\"-50\" />\n"+
        "<text alpha=\"0.8\" color=\"#ffffff\" gtapi-trx-id=\"d4b40701-5929-498b-b96e-cc6796d76771\" modifier=\"italics\" size=\"112\" textalign=\"center\" translate=\"true\" w=\"300\" yoffset=\"140\" />\n"+
        "</page>";

        String xmlAdditions = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
                "<page color=\"#00759A\" watermark=\"Home_Watermark.png\">\n"+
                "<text color=\"#FFFFFF\" gtapi-trx-id=\"da76705f-187d-4083-8677-593a1b7c58fd\" size=\"136\" textalign=\"center\" translate=\"true\" w=\"300\" xoffset=\"40\" y=\"50\" />\n"+
                "<text color=\"#FFFFFF\" gtapi-trx-id=\"ua10000f-187d-4083-8677-593a1t7c58fd\" size=\"136\" textalign=\"center\" translate=\"true\" w=\"300\" xoffset=\"300\" y=\"50\" />\n"+
                "<text color=\"#FFFFFF\" gtapi-trx-id=\"be73de97-89fe-4490-a686-220546e2592c\" modifier=\"bold\" size=\"156\" textalign=\"center\" translate=\"true\" w=\"300\" yoffset=\"-50\" />\n"+
                "<text color=\"#FFFFFF\" gtapi-trx-id=\"404edd90-1aff-4bb9-83a2-f0d15c46a6e8\" modifier=\"bold\" size=\"156\" textalign=\"center\" translate=\"true\" w=\"300\" xoffset=\"-34\" yoffset=\"-50\" />\n"+
                "<text alpha=\"0.8\" color=\"#ffffff\" gtapi-trx-id=\"d4b40701-5929-498b-b96e-cc6796d76771\" modifier=\"italics\" size=\"112\" textalign=\"center\" translate=\"true\" w=\"300\" yoffset=\"140\" />\n"+
                "</page>";

        Document originalXmlDocument = createDocumentFromString(xmlOriginal);
        Document additionsXmlDocument = createDocumentFromString(xmlAdditions);

        pageStructure.setXmlContent(originalXmlDocument);
        pageStructure.addXmlContent(additionsXmlDocument);

        //convert the doc to a string to make the comparison easier in case
        //an xml element/tag is on the same line as it's sibling,child text, etc.
        String originalXML = domDocumentToString(pageStructure.getXmlContent());
        String additionalXML = domDocumentToString(additionsXmlDocument);

        Assert.assertEquals(originalXML, additionalXML);
    }


    /**
     * This test validations that the PageStructure.addXmlContent() can
     * insert a node to a document successfully when the new node is the first child.  The PageStructure.xmlContent document
     * will now have the new nodes from the additionsXmlDocument. No updates are made to the existing
     * XML Document. Only Additions.
     *
     * Expected outcome: AssertEquals true
     *
     */
    @Test
    public void testAddXmlInsertAsFirstChild() throws Exception
    {
        PageStructure pageStructure = new PageStructure();
        pageStructure.setId(UUID.randomUUID());

        String xmlOriginal = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
                "<page backgroundimage=\"wave.png\" color=\"#DBF0FC\">\n" +
                "<text color=\"#007486\" gtapi-trx-id=\"34cc87cd-64cd-49a9-a12b-b6b06e4acdbf\" modifier=\"bold\" size=\"100\" textalign=\"center\" translate=\"true\" w=\"300\" y=\"200\"> </text>\n" +
                "<text color=\"#007486\" gtapi-trx-id=\"99a12a3b-0b53-4059-92eb-53496438c3de\" modifier=\"italics\" size=\"240\" textalign=\"center\" translate=\"true\" w=\"300\" y=\"245\"> </text>\n" +
                "</page>";

        String xmlAddition = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
                "<page backgroundimage=\"wave.png\" color=\"#DBF0FC\">\n" +
                "<text color=\"#007486\" gtapi-trx-id=\"364fb3eb-7a2d-40a3-bd19-32412af6df70\" modifier=\"italics\" size=\"340\" textalign=\"center\" translate=\"true\" w=\"500\" y=\"945\"> </text>\n" +
                "<text color=\"#007486\" gtapi-trx-id=\"34cc87cd-64cd-49a9-a12b-b6b06e4acdbf\" modifier=\"bold\" size=\"100\" textalign=\"center\" translate=\"true\" w=\"300\" y=\"200\"> </text>\n" +
                "<text color=\"#007486\" gtapi-trx-id=\"99a12a3b-0b53-4059-92eb-53496438c3de\" modifier=\"italics\" size=\"240\" textalign=\"center\" translate=\"true\" w=\"300\" y=\"245\"> </text>\n" +
                "</page>";

        Document originalXmlDocument = createDocumentFromString(xmlOriginal);
        Document additionsXmlDocument = createDocumentFromString(xmlAddition);

        pageStructure.setXmlContent(originalXmlDocument);
        pageStructure.addXmlContent(additionsXmlDocument);

        //convert the doc to a string to make the comparison easier in case
        //an xml element/tag is on the same line as it's sibling,child text, etc.
        String originalXML = domDocumentToString(pageStructure.getXmlContent());
        String additionalXML = domDocumentToString(additionsXmlDocument);

        Assert.assertEquals(originalXML, additionalXML);
    }

    private String domDocumentToString(Document document) throws IOException,TransformerException
    {

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        StreamResult streamResult = new StreamResult(new StringWriter());
        DOMSource source = new DOMSource(document);

        transformer.transform(source, streamResult);

        BufferedReader bufferedReader  = new BufferedReader(new StringReader(streamResult.getWriter().toString()));;
        StringBuilder stringBuffer = new StringBuilder();

        String line;

        while((line = bufferedReader.readLine())  != null)
        {
            stringBuffer.append(line.trim());
        }

        return stringBuffer.toString();

    }

    private Document createDocumentFromString(String documentString) throws IOException, ParserConfigurationException, SAXException
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new InputSource(new StringReader(documentString)));
    }

    private XMLEventReader getXmlEventReaderFromByteArray(ByteArrayOutputStream byteArrayOutputStream) throws XMLStreamException
    {
        InputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        XMLEventReader xmlEventReader = xmlInputFactory.createXMLEventReader(inputStream);

        return xmlEventReader;
    }
}
