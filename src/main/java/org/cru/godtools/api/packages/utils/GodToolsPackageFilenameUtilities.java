package org.cru.godtools.api.packages.utils;

import com.google.common.base.Strings;
import org.cru.godtools.api.packages.GodToolsPackage;
import org.cru.godtools.api.packages.GodToolsPackageImage;
import org.cru.godtools.api.packages.GodToolsPackagePage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Set;

/**
 * Created by ryancarlson on 3/19/14.
 */
public class GodToolsPackageFilenameUtilities
{

    public void replaceFilenamesWithHash(GodToolsPackage godToolsPackage)
    {
        replacePageFilenames(godToolsPackage);
        replaceImageFilenames(godToolsPackage);
    }

    public void replaceFilenamesWithHash(Set<GodToolsPackage> godToolsPackages)
    {
        for(GodToolsPackage godToolsPackage : godToolsPackages)
        {
            replaceFilenamesWithHash(godToolsPackage);
        }
    }

    private void replacePageFilenames(GodToolsPackage godToolsPackage)
    {
        Document packageXml = godToolsPackage.getPackageXml();

        replacePageNamesForElement(godToolsPackage, packageXml, "page", "filename");
        replacePageNamesForElement(godToolsPackage, packageXml, "about", "filename");
    }

    private void replaceImageFilenames(GodToolsPackage godToolsPackage)
    {
        for(GodToolsPackagePage godToolsPackagePage : godToolsPackage.getPageFiles())
        {
            replaceImageNamesForElement(godToolsPackage,godToolsPackagePage.getXml(), "page", "backgroundimage");
            replaceImageNamesForElement(godToolsPackage,godToolsPackagePage.getXml(), "page", "watermark");
        }
    }

    private void replacePageNamesForElement(GodToolsPackage godToolsPackage, Document packageXml, String elementName, String attributeName)
    {
        NodeList pageNodes = packageXml.getElementsByTagName(elementName);

        for(int i = 0; i < pageNodes.getLength(); i++)
        {
            Node pageNode = pageNodes.item(i);

            if(pageNode instanceof Element)
            {
                Element pageElement = (Element) pageNode;
                GodToolsPackagePage godToolsPackagePage = godToolsPackage.getPageByFilename(pageElement.getAttribute(attributeName));
                pageElement.setAttribute(attributeName, godToolsPackagePage.getPageHash() + ".xml");
            }
        }
    }

    private void replaceImageNamesForElement(GodToolsPackage godToolsPackage, Document pageXml, String elementName, String attributeName)
    {
        NodeList pageNodes = pageXml.getElementsByTagName(elementName);

        for(int i = 0; i < pageNodes.getLength(); i++)
        {
            Node pageNode = pageNodes.item(i);

            if(pageNode instanceof Element)
            {
                Element pageElement = (Element) pageNode;

                if(Strings.isNullOrEmpty(pageElement.getAttribute(attributeName))) continue;

                GodToolsPackageImage godToolsPackageImage = godToolsPackage.getImageByFilename(pageElement.getAttribute(attributeName));
                pageElement.setAttribute(attributeName, godToolsPackageImage.getHash() + ".png");
            }
        }
    }
}
