package org.cru.godtools.api.packages;

import org.jboss.resteasy.plugins.providers.multipart.MultipartRelatedOutput;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

/**
 * Created by ryancarlson on 3/17/14.
 */
public class PackageResponse
{

    Document contentsFile;
    Map<String, Document> pageFiles;

    public PackageResponse(Document contentsFile, Map<String, Document> pageFiles)
    {
        this.contentsFile = contentsFile;
        this.pageFiles = pageFiles;
    }

    public MultipartRelatedOutput build()
    {
        MultipartRelatedOutput output = new MultipartRelatedOutput();

        convertContentsFile();

        output.addPart(contentsFile, MediaType.valueOf("application/xml"), "foo.xml");

        for(String key : pageFiles.keySet())
        {
            output.addPart(pageFiles.get(key), MediaType.valueOf("application/xml"), getStringHash(pageFiles.get(key)) + ".xml", "");
        }

        return output;
    }

    private void convertContentsFile()
    {
        NodeList pageNodes = contentsFile.getElementsByTagName("page");

        for(int i = 0; i < pageNodes.getLength(); i++)
        {
            Node pageNode = pageNodes.item(i);

            if(pageNode instanceof Element)
            {
                Element pageElement = (Element) pageNode;
                String newFilename = getStringHash(pageFiles.get(pageElement.getAttribute("filename")));
                pageElement.setAttribute("filename", newFilename + ".xml");
            }
        }
    }

    private String getStringHash(Document page)
    {
        return String.valueOf(page.hashCode());
    }
}
