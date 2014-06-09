package org.cru.godtools.api.packages;

import com.google.common.collect.Lists;
import org.cru.godtools.api.images.domain.Image;
import org.cru.godtools.api.packages.domain.PackageStructure;
import org.cru.godtools.api.packages.domain.PageStructure;
import org.cru.godtools.api.translations.GodToolsTranslation;

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


	public static GodToolsPackage assembleFromComponents(GodToolsTranslation godToolsTranslation, List<Image> images)
	{
		GodToolsPackage godToolsPackage = new GodToolsPackage();
		godToolsPackage.godToolsTranslation = godToolsTranslation;
		godToolsPackage.images = images;

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

}
