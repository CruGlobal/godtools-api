package org.cru.godtools.migration;

import com.google.common.base.Strings;
import org.cru.godtools.api.packages.domain.Package;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.UUID;

/**
 * Created by ryancarlson on 3/21/14.
 */
public class PackageDirectory
{
    public File getDirectory(String packageCode) throws URISyntaxException
    {
        URL packageFolderUrl = this.getClass().getResource("/data/Packages/" + packageCode);
        return new File(packageFolderUrl.toURI());
    }

    public Package buildPackage(String packageCode, File packageDescriptor) throws IOException, SAXException, ParserConfigurationException
    {
        Package gtPackage = new Package();

        gtPackage.setId(UUID.randomUUID());
        gtPackage.setName(getPackageName(getPackageDescriptorXml(packageDescriptor)));
        gtPackage.setCode(packageCode);

        return gtPackage;
    }

    private Document getPackageDescriptorXml(File packageDescriptor) throws ParserConfigurationException, IOException, SAXException
    {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

        return builder.parse(packageDescriptor);
    }

    private String getPackageName(Document packageDescriptorXml)
    {
        NodeList nodes = packageDescriptorXml.getElementsByTagName("packagename");

        for(int i = 0; i < nodes.getLength(); i++)
        {
            if(!Strings.isNullOrEmpty(nodes.item(i).getTextContent()))
            {
                return nodes.item(i).getTextContent();
            }
        }

        return null;
    }
}
