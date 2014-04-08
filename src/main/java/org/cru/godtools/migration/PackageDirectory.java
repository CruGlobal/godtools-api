package org.cru.godtools.migration;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.cru.godtools.api.languages.Language;
import org.cru.godtools.api.packages.domain.Image;
import org.cru.godtools.api.packages.domain.Package;
import org.cru.godtools.api.packages.utils.ShaGenerator;
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
import java.util.List;
import java.util.UUID;

/**
 * Encapsulates logic for a package directory. (e.g: "Packages/kgp")
 *
 *  - build a Package
 *  - build a list of Languages a Package is translated into
 *  - build a list of Icons represented by a Package
 */
public class PackageDirectory
{
    private String packageCode;

    public PackageDirectory(String packageCode)
    {
        this.packageCode = packageCode;
    }


    public Package buildPackage() throws Exception
    {
        File directory = getDirectory();

        for(File nextFile : directory.listFiles())
        {
            if(nextFile.isFile() && nextFile.getName().endsWith(".xml"))
            {
                Package gtPackage = new Package();

                gtPackage.setId(UUID.randomUUID());
                gtPackage.setName(getPackageName(getPackageDescriptorXml(nextFile)));
                gtPackage.setCode(packageCode);

                return gtPackage;
            }
        }

        throw new RuntimeException("unable to build package for packageCode: " + packageCode);
    }

    /**
     * Returns a list of all the languages represented in this package directory.
     *
     * @return
     * @throws Exception
     */
    public List<Language> buildLanguages() throws Exception
    {
        List<Language> languages = Lists.newArrayList();
        File directory = getDirectory();

        for(File nextFile : directory.listFiles())
        {
            if(nextFile.isFile() && nextFile.getName().endsWith(".xml"))
            {
                PackageDescriptorFile packageDescriptorFile = new PackageDescriptorFile(nextFile);

                Language language = new Language();

                language.setId(UUID.randomUUID());
                language.setCode(packageDescriptorFile.getLanguageCode());
                language.setLocale(packageDescriptorFile.getLocaleCode());
                language.setSubculture(packageDescriptorFile.getSubculture());

                languages.add(language);
            }
        }

        return languages;
    }

    public List<Image> buildIcons() throws URISyntaxException, IOException
    {
        File directory = getDirectory();

        List<Image> images = Lists.newArrayList();

        for(File file : directory.listFiles())
        {
            if(file.isDirectory() && file.getName().equalsIgnoreCase("icons"))
            {
                for(File imageFile : file.listFiles())
                {
                Image image = new Image();
                image.setId(UUID.randomUUID());
                image.setFilename(imageFile.getName());
                image.setImageContent(ImageReader.read(imageFile));
                image.setImageHash(ShaGenerator.calculateHash(image.getImageContent()));
                image.setResolution("High");
                images.add(image);
                }
            }
        }

        return images;
    }

    public Document getPackageDescriptorXml(Language language) throws IOException, SAXException, ParserConfigurationException
    {
        String path = "/data/SnuffyPackages/" + packageCode + "/";
        path += language.getCode();
        if(!Strings.isNullOrEmpty(language.getLocale())) path = path + "_" + language.getLocale();
        if(!Strings.isNullOrEmpty(language.getSubculture())) path = path + "_" + language.getSubculture();
        path += ".xml";

        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        return builder.parse(this.getClass().getResourceAsStream(path));
    }

    private File getDirectory() throws URISyntaxException
    {
        URL packageFolderUrl = this.getClass().getResource("/data/SnuffyPackages/" + packageCode);
        return new File(packageFolderUrl.toURI());
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
