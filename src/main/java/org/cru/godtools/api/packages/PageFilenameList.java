package org.cru.godtools.api.packages;

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
        Element rootElement = contentsFile.getDocumentElement();

        NodeList pageNodes = rootElement.getElementsByTagName("page");

        for(int i = 0; i < pageNodes.getLength(); i++)
        {
            Node node = pageNodes.item(i);

            if(node instanceof Element)
            {
                Element page = (Element) node;
                list.add(page.getAttribute("filename"));
            }
        }

        return this;
    }

    @Override
    protected List<String> delegate()
    {
        return list;
    }
}
