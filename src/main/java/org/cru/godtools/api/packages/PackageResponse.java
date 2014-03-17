package org.cru.godtools.api.packages;

import org.jboss.resteasy.plugins.providers.multipart.MultipartRelatedOutput;
import org.w3c.dom.Document;

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

        output.addPart(contentsFile, MediaType.valueOf("application/xml"), "foo.xml");

        for(String key : pageFiles.keySet())
        {
            output.addPart(pageFiles.get(key), MediaType.valueOf("application/xml"), String.valueOf(pageFiles.get(key).hashCode()) + ".xml", "");
        }

        return output;
    }
}
