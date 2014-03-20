package org.cru.godtools.api.packages.utils;

import com.beust.jcommander.internal.Sets;
import com.google.common.collect.ForwardingSet;
import org.cru.godtools.api.packages.GodToolsPackagePage;

import java.util.List;
import java.util.Set;

/**
 * Created by ryancarlson on 3/19/14.
 */
public class ImageNameList extends ForwardingSet<String>
{
    Set<String> set = Sets.newHashSet();

    public ImageNameList fromPageFiles(List<GodToolsPackagePage> godToolsPackagePages)
    {
        for(GodToolsPackagePage page : godToolsPackagePages)
        {
            set.addAll(XmlDocumentSearchUtilities.findAttributesWithinElement(page.getXml(), "page", "backgroundimage"));
            set.addAll(XmlDocumentSearchUtilities.findAttributesWithinElement(page.getXml(), "page", "watermark"));
            set.addAll(XmlDocumentSearchUtilities.findElementValues(page.getXml(), "image"));
        }
        return this;
    }

    @Override
    protected Set<String> delegate()
    {
        return set;
    }
}
