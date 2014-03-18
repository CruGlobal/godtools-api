package org.cru.godtools.api.packages;

import com.google.common.base.Throwables;
import org.jboss.resteasy.plugins.providers.multipart.MultipartRelatedOutput;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by ryancarlson on 3/17/14.
 */
public class AssemblePackageProcess
{
    final String languageCode;
    final String packageCode;

    MockPackageService packageService;

    public AssemblePackageProcess(MockPackageService packageService, String languageCode, String packageCode)
    {
        this.packageService = packageService;
        this.languageCode = languageCode;
        this.packageCode = packageCode;
    }

    public Response buildZippedResponse() throws IOException
    {
        try
        {
            Document  contentsFile = packageService.getContentsFile(languageCode, packageCode);
            Map<String, Document> packagePages = packageService.getPageFiles(languageCode, packageCode, new PageFilenameList().fromContentsFile(contentsFile));

            ByteArrayOutputStream bundledStream = new ByteArrayOutputStream();
            ZipOutputStream zipOutputStream = new ZipOutputStream(bundledStream);
            FileZipper fileZipper = new FileZipper();

            fileZipper.zipContentsFile(contentsFile, zipOutputStream);

            fileZipper.zipPageFiles(packagePages, zipOutputStream);

            zipOutputStream.close();

            bundledStream.close();

            return Response.ok(new ByteArrayInputStream(bundledStream.toByteArray())).build();
        }
        catch(Exception e)
        {
            Throwables.propagate(e);
            return null;
        }
    }



//    private void convertContentsFile()
//    {
//        NodeList pageNodes = contentsFile.getElementsByTagName("page");
//
//        for(int i = 0; i < pageNodes.getLength(); i++)
//        {
//            Node pageNode = pageNodes.item(i);
//
//            if(pageNode instanceof Element)
//            {
//                Element pageElement = (Element) pageNode;
//                String newFilename = getStringHash(packagePages.get(pageElement.getAttribute("filename")));
//                pageElement.setAttribute("filename", newFilename + ".xml");
//            }
//        }
//    }

    private String getStringHash(Document page)
    {
        return String.valueOf(page.hashCode());
    }
}
