package org.cru.godtools.api.packages;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.cru.godtools.api.packages.domain.Image;
import org.cru.godtools.api.packages.domain.Page;
import org.cru.godtools.api.translations.GodToolsTranslation;
import org.w3c.dom.Document;

import java.util.List;
import java.util.Set;

/**
 * Created by ryancarlson on 4/8/14.
 */
public class GodToolsPackage extends GodToolsTranslation
{
	private List<Image> images = Lists.newArrayList();

	public GodToolsPackage(GodToolsTranslation godToolsTranslation, List<Image> images)
	{
		super(godToolsTranslation.getPackageXml(),godToolsTranslation.getPageFiles(), godToolsTranslation.getLanguageCode(),godToolsTranslation.getPackageCode());
		this.images = images;
	}

	public GodToolsPackage(Document packageXml, List<Page> pageFiles, String languageCode, String packageCode, List<Image> images)
	{
		super(packageXml, pageFiles, languageCode, packageCode);
		this.images = images;
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
