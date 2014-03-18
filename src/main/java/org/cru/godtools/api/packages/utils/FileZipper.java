package org.cru.godtools.api.packages.utils;

import org.w3c.dom.Document;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by ryancarlson on 3/18/14.
 */
public class FileZipper
{
    public String zipPackageFile(Document packageFile, ZipOutputStream zipOutputStream) throws IOException, TransformerException, Exception
    {
        String checksum = new FileChecksumGenerator().getChecksum(packageFile);

        zipFile(packageFile, checksum + ".xml", zipOutputStream);

        return checksum;
    }

    public void zipPageFiles(Map<String,Document> packagePages, ZipOutputStream zipOutputStream) throws Exception
    {
        for(String key : packagePages.keySet())
        {
            String checksum = new FileChecksumGenerator().getChecksum(packagePages.get(key));

            zipFile(packagePages.get(key), checksum + ".xml", zipOutputStream);
        }
    }

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
}
