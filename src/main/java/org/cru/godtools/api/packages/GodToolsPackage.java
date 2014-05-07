package org.cru.godtools.api.packages;

import com.google.common.collect.Lists;
import org.cru.godtools.api.images.domain.Image;
import org.cru.godtools.api.packages.domain.Page;
import org.cru.godtools.api.translations.GodToolsTranslation;
import org.w3c.dom.Document;

import java.util.List;

/**
 * Created by ryancarlson on 4/8/14.
 */
public class GodToolsPackage extends GodToolsTranslation
{
	private List<Image> images = Lists.newArrayList();

	public GodToolsPackage(GodToolsTranslation godToolsTranslation)
	{

	}

	public GodToolsPackage(Document packageXml, List<Page> pageFiles, String languageCode, String packageCode)
	{
	}

	public List<Image> getImages()
	{
		return images;
	}

	public void setImages(List<Image> images)
	{
		this.images = images;
	}
}
