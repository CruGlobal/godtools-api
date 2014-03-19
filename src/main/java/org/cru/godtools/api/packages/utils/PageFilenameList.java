package org.cru.godtools.api.packages.utils;

import com.google.common.collect.ForwardingList;
import com.google.common.collect.Lists;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


import java.util.List;

/**
 * Created by ryancarlson on 3/17/14.
 */
public class PageFilenameList extends ForwardingList<String>
{
    List<String> list = Lists.newArrayList();

    public PageFilenameList fromContentsFile(Document contentsFile)
    {
        loadFileNamesForElementName(contentsFile, "page");
        loadFileNamesForElementName(contentsFile, "about");

        return this;
    }

    private void loadFileNamesForElementName(Document contentsFile, String elementName)
    {
        loadFileNamesForElementName(contentsFile, elementName, "filename");
    }

    private void loadFileNamesForElementName(Document contentsFile, String elementName, String attributeName)
    {
        Element rootElement = contentsFile.getDocumentElement();

        NodeList pageNodes = rootElement.getElementsByTagName(elementName);

        for(int i = 0; i < pageNodes.getLength(); i++)
        {
            Node node = pageNodes.item(i);

            if(node instanceof Element)
            {
                Element page = (Element) node;
                list.add(page.getAttribute(attributeName));
            }
        }
    }

    @Override
    protected List<String> delegate()
    {
        return list;
    }
}
