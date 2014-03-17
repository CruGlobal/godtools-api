package org.cru.godtools.api.packages;

import org.w3c.dom.Document;

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

    public Document getContentsFile()
    {
        return contentsFile;
    }

    public Map<String, Document> getPageFiles()
    {
        return pageFiles;
    }
}
