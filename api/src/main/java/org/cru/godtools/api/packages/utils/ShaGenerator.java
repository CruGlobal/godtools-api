package org.cru.godtools.api.packages.utils;

import com.google.common.base.Throwables;
import org.w3c.dom.Document;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;

/**
 * Created by ryancarlson on 3/18/14.
 */
public class ShaGenerator
{
	private ShaGenerator()
	{

	}

    public static String calculateHash(Document xmlFile)
    {
        try
        {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            ByteArrayOutputStream byteStream = XmlDocumentStreamConverter.xmlToStream(xmlFile);

            messageDigest.update(byteStream.toByteArray());

            return calculateHash(messageDigest.digest());
        }
        catch(Exception e)
        {
            Throwables.propagate(e);
            return null; /*unreachable*/
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
		}
		catch(Exception e)
		{
			Throwables.propagate(e);
			return null;
		}
	}

    public static String calculateHash(byte[] image)
    {
        try
        {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");

            messageDigest.update(image);

            byte[] messageDigestBytes = messageDigest.digest();

            StringBuffer hexString = new StringBuffer();

            for(int i=0; i < messageDigestBytes.length; i++)
            {
                hexString.append(Integer.toHexString(0xFF & messageDigestBytes[i]));
            }

            return hexString.toString();
        }
        catch(Exception e)
        {
            Throwables.propagate(e);
            return null; /*unreachable*/
        }
    }

}
