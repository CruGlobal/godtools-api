package org.cru.godtools.api.translations;

import org.cru.godtools.api.images.domain.Image;
import org.cru.godtools.api.packages.domain.PackageStructure;
import org.cru.godtools.api.packages.domain.PageStructure;
import org.cru.godtools.api.packages.domain.TranslationElement;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Contains the XML structure files for a GodTools translation.
 *
 * Created by ryancarlson on 3/18/14.
 */
public class GodToolsTranslation
{
	String packageCode;
	PackageStructure packageStructure;
	List<PageStructure> pageStructureList;

    public GodToolsTranslation()
    {
    }

	public static GodToolsTranslation assembleFromComponents(String packageCode,
															 PackageStructure packageStructure,
															 List<PageStructure> pageStructures,
															 List<TranslationElement> translationElementList,
															 List<Image> referencedImages)
	{
		GodToolsTranslation godToolsTranslation = new GodToolsTranslation();

		Map<UUID, TranslationElement> mapOfTranslationElements = TranslationElement.createMapOfTranslationElements(translationElementList);

		packageStructure.setTranslatedFields(mapOfTranslationElements);

		for(PageStructure pageStructure : pageStructures)
		{
			pageStructure.setTranslatedFields(mapOfTranslationElements);
			pageStructure.replaceImageNamesWithImageHashes(Image.createMapOfImages(referencedImages));
		}

		packageStructure.replacePageNamesWithPageHashes(PageStructure.createMapOfPageStructures(pageStructures));
		packageStructure.replaceImageNamesWithImageHashes(Image.createMapOfImages(referencedImages));

		godToolsTranslation.setPackageStructure(packageStructure);
		godToolsTranslation.setPageStructureList(pageStructures);
		godToolsTranslation.setPackageCode(packageCode);

		return godToolsTranslation;
	}

	public String getPackageCode()
	{
		return packageCode;
	}

	public void setPackageCode(String packageCode)
	{
		this.packageCode = packageCode;
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
