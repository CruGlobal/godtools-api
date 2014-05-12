package org.cru.godtools.api.packages;

import com.google.common.collect.Lists;
import org.cru.godtools.api.images.domain.Image;
import org.cru.godtools.api.packages.domain.PackageStructure;
import org.cru.godtools.api.packages.domain.Page;
import org.cru.godtools.api.packages.domain.PageStructure;
import org.cru.godtools.api.packages.domain.TranslationElement;
import org.cru.godtools.api.translations.GodToolsTranslation;
import org.w3c.dom.Document;

import java.util.List;

/**
 * Created by ryancarlson on 4/8/14.
 */
public class GodToolsPackage
{
	private GodToolsTranslation godToolsTranslation;
	private List<Image> images = Lists.newArrayList();

	public GodToolsPackage(GodToolsTranslation godToolsTranslation)
	{
		this.godToolsTranslation = godToolsTranslation;
	}

	public List<Image> getImages()
	{
		return images;
	}

	public void setImages(List<Image> images)
	{
		this.images = images;
	}

	public void setPageStructureList(List<PageStructure> pageStructureList)
	{
		godToolsTranslation.setPageStructureList(pageStructureList);
	}

	public List<PageStructure> getPageStructureList()
	{
		return godToolsTranslation.getPageStructureList();
	}

	public void setPackageStructure(PackageStructure packageStructure)
	{
		godToolsTranslation.setPackageStructure(packageStructure);
	}

	public PackageStructure getPackageStructure()
	{
		return godToolsTranslation.getPackageStructure();
	}

}
