package org.cru.godtools.api.packages;

import org.w3c.dom.Document;

import java.util.Map;

/**
 * Created by ryancarlson on 3/18/14.
 */
public class GodToolsPackage
{
    Document packageFile;
    Map<String, Document> pageFiles;
    String languageCode;
    String packageCode;

    String packageFileChecksum;

    public GodToolsPackage(Document packageFile, Map<String, Document> pageFiles, String languageCode, String packageCode)
    {
        this.packageFile = packageFile;
        this.pageFiles = pageFiles;
        this.languageCode = languageCode;
        this.packageCode = packageCode;
    }

    public Document getPackageFile()
    {
        return packageFile;
    }

    public void setPackageFile(Document packageFile)
    {
        this.packageFile = packageFile;
    }

    public Map<String, Document> getPageFiles()
    {
        return pageFiles;
    }

    public void setPageFiles(Map<String, Document> pageFiles)
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

    public String getPackageFileChecksum()
    {
        return packageFileChecksum;
    }

    public void setPackageFileChecksum(String packageFileChecksum)
    {
        this.packageFileChecksum = packageFileChecksum;
    }
}
