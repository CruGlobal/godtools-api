package org.cru.godtools.api.packages.utils;

import com.google.common.base.Throwables;
import org.w3c.dom.Document;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;

/**
 * Created by ryancarlson on 3/18/14.
 */
public class GodToolsPackageShaGenerator
{
    public String calculateHash(Document xmlFile)
    {
        try
        {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            ByteArrayOutputStream byteStream = XmlDocumentStreamConverter.convert(xmlFile);

            messageDigest.update(byteStream.toByteArray());

            return calculateHash(messageDigest.digest());
        }
        catch(Exception e)
        {
            Throwables.propagate(e);
            return null; /*unreachable*/
        }
    }

    public String calculateHash(byte[] image)
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
