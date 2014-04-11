package org.cru.godtools.api.packages;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.cru.godtools.api.packages.domain.Image;
import org.cru.godtools.api.packages.domain.ImageService;
import org.cru.godtools.api.packages.domain.Page;
import org.cru.godtools.api.packages.utils.XmlDocumentSearchUtilities;
import org.cru.godtools.api.translations.GodToolsTranslation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;
import java.util.Set;

/**
 * Created by ryancarlson on 4/8/14.
 */
public class GodToolsPackage extends GodToolsTranslation
{
	private List<Image> images = Lists.newArrayList();

	public GodToolsPackage(GodToolsTranslation godToolsTranslation)
	{
		super(godToolsTranslation.getPackageXml(),godToolsTranslation.getPageFiles(), godToolsTranslation.getLanguageCode(),godToolsTranslation.getPackageCode());
	}

	public GodToolsPackage(Document packageXml, List<Page> pageFiles, String languageCode, String packageCode)
	{
		super(packageXml, pageFiles, languageCode, packageCode);
	}

	public List<Image> getImages()
	{
		return images;
	}

	public void setImages(ImageService imageService)
	{
		for(Page page : pageFiles)
		{
			for(String imageHash : page.getReferencedImageHashSet())
			{

				Image image = imageService.selectByHash(imageHash);
				if(image == null)
				{
					System.out.println("Couldn't find..." + imageHash);
				}
				images.add(image);
			}
		}

		addThumbnails(imageService);
	}

	private void addThumbnails(ImageService imageService)
	{
		for(Element element : XmlDocumentSearchUtilities.findElementsWithAttribute(packageXml, "page", "thumb"))
		{
			images.add(imageService.selectByHash((element.getAttribute("thumb").replace(".png", ""))));
		}
	}
}
