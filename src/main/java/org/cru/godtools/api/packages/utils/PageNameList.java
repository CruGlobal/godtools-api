package org.cru.godtools.api.packages.utils;

import com.google.common.collect.ForwardingList;
import com.google.common.collect.Lists;
import org.w3c.dom.Document;


import java.util.List;

/**
 * Created by ryancarlson on 3/17/14.
 */
public class PageNameList extends ForwardingList<String>
{
    List<String> list = Lists.newArrayList();

    public PageNameList fromContentsFile(Document contentsFile)
    {
        list.addAll(XmlDocumentSearchUtilities.findAttributesWithinElement(contentsFile, "page", "filename"));
        list.addAll(XmlDocumentSearchUtilities.findAttributesWithinElement(contentsFile, "about", "filename"));

        return this;
    }

    @Override
    protected List<String> delegate()
    {
        return list;
    }
}
