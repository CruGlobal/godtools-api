package org.cru.godtools.domain;

import org.ccci.util.xml.XmlDocumentSearchUtilities;
import org.cru.godtools.domain.packages.PageStructure;
import org.cru.godtools.utils.XmlDocumentFromFile;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
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
import java.util.List;
import java.util.UUID;

/**
 * Created by laelwatkins on 3/29/16.
 */
public class PageStructureTest
{
    @Test
    public void testXmlParsing() throws Exception
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
}
