package org.cru.godtools.migration;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.cru.godtools.api.packages.domain.Image;
import org.cru.godtools.api.packages.domain.Page;
import org.cru.godtools.api.packages.domain.Version;
import org.cru.godtools.api.packages.utils.ShaGenerator;
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
import java.util.Map;
import java.util.UUID;

/**
 * Encapsulates logic for a pages directory. (e.g: "Packages/kgp/en_US")
 *
 *  - build a list of Pages
 *  - build a list of Images used by a Package
 */
public class PageDirectory
{

	private String path;

    public PageDirectory(String packageCode, String translationPath)
    {
		this.path = "/data/SnuffyPackages/" + packageCode + "/" + translationPath;
    }

    public List<Page> buildPages(Version version) throws URISyntaxException, ParserConfigurationException, SAXException, IOException
    {
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
				page.replaceImageNamesWithImageHashes(getAllAvailableImagesByFilenameMap());
                page.calculateHash();
                pages.add(page);
            }
        }

        return pages;
    }

    private File getDirectory()
    {
		try
		{
			URL url = this.getClass().getResource(path);
			return new File(url.toURI());
		}
		catch(Exception e)
		{
			Throwables.propagate(e);
			return null;
		}
    }

	private Map<String, Image> getAllAvailableImagesByFilenameMap()
	{
		return ImageDirectory.getCombinedImageByFilenameMap(new ThumbsImageDirectory(path), new ImagesImageDirectory(path), new ImageDirectory("/data/SnuffyPackages/shared"));
	}


    private Document getPageXml(File pageFile) throws IOException, SAXException, ParserConfigurationException
    {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        return builder.parse(pageFile);
    }
}
