package org.cru.godtools.api.packages;

import com.google.common.collect.Lists;
import org.cru.godtools.api.translations.GodToolsTranslation;
import org.cru.godtools.domain.images.Image;
import org.cru.godtools.domain.packages.PackageStructure;
import org.cru.godtools.domain.packages.PageStructure;

import java.util.List;

/**
 * Contains the XML structure files for a GodTools package and the images associated with the package.
 *
 * Created by ryancarlson on 4/8/14.
 */
public class GodToolsPackage
{
	private GodToolsTranslation godToolsTranslation;
	private List<Image> images = Lists.newArrayList();
	private Image icon;

	public static GodToolsPackage assembleFromComponents(GodToolsTranslation godToolsTranslation, List<Image> images, Image icon)
	{
		GodToolsPackage godToolsPackage = new GodToolsPackage();
		godToolsPackage.godToolsTranslation = godToolsTranslation;
		godToolsPackage.images = images;
		godToolsPackage.icon = icon;

		return godToolsPackage;
	}

	public String getPackageCode()
	{
		return godToolsTranslation.getPackageCode();
	}

	public void setPackageCode(String packageCode)
	{
		godToolsTranslation.setPackageCode(packageCode);
	}

	public List<Image> getImages()
	{
		return images;
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

	public Image getIcon()
	{
		return icon;
	}

	public void setIcon(Image icon)
	{
		this.icon = icon;
	}
}
