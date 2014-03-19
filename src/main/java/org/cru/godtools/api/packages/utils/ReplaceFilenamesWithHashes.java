package org.cru.godtools.api.packages.utils;

import org.cru.godtools.api.packages.GodToolsPackage;
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

        NodeList pageNodes = packageXml.getElementsByTagName("page");

        for(int i = 0; i < pageNodes.getLength(); i++)
        {
            Node page = pageNodes.item(i);

            if(page instanceof Element)
            {
                ((Element) page).setAttribute("filename", godToolsPackage.getPageFiles().get(i).getPageHash() + ".xml");
            }

        }
    }
}
