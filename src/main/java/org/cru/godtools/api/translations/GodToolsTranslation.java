package org.cru.godtools.api.translations;

import org.cru.godtools.api.packages.domain.PackageStructure;
import org.cru.godtools.api.packages.domain.PageStructure;

import java.util.List;

/**
 * Created by ryancarlson on 3/18/14.
 */
public class GodToolsTranslation
{
	PackageStructure packageStructure;
	List<PageStructure> pageStructureList;

    public GodToolsTranslation()
    {
    }

	public PackageStructure getPackageStructure()
	{
		return packageStructure;
	}

	public void setPackageStructure(PackageStructure packageStructure)
	{
		this.packageStructure = packageStructure;
	}

	public List<PageStructure> getPageStructureList()
	{
		return pageStructureList;
	}

	public void setPageStructureList(List<PageStructure> pageStructureList)
	{
		this.pageStructureList = pageStructureList;
	}
}
