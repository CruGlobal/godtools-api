package org.cru.godtools.api.packages;

import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import org.w3c.dom.Document;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Created by ryancarlson on 3/14/14.
 */
public class MockPackageService
{

    public Document getContentsFile(String languageCode, String packageCode)
    {
        try
        {
            DocumentBuilder documentBuilder =  DocumentBuilderFactory.newInstance().newDocumentBuilder();
            return documentBuilder.parse(getContentsFileStream(languageCode,packageCode));
        }
        catch(Exception e)
        {
            Throwables.propagate(e);
            return null; /*unreachable*/
        }
    }

    public InputStream getContentsFileStream(String languageCode, String packageCode)
    {
        try
        {
            return this.getClass().getResourceAsStream("/data/packages-" + languageCode + "-" + packageCode + ".xml");
        }
        catch(Exception e)
        {
            Throwables.propagate(e);
            return null; /*unreachable*/
        }
    }

    public Map<String, Document> getPageFiles(String languageCode, String packageCode, List<String> fileNames)
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
