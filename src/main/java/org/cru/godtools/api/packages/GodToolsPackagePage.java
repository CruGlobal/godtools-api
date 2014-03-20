package org.cru.godtools.api.packages;

import org.w3c.dom.Document;

/**
 * Created by ryancarlson on 3/18/14.
 */
public class GodToolsPackagePage
{
    Document xml;
    String pageHash;
    String originalFilename;

    public GodToolsPackagePage(Document xml, String originalFilename)
    {
        this.xml = xml;
        this.originalFilename = originalFilename;
    }

    public Document getXml()
    {
        return xml;
    }

    public void setXml(Document xml)
    {
        this.xml = xml;
    }

    public String getPageHash()
    {
        return pageHash;
    }

    public void setPageHash(String pageHash)
    {
        this.pageHash = pageHash;
    }

    public String getOriginalFilename()
    {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename)
    {
        this.originalFilename = originalFilename;
    }
}
