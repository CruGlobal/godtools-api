package org.cru.godtools.api.packages;

import com.google.common.base.Strings;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * Created by ryancarlson on 3/14/14.
 */
public class MockPackageService
{
    public Document getPackage(String languageCode, String packageCode) throws IOException, SAXException, ParserConfigurationException
    {
        DocumentBuilder documentBuilder =  DocumentBuilderFactory.newInstance().newDocumentBuilder();

        if(Strings.isNullOrEmpty(languageCode) && Strings.isNullOrEmpty(packageCode))
        {
//            return documentBuilder.parse(this.getClass().getResourceAsStream("/data/meta-all.xml"));
        }

        else if("en".equalsIgnoreCase(languageCode) && "kgp".equalsIgnoreCase(packageCode))
        {
            return documentBuilder.parse(this.getClass().getResourceAsStream("/data/packages-en-kgp.xml"));
        }

        else if("en".equalsIgnoreCase(languageCode))
        {
//            return documentBuilder.parse(this.getClass().getResourceAsStream("/data/meta-en.xml"));
        }

        else if("kgp".equalsIgnoreCase(packageCode))
        {
//            return documentBuilder.parse(this.getClass().getResourceAsStream("/data/meta-kgp.xml"));
        }

        return null;
    }
}