package org.cru.godtools.api.packages;

import com.google.common.collect.Lists;
import org.w3c.dom.Document;

import java.util.List;
import java.util.Map;

/**
 * Created by ryancarlson on 3/18/14.
 */
public class GodToolsPackage
{
    Document packageXml;
    List<GodToolsPackagePage> pageFiles;
    String languageCode;
    String packageCode;

    String packageXmlHash;

    public GodToolsPackage(Document packageXml, List<Document> xmlPageFiles, String languageCode, String packageCode)
    {
        this.packageXml = packageXml;
        this.languageCode = languageCode;
        this.packageCode = packageCode;

        hashPages(xmlPageFiles);
    }

    private void hashPages(List<Document> xmlPageFiles)
    {
        pageFiles = Lists.newArrayList();

        for(Document xmlPage : xmlPageFiles)
        {
            pageFiles.add(new GodToolsPackagePage(xmlPage));
        }
    }

    public Document getPackageXml()
    {
        return packageXml;
    }

    public void setPackageXml(Document packageXml)
    {
        this.packageXml = packageXml;
    }

    public List<GodToolsPackagePage> getPageFiles()
    {
        return pageFiles;
    }

    public void setPageFiles(List<GodToolsPackagePage> pageFiles)
    {
        this.pageFiles = pageFiles;
    }

    public String getLanguageCode()
    {
        return languageCode;
    }

    public void setLanguageCode(String languageCode)
    {
        this.languageCode = languageCode;
    }

    public String getPackageCode()
    {
        return packageCode;
    }

    public void setPackageCode(String packageCode)
    {
        this.packageCode = packageCode;
    }

    public String getPackageXmlHash()
    {
        return packageXmlHash;
    }

    public void setPackageXmlHash(String packageXmlHash)
    {
        this.packageXmlHash = packageXmlHash;
    }
}
