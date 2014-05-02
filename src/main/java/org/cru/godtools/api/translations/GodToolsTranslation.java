package org.cru.godtools.api.translations;

import com.google.common.collect.Lists;
import org.cru.godtools.api.packages.domain.PackageStructure;
import org.cru.godtools.api.packages.domain.Page;
import org.cru.godtools.api.packages.domain.PageStructure;
import org.cru.godtools.api.packages.domain.TranslationElement;
import org.cru.godtools.api.packages.domain.Version;
import org.cru.godtools.api.packages.utils.ShaGenerator;
import org.w3c.dom.Document;

import java.util.List;

/**
 * Created by ryancarlson on 3/18/14.
 */
public class GodToolsTranslation
{
	PackageStructure packageStructure;
	List<PageStructure> pageStructureList;
	List<TranslationElement> translationElementList;


    public GodToolsTranslation()
    {
    }

	public GodToolsTranslation(PackageStructure packageStructure, List<PageStructure> pageStructureList, List<TranslationElement> translationElementList)
	{
		this.packageStructure = packageStructure;
		this.pageStructureList = pageStructureList;
		this.translationElementList = translationElementList;
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

	public List<TranslationElement> getTranslationElementList()
	{
		return translationElementList;
	}

	public void setTranslationElementList(List<TranslationElement> translationElementList)
	{
		this.translationElementList = translationElementList;
	}
}
