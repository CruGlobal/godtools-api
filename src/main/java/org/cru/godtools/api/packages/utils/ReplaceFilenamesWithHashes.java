package org.cru.godtools.api.packages.utils;

import org.cru.godtools.api.packages.GodToolsPackage;
import org.cru.godtools.api.packages.GodToolsPackagePage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Set;

/**
 * Created by ryancarlson on 3/19/14.
 */
public class ReplaceFilenamesWithHashes
{

    public void replace(GodToolsPackage godToolsPackage)
    {
        replacePageFilenames(godToolsPackage);
    }

    public void replace(Set<GodToolsPackage> godToolsPackages)
    {
        for(GodToolsPackage godToolsPackage : godToolsPackages)
        {
            replace(godToolsPackage);
        }
    }

    private void replacePageFilenames(GodToolsPackage godToolsPackage)
    {
        Document packageXml = godToolsPackage.getPackageXml();

        replaceFilenamesForElement(godToolsPackage, packageXml, "page");
        replaceFilenamesForElement(godToolsPackage, packageXml, "about");
    }

    private void replaceFilenamesForElement(GodToolsPackage godToolsPackage, Document packageXml, String elementName)
    {
        replaceFilenamesForElement(godToolsPackage, packageXml, elementName, "filename");
    }

    private void replaceFilenamesForElement(GodToolsPackage godToolsPackage, Document packageXml, String elementName, String attributeName)
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
}
