package org.cru.godtools.api.packages;

import com.google.common.collect.Lists;
import org.cru.godtools.api.packages.domain.Page;
import org.w3c.dom.Document;

import java.util.List;

/**
 * Created by ryancarlson on 3/18/14.
 */
public class GodToolsPackagePage
{
    Document xml;
    String pageHash;
    String originalFilename;

    public GodToolsPackagePage(Page databasePage)
    {
        this.xml = databasePage.getXmlContent();
        this.originalFilename = databasePage.getFilename();
        this.setPageHash(databasePage.getPageHash());
    }

    public GodToolsPackagePage(Document xmlPage, String filename)
    {
        this.xml = xmlPage;
        this.originalFilename = filename;
    }

    public static List<GodToolsPackagePage> createList(List<Page> databasePages)
    {
        List<GodToolsPackagePage> apiPages = Lists.newArrayList();
        for(Page page : databasePages)
        {
            apiPages.add(new GodToolsPackagePage(page));
        }
        return apiPages;
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
