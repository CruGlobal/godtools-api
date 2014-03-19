package org.cru.godtools.api.packages;

import org.cru.godtools.api.packages.utils.XmlFileHasher;
import org.w3c.dom.Document;

/**
 * Created by ryancarlson on 3/18/14.
 */
public class GodToolsPackagePage
{
    Document xml;
    String pageHash;

    public GodToolsPackagePage(Document xml)
    {
        this.xml = xml;
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
}
