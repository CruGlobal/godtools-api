package org.cru.godtools.api.packages.utils;

import org.ccci.util.ShaGenerator;
import org.cru.godtools.domain.images.Image;
import org.cru.godtools.domain.packages.PackageStructure;
import org.cru.godtools.domain.packages.PageStructure;
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
     * @param zipOutputStream
     * @return
     * @throws IOException
     * @throws TransformerException
     * @throws Exception
     */
    public void zipPackageFile(PackageStructure packageStructure, ZipOutputStream zipOutputStream) throws IOException, TransformerException, Exception
    {
		Document xmlContent = packageStructure.getXmlContent();
		zipFile(xmlContent, ShaGenerator.calculateHash(xmlContent) + ".xml", zipOutputStream);
    }

    /**
     * The page XML is added to the zipOutputStream.
     *
     * @param zipOutputStream
     * @return
     * @throws IOException
     * @throws TransformerException
     * @throws Exception
     */
    public void zipPageFiles(List<PageStructure> pageStructureList, ZipOutputStream zipOutputStream) throws Exception
    {
        for(PageStructure page : pageStructureList)
        {
			Document xmlContent = page.getXmlContent();

			if(xmlContent == null) continue;

			zipFile(xmlContent, ShaGenerator.calculateHash(xmlContent) + ".xml", zipOutputStream);
        }
    }

    public void zipImageFiles(List<Image> images, ZipOutputStream zipOutputStream, PriorityQueue<String> imagesAlreadyZipped) throws IOException
    {
        for(Image image : images)
        {
			String imageHash = ShaGenerator.calculateHash(image.getImageContent());

			if(imagesAlreadyZipped.contains(imageHash)) continue;
            zipImage(image.getImageContent(), imageHash + ".png", zipOutputStream);
            imagesAlreadyZipped.add(imageHash);
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
