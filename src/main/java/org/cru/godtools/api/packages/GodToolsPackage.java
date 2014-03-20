package org.cru.godtools.api.packages;

import com.beust.jcommander.internal.Sets;
import com.google.common.collect.Lists;
import org.w3c.dom.Document;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Created by ryancarlson on 3/18/14.
 */
public class GodToolsPackage
{
    Document packageXml;
    List<GodToolsPackagePage> pageFiles = Lists.newArrayList();
    Set<GodToolsPackageImage> imageFiles = Sets.newHashSet();
    String languageCode;
    String packageCode;

    String packageXmlHash;

    public GodToolsPackage(Document packageXml, List<GodToolsPackagePage> pageFiles, String languageCode, String packageCode)
    {
        this.packageXml = packageXml;
        this.pageFiles = pageFiles;
        this.languageCode = languageCode;
        this.packageCode = packageCode;
    }

    public GodToolsPackagePage getPageByFilename(String filename)
    {
        for(GodToolsPackagePage godToolsPackagePage : pageFiles)
        {
            if(godToolsPackagePage.getOriginalFilename().equals(filename))
            {
                return godToolsPackagePage;
            }
        }

        throw new NoSuchElementException();
    }


    public GodToolsPackageImage getImageByFilename(String imageName)
    {
        for(GodToolsPackageImage godToolsPackageImage : imageFiles)
        {
            if(godToolsPackageImage.getOriginalFilename().equals(imageName))
            {
                return godToolsPackageImage;
            }
        }

        throw new NoSuchElementException();
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

    public Set<GodToolsPackageImage> getImageFiles()
    {
        return imageFiles;
    }

    public void setImageFiles(Set<GodToolsPackageImage> imageFiles)
    {
        this.imageFiles = imageFiles;
    }
}
