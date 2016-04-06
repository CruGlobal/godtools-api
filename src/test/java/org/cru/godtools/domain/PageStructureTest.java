package org.cru.godtools.domain;

import org.cru.godtools.domain.packages.PageStructure;
import org.cru.godtools.tests.AbstractFullPackageServiceTest;
import org.cru.godtools.utils.XmlDocumentFromFile;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.UUID;

/**
 * Created by laelwatkins on 3/29/16.
 */
public class PageStructureTest
{
    @Test
    public void testAddXmlToEnd() throws Exception
    {
        PageStructure pageStructure = new PageStructure();
        pageStructure.setId(UUID.randomUUID());

        String xmlAdditions = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
            "<languages>\n" +
                "<language code=\"en\">\n"+
                    "<package code=\"kgp\" >\n" +
                        "<name>Knowing God Personally</name>\n" +
                    "</package>\n" +
                    "<package code=\"satisfied\">\n " +
                        "<name>Satisfied</name>\n"+
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
        pageStructure.setXmlContent(XmlDocumentFromFile.get("/test_file_1.xml"));
        pageStructure.addXmlContent(additionsXmlDocument);

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        StreamResult result = new StreamResult(new StringWriter());
        DOMSource source = new DOMSource(pageStructure.getXmlContent());
        transformer.transform(source, result);

        String xmlOutput = result.getWriter().toString();
        System.out.println(xmlOutput);

        pageStructure.getXmlContent().normalizeDocument();
        additionsXmlDocument.normalizeDocument();

        boolean theDocumentsAreEqual = false;

        if(pageStructure.getXmlContent().equals(additionsXmlDocument))
            theDocumentsAreEqual = true;

        Assert.assertTrue(theDocumentsAreEqual);
    }

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
                "            <name>Knowing God Personally</name>\n" +
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
                "            <name>Knowing God Personally</name>\n" +
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

        Transformer transformer = TransformerFactory.newInstance().newTransformer();

        StreamResult result = new StreamResult(new StringWriter());
        StreamResult result1 = new StreamResult(new StringWriter());

        pageStructure.getXmlContent().normalizeDocument();
        DOMSource source = new DOMSource(pageStructure.getXmlContent());
        DOMSource source1 = new DOMSource(additionsXmlDocument);

        transformer.transform(source, result);
        transformer.transform(source1, result1);

        String xmlOutput = result.getWriter().toString();
        String xmlOutput2 = result1.getWriter().toString();
        System.out.println(xmlOutput.replace("\\s", "") );
        System.out.println(xmlOutput2.replace("\\s", ""));

        boolean theDocumentsAreEqual = false;

        if(xmlOutput.replace("\\s", "").equals(xmlOutput2.replace("\\s", "")))
            theDocumentsAreEqual = true;

        Assert.assertEquals(xmlOutput.replace("\\s", ""),xmlOutput2.replace("\\s", ""));
    }
}
