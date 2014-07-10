package org.cru.godtools.api.packages.utils;

import org.cru.godtools.migration.ImageReader;
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
    public void testCalculateHashOnFileWithGuava() throws IOException, SAXException, ParserConfigurationException
    {
        Document testFile = XmlDocumentFromFile.get("/test_file_1.xml");

        Assert.assertNotNull(testFile);
        Assert.assertEquals(GuavHashGenerator.calculateHash(testFile), "f9448a4c925e3c45a4f8077c8a5dfce0aeabed38");
    }

    @Test
    public void testCalculateHashOnImageWithGuava() throws URISyntaxException, IOException
    {
        byte[] imageBytes = ImageReader.read(new File(this.getClass().getResource("/test_image_1.png").toURI()));
        Assert.assertEquals(GuavHashGenerator.calculateHash(imageBytes), "2499a25070946a302a1aa7c0f401b4c7015d710d");
    }
}
