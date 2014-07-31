package org.cru.godtools.domain;

import com.google.common.base.Throwables;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import org.w3c.dom.Document;
import org.ccci.util.xml.XmlDocumentStreamConverter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

/**
 * Created by matthewfrederick on 7/10/14.
 * HashCode Generator using Guava libraries
 */
public class GuavaHashGenerator
{
    private GuavaHashGenerator()
    {
    }

    public static String calculateHash(Document xmlFile)
    {
        try
        {
            HashFunction hf = Hashing.sha1();
            ByteArrayOutputStream byteStream = XmlDocumentStreamConverter.writeToByteArrayStream(xmlFile);
            HashCode hc = hf.newHasher().putBytes(byteStream.toByteArray()).hash();
            return hc.toString();
        } catch (Exception e)
        {
            Throwables.propagate(e);
            return null;
        }
    }

    public static String calculateHash(BufferedImage bufferedImage)
    {
        try
        {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
            byteArrayOutputStream.flush();

            String hash = calculateHash(byteArrayOutputStream.toByteArray());
            byteArrayOutputStream.close();

            return hash;
        } catch (Exception e)
        {
            Throwables.propagate(e);
            return null;
        }
    }

    public static String calculateHash(byte[] image)
    {
        try
        {
            HashFunction hf = Hashing.sha1();
            HashCode hc = hf.newHasher().putBytes(image).hash();
            return hc.toString();

        } catch (Exception e)
        {
            Throwables.propagate(e);
            return null;
        }
    }
}
