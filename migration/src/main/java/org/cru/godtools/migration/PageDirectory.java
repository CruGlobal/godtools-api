package org.cru.godtools.migration;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import org.cru.godtools.domain.packages.Page;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileNotFoundException;
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
	 * @throws java.net.URISyntaxException
	 * @throws javax.xml.parsers.ParserConfigurationException
	 * @throws org.xml.sax.SAXException
	 * @throws java.io.IOException
	 */
    public List<Page> buildPages()
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

	/**
	 * Look for a page in this directory with the filename specified by pageName
	 *
	 *
	 */
	public Page getPageByName(String pageName) throws FileNotFoundException
	{
		File directory = getDirectory();

		for(File file : directory.listFiles())
		{
			if(file.isFile() && file.getName().equals(pageName))
			{
				Page page = new Page();
				page.setId(UUID.randomUUID());
				page.setXmlContent(getPageXml(file));
				page.setFilename(file.getName());

				return page;
			}
		}

		throw new FileNotFoundException();
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

    private Document getPageXml(File pageFile)
    {
		try
		{
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			return builder.parse(pageFile);
		}
		catch(Exception e)
		{
			Throwables.propagate(e);
			return null;
		}
    }

	private String getTranslationPath()
	{
		return PackageDirectory.DIRECTORY_BASE + packageCode + "/" + languageCode;
	}

}
