package org.cru.godtools.migration;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.cru.godtools.api.languages.Language;
import org.cru.godtools.api.packages.domain.Package;
import org.cru.godtools.api.packages.domain.Page;
import org.cru.godtools.api.packages.domain.Version;
import org.cru.godtools.api.translations.Translation;
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
 * Created by ryancarlson on 3/21/14.
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

    /**
     * Returns a list of all the translations represented in this package directory.
     *  - package and language are passed in to use the UUIDs that have already been created.
     *
     * @param gtPackage
     * @param language
     * @return
     */
    public Translation buildTranslation(Package gtPackage, Language language)
    {
        Translation translation = new Translation();

        translation.setId(UUID.randomUUID());
        translation.setPackageId(gtPackage.getId());
        translation.setLanguageId(language.getId());

        return translation;
    }

    /**
     * Returns a list of all the translations represented in this package directory.
     *  - package and translation are passed in to use the UUIDs that have already been created.
     *
     * @param gtPackage
     * @param translation
     * @return
     */
    public Version buildVersion(Package gtPackage, Translation translation) throws URISyntaxException
    {
        Version version = new Version();

        version.setId(UUID.randomUUID());
        version.setPackageId(gtPackage.getId());
        version.setTranslationId(translation.getId());
        version.setVersionNumber(1);
        version.setReleased(true);

        return version;
    }

    public Document getPackageDescriptorXml(Language language) throws IOException, SAXException, ParserConfigurationException
    {
        String path = "/data/Packages/" + packageCode + "/";
        path += language.getCode();
        if(!Strings.isNullOrEmpty(language.getLocale())) path = path + "_" + language.getLocale();
        if(!Strings.isNullOrEmpty(language.getSubculture())) path = path + "_" + language.getSubculture();
        path += ".xml";

        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        return builder.parse(this.getClass().getResourceAsStream(path));
    }

    private File getDirectory() throws URISyntaxException
    {
        URL packageFolderUrl = this.getClass().getResource("/data/Packages/" + packageCode);
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
