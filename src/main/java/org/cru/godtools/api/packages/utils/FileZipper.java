package org.cru.godtools.api.packages.utils;

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
import java.io.IOException;
import java.util.List;
import java.util.PriorityQueue;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by ryancarlson on 3/18/14.
 */
public class FileZipper
{

    /**
     * The package XML is added to the zipOutputStream.
     *
     * @param godToolsPackage
     * @param zipOutputStream
     * @return
     * @throws IOException
     * @throws TransformerException
     * @throws Exception
     */
    public void zipPackageFile(GodToolsPackage godToolsPackage, ZipOutputStream zipOutputStream) throws IOException, TransformerException, Exception
    {
        zipFile(godToolsPackage.getPackageXml(), godToolsPackage.getPackageXmlHash() + ".xml", zipOutputStream);
    }

    /**
     * The page XML is added to the zipOutputStream.
     *
     * @param godToolsPackage
     * @param zipOutputStream
     * @return
     * @throws IOException
     * @throws TransformerException
     * @throws Exception
     */
    public void zipPageFiles(GodToolsPackage godToolsPackage, ZipOutputStream zipOutputStream) throws Exception
    {
        for(GodToolsPackagePage page : godToolsPackage.getPageFiles())
        {
            zipFile(page.getXml(), page.getPageHash() + ".xml", zipOutputStream);
        }
    }

    public void zipImageFiles(GodToolsPackage godToolsPackage, ZipOutputStream zipOutputStream, PriorityQueue<String> imagesAlreadyZipped) throws IOException
    {
        for(GodToolsPackageImage image : godToolsPackage.getImageFiles())
        {
            if(imagesAlreadyZipped.contains(image.getHash())) continue;
            zipImage(image.getContents(), image.getHash() + ".png", zipOutputStream);
            imagesAlreadyZipped.add(image.getHash());
        }
    }
    /**
     * The contents XML is added to the zipOutputStream.
     *
     * @param contentsFile
     * @param zipOutputStream
     * @return
     * @throws IOException
     * @throws TransformerException
     * @throws Exception
     */
    public void zipContentsFile(Document contentsFile, ZipOutputStream zipOutputStream) throws IOException, TransformerException
    {
        zipFile(contentsFile, "contents.xml", zipOutputStream);
    }

    public void zipFile(Document file, String filename, ZipOutputStream zipOutputStream) throws IOException, TransformerException
    {
        zipOutputStream.putNextEntry(new ZipEntry(filename));

        Source source = new DOMSource(file);
        Result result = new StreamResult(zipOutputStream);
        Transformer transformer = TransformerFactory.newInstance().newTransformer();

        transformer.transform(source, result);

        zipOutputStream.closeEntry();
    }

    public void zipImage(byte[] image, String filename, ZipOutputStream zipOutputStream) throws IOException
    {
        zipOutputStream.putNextEntry(new ZipEntry(filename));

        zipOutputStream.write(image);

        zipOutputStream.closeEntry();
    }
}
