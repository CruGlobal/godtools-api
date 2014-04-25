package org.cru.godtools.migration;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import org.cru.godtools.api.images.FileSystemImageLookup;
import org.cru.godtools.api.packages.domain.Page;
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
 * Encapsulates logic for a pages directory. (e.g: "Packages/kgp/en")
 *
 *  - build a list of Pages
 *  - build a list of Images used by a Package
 */
public class PageDirectory
{

	private String packageCode;
	private String languageCode;

    public PageDirectory(String packageCode, String languageCode)
    {
		this.packageCode = packageCode;
		this.languageCode = languageCode;
    }

	/**
	 * Loops through files in the `path`.  If it's an .xml file then it's a page.
	 *
	 *
	 * @return
	 * @throws URISyntaxException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
    public List<Page> buildPages() throws URISyntaxException, ParserConfigurationException, SAXException, IOException
    {
        File directory = getDirectory();
        List<Page> pages = Lists.newArrayList();

        for(File file : directory.listFiles())
        {
            if(file.isFile() && file.getName().endsWith(".xml"))
            {
                Page page = new Page();
                page.setId(UUID.randomUUID());
                page.setXmlContent(getPageXml(file));
				page.setFilename(file.getName());
                pages.add(page);
            }
        }

        return pages;
    }

    private File getDirectory()
    {
		try
		{
			URL url = this.getClass().getResource(getTranslationPath());
			return new File(url.toURI());
		}
		catch(Exception e)
		{
			Throwables.propagate(e);
			return null;
		}
    }

    private Document getPageXml(File pageFile) throws IOException, SAXException, ParserConfigurationException
    {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        return builder.parse(pageFile);
    }

	private String getTranslationPath()
	{
		return "/data/SnuffyPackages/" + packageCode + "/" + languageCode;
	}

	private String getPackagePath()
	{
		return "/data/SnuffyPackages/" + packageCode;
	}


}
