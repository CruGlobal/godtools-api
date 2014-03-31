package org.cru.godtools.migration;

import com.google.common.collect.Lists;
import org.cru.godtools.api.packages.domain.Image;
import org.cru.godtools.api.packages.domain.Page;
import org.cru.godtools.api.packages.domain.Version;
import org.cru.godtools.api.packages.utils.GodToolsPackageShaGenerator;
import org.w3c.dom.Document;
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
 * Encapsulates logic for a pages directory. (e.g: "Packages/kgp/en_US")
 *
 *  - build a list of Pages
 *  - build a list of Images used by a Package
 */
public class PageDirectory
{

    private String packageCode;
    private String translationPath;

    public PageDirectory(String packageCode, String translationPath)
    {
        this.packageCode = packageCode;
        this.translationPath = translationPath;
    }

    public List<Page> buildPages(Version version) throws URISyntaxException, ParserConfigurationException, SAXException, IOException
    {
        GodToolsPackageShaGenerator shaGenerator = new GodToolsPackageShaGenerator();

        File directory = getDirectory();
        List<Page> pages = Lists.newArrayList();

        for(File file : directory.listFiles())
        {
            if(file.isFile() && file.getName().endsWith(".xml"))
            {
                Page page = new Page();
                page.setId(UUID.randomUUID());
                page.setVersionId(version.getId());
                page.setFilename(file.getName());
                page.setXmlContent(getPageXml(file));
                page.setPageHash(shaGenerator.calculateHash(page.getXmlContent()));
                pages.add(page);
            }
        }

        return pages;
    }

    public List<Image> buildImages() throws URISyntaxException, IOException
    {
        GodToolsPackageShaGenerator shaGenerator = new GodToolsPackageShaGenerator();

        File directory = getDirectory();
        List<Image> images = Lists.newArrayList();

        for(File file : directory.listFiles())
        {
            if(file.isDirectory())
            {
                for(File imageFile : file.listFiles())
                {
                    Image image = new Image();
                    image.setId(UUID.randomUUID());
                    image.setFilename(buildFileName(imageFile.getName()));
                    image.setImageContent(ImageReader.read(imageFile));
                    image.setImageHash(shaGenerator.calculateHash(image.getImageContent()));
                    image.setResolution("High");
                    images.add(image);
                }
            }
        }

        return images;
    }

    private File getDirectory() throws URISyntaxException
    {
        URL url = this.getClass().getResource("/data/SnuffyPackages/" + packageCode + "/" + translationPath);
        return new File(url.toURI());
    }

    private Document getPageXml(File pageFile) throws IOException, SAXException, ParserConfigurationException
    {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        return builder.parse(pageFile);
    }

    private String buildFileName(String filename )
    {
        if(translationPath.contains("classic"))
        {
            return translationPath + "_" + filename;
        }
        return filename;
    }
}
