package org.cru.godtools.domain;

import java.io.BufferedReader;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.cru.godtools.domain.packages.PageStructure;
import org.cru.godtools.tests.AbstractFullPackageServiceTest;
import org.cru.godtools.utils.XmlDocumentFromFile;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
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

        String xmlAdditions = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
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

        DocumentBuilderFactory factory = DocumentBuilderFactory
                .newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder;
        builder = factory.newDocumentBuilder();
        Document additionsXmlDocument = builder.parse(new InputSource(new StringReader(xmlAdditions)));

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

        System.out.println(originalXML);
        System.out.println(additionsXML);

        boolean theDocumentsAreEqual = originalXML.equals(additionsXML);

        Assert.assertTrue(theDocumentsAreEqual);
    }

    /**
     * This test validations that the PageStructure.addXmlContent() can
     * insert a node to a document successfully.  The PageStructure.xmlContent document
     * will now have the new nodes from the additionsXmlDocument. No updates are made to the existing
     * XML Document. Only Additions.
     *
     * Expected outcome: AssertEquals true
     *
     */
    @Test
    public void testAddXmlInsert() throws Exception
    {
        PageStructure pageStructure = new PageStructure();
        pageStructure.setId(UUID.randomUUID());

        String xmlAdditions = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<!-- NOTE: this structure does not represent a valid GodTools XML file, just here to provide test cases for various utilities -->\n" +
                "<languages>\n" +
                "    <language code=\"en\">\n" +
                "        <package code=\"kgp\" >\n" +
                "            <name>Knowing God</name>\n" +  //This node is different than the additionsXML. but it won't change or be replaced.
                "        </package>\n" +
                "        <package code=\"satisfied\">\n" +
                "            <name>Satisfied?</name>\n" +
                "        </package>\n" +
                "    </language>\n" +
                "</languages>";

        String xmlAdditions2 = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<!-- NOTE: this structure does not represent a valid GodTools XML file, just here to provide test cases for various utilities -->\n" +
                "<languages>\n" +
                "    <language code=\"en\">\n" +
                "        <package code=\"kgp\" >\n" +
                "            <name>Knowing God Personally</name>\n" +  //Different
                "        </package>\n" +
                "        <package code=\"satisfied\">\n" +
                "            <name>Satisfied?</name>\n" +
                "        </package>\n" +
                "        <package code=\"fourlaws\">\n" +
                "            <name>Four Laws</name>\n" +
                "        </package>\n" +
                "    </language>\n" +
                "</languages>";

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilderFactory factory2 = DocumentBuilderFactory.newInstance();

        DocumentBuilder builder;
        DocumentBuilder builder2;

        builder = factory.newDocumentBuilder();
        builder2 = factory2.newDocumentBuilder();

        Document originalXmlDocument = builder.parse(new InputSource(new StringReader(xmlAdditions)));
        Document additionsXmlDocument = builder2.parse(new InputSource(new StringReader(xmlAdditions2)));

        originalXmlDocument.normalize();
        additionsXmlDocument.normalize();

        pageStructure.setXmlContent(originalXmlDocument);
        pageStructure.addXmlContent(additionsXmlDocument);

        //convert the doc to a string to make the comparison easier in case
        //an xml element/tag is on the same line as it's sibling,child text, etc.
        String originalXML = domDocumentToString(pageStructure.getXmlContent());

        boolean theNewNodesArePresent = originalXML.contains("<package code=\"fourlaws\"><name>Four Laws</name></package>");

        Assert.assertTrue(theNewNodesArePresent);
    }

    /**
     * This test validations that the PageStructure.removeXmlContent() can
     * remove a node from a document successfully.
     *
     * Expected outcome: document contains node AssertFalse
     *
     */
    @Test
    public void testRemoveElement() throws ParserConfigurationException, IOException, SAXException,TransformerException
    {
        PageStructure pageStructure = new PageStructure();
        pageStructure.setId(UUID.randomUUID());

        String xmlAdditions = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<!-- NOTE: this structure does not represent a valid GodTools XML file, just here to provide test cases for various utilities -->\n" +
                "<languages>\n" +
                "    <language code=\"en\">\n" +
                "        <package code=\"kgp\" >\n" +
                "            <name>Knowing God</name>\n" +
                "        </package>\n" +
                "        <package code=\"satisfied\">\n" +
                "            <name>Satisfied?</name>\n" +
                "        </package>\n" +
                "    </language>\n" +
                "</languages>";

        String xmlAdditions2 = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<!-- NOTE: this structure does not represent a valid GodTools XML file, just here to provide test cases for various utilities -->\n" +
                "<languages>\n" +
                "    <language code=\"en\">\n" +
                "        <package code=\"kgp\" >\n" +
                "            <name>Knowing God</name>\n" +
                "        </package>\n"+
                "    </language>\n" +
                "</languages>";

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilderFactory factory2 = DocumentBuilderFactory.newInstance();

        DocumentBuilder builder;
        DocumentBuilder builder2;

        builder = factory.newDocumentBuilder();
        builder2 = factory2.newDocumentBuilder();

        Document originalXmlDocument = builder.parse(new InputSource(new StringReader(xmlAdditions)));
        Document additionsXmlDocument = builder2.parse(new InputSource(new StringReader(xmlAdditions2)));

        originalXmlDocument.normalize();
        additionsXmlDocument.normalize();

        pageStructure.setXmlContent(originalXmlDocument);
        pageStructure.removeXmlContent(additionsXmlDocument);

        //convert the doc to a string to make the comparison easier in case
        //an xml element/tag is on the same line as it's sibling,child text, etc.
        String originalXML = domDocumentToString(pageStructure.getXmlContent());

        Assert.assertFalse(originalXML.contains("<package code=\"kgp\"><name>Knowing God</name></package>"));
    }


    public String domDocumentToString(Document document) throws IOException,TransformerException
    {

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        StreamResult streamResult = new StreamResult(new StringWriter());
        DOMSource source = new DOMSource(document);

        transformer.transform(source, streamResult);

        BufferedReader bufferedReader  = new BufferedReader(new StringReader(streamResult.getWriter().toString()));;
        StringBuffer stringBuffer = new StringBuffer();

        String line;

        while((line = bufferedReader.readLine())  != null)
        {
            stringBuffer.append(line.trim());
        }

        return stringBuffer.toString();

    }
}
