package org.cru.godtools.api.packages;

import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.cru.godtools.api.packages.utils.PageFilenameList;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by ryancarlson on 3/14/14.
 */
public class MockPackageService
{

    public GodToolsPackage getPackage(String languageCode, String packageCode)
    {
        try
        {
            DocumentBuilder documentBuilder =  DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document packageFile =  documentBuilder.parse(this.getClass().getResourceAsStream("/data/packages-" + languageCode + "-" + packageCode + ".xml"));

            return new GodToolsPackage(packageFile,
                    getPageFiles(languageCode, packageCode, new PageFilenameList().fromContentsFile(packageFile)),
                    languageCode,
                    packageCode);
        }
        catch(Exception e)
        {
            Throwables.propagate(e);
            return null; /*unreachable*/
        }
    }

    public Set<GodToolsPackage> getPackagesForLanguage(String languageCode)
    {
        GodToolsPackage kgp = getPackage(languageCode, "kgp");
        GodToolsPackage satisfied = getPackage(languageCode, "satisfied");

        return Sets.newHashSet(kgp, satisfied);
    }

    private Map<String, Document> getPageFiles(String languageCode, String packageCode, List<String> fileNames)
    {
        Map<String, Document> pages = Maps.newHashMap();

        try
        {
            DocumentBuilder documentBuilder =  DocumentBuilderFactory.newInstance().newDocumentBuilder();

            for(String filename : fileNames)
            {
                String path = path(languageCode,packageCode,filename);

                Document page = documentBuilder.parse(this.getClass().getResourceAsStream(path));
                pages.put(filename, page);
            }
        }
        catch(Exception e)
        {
            Throwables.propagate(e);
        }
       return pages;
    }

    private String path(String languageCode, String packageCode, String filename)
    {
        return "/data/packages/" + languageCode + "/" + packageCode + "/" + filename;
    }

}
