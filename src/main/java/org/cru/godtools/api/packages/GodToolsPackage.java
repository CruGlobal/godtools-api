package org.cru.godtools.api.packages;

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
	private Set<Image> images = Sets.newHashSet();

	public GodToolsPackage(GodToolsTranslation godToolsTranslation, Set<Image> images)
	{
		super(godToolsTranslation.getPackageXml(),godToolsTranslation.getPageFiles(), godToolsTranslation.getLanguageCode(),godToolsTranslation.getPackageCode());
		this.images = images;
	}

	public GodToolsPackage(Document packageXml, List<Page> pageFiles, String languageCode, String packageCode, Set<Image> images)
	{
		super(packageXml, pageFiles, languageCode, packageCode);
		this.images = images;
	}

	public Set<Image> getImages()
	{
		return images;
	}

	public void setImages(Set<Image> images)
	{
		this.images = images;
	}
}
