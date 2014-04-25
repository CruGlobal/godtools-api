package org.cru.godtools.api.packages;

import org.cru.godtools.api.packages.domain.Page;
import org.w3c.dom.Document;

import java.util.List;

/**
 * Created by ryancarlson on 4/25/14.
 */
public class InMemoryPageLookup implements PageLookup
{

	List<Page> pages;


	public InMemoryPageLookup(List<Page> pages)
	{
		this.pages = pages;
	}

	@Override
	public Document findByFilename(String filename)
	{
		for(Page page : pages)
		{
			if(filename.equalsIgnoreCase(page.getFilename())) return page.getXmlContent();
		}

		return null;
	}
}
