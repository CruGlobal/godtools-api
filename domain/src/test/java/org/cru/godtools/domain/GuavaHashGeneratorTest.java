package org.cru.godtools.domain;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

/**
 * Created by ryancarlson on 4/2/14.
 */
public class GuavaHashGeneratorTest
{

    @Test
    public void testCalculateHashOnFileWithGuava() throws IOException, SAXException, ParserConfigurationException
    {
		InputStream inputStream = this.getClass().getResourceAsStream("/test_file_1.xml");
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document testFile = builder.parse(inputStream);

        Assert.assertNotNull(testFile);
        Assert.assertEquals(GuavaHashGenerator.calculateHash(testFile), "f9448a4c925e3c45a4f8077c8a5dfce0aeabed38");
    }

    @Test
    public void testCalculateHashOnImageWithGuava() throws URISyntaxException, IOException
    {
        BufferedImage bufferedImage = ImageIO.read(this.getClass().getResourceAsStream("/test_image_1.png"));
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
        Assert.assertEquals(GuavaHashGenerator.calculateHash(byteArrayOutputStream.toByteArray()), "646dbcad0e235684c4b89c0b82fc7aa8ba3a87b5");
    }
}
