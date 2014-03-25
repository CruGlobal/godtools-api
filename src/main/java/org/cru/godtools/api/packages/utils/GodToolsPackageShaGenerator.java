package org.cru.godtools.api.packages.utils;

import com.google.common.base.Throwables;
import org.cru.godtools.api.packages.GodToolsPackage;
import org.cru.godtools.api.packages.GodToolsPackageImage;
import org.cru.godtools.api.packages.GodToolsPackagePage;
import org.w3c.dom.Document;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;

import java.util.Set;

/**
 * Created by ryancarlson on 3/18/14.
 */
public class GodToolsPackageShaGenerator
{

    public void setHashes(GodToolsPackage godToolsPackage)
    {
        godToolsPackage.setPackageXmlHash(calculateHash(godToolsPackage.getPackageXml()));

        for(GodToolsPackagePage page : godToolsPackage.getPageFiles())
        {
            page.setPageHash(calculateHash(page.getXml()));
        }

        for(GodToolsPackageImage image: godToolsPackage.getImageFiles())
        {
            image.setHash(calculateHash(image.getContents()));
        }
    }

    public void setHashes(Set<GodToolsPackage> godToolsPackages)
    {
        for(GodToolsPackage godToolsPackage : godToolsPackages)
        {
            setHashes(godToolsPackage);
        }
    }

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
            return null;
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
