package org.cru.godtools.api.translations;

import com.google.common.collect.Lists;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.cru.godtools.domain.images.Image;
import org.cru.godtools.domain.packages.PackageStructure;
import org.cru.godtools.domain.packages.PageStructure;
import org.cru.godtools.domain.packages.TranslationElement;

import java.math.BigDecimal;
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
	String packageName;
	BigDecimal versionNumber;
	PackageStructure packageStructure;
	List<PageStructure> pageStructureList;
	boolean isDraft;
	private Image icon;
	private List<Image> images = Lists.newArrayList();

    public GodToolsTranslation()
    {
    }

	public static GodToolsTranslation assembleFromComponents(String packageCode,
															 String packageName,
															 Integer translationVersionNumber,
															 PackageStructure packageStructure,
															 List<PageStructure> pageStructures,
															 List<TranslationElement> translationElementList,
															 List<Image> referencedImages,
															 boolean isDraft,
															 Image icon)
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
		godToolsTranslation.setPackageName(packageName);
		godToolsTranslation.setVersionNumber(new BigDecimal(packageStructure.getVersionNumber() + "." + translationVersionNumber));
		godToolsTranslation.setDraft(isDraft);

		godToolsTranslation.setImages(referencedImages);
		godToolsTranslation.setIcon(icon);

		return godToolsTranslation;
	}

	/**
	 * You can't have one without the other
	 * @return
	 */
	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(17, 83)  // two randomly chosen prime numbers (as random as random can be...)
				.append(packageCode)
				.append(isDraft)
				.toHashCode();
	}

	/**
	 * Override equality... two translations will be considered equal iff they have the same packageCode and draft status
	 *
	 * @param obj
	 * @return
	 */
	@Override
	public boolean equals(Object obj)
	{
		if(obj == null) return false;
		if(!(obj instanceof GodToolsTranslation)) return false;
		GodToolsTranslation translation = (GodToolsTranslation) obj;

		return new EqualsBuilder()
				.append(packageCode, translation.getPackageCode())
				.append(isDraft, translation.isDraft())
				.isEquals();
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

	public boolean isDraft()
	{
		return isDraft;
	}

	public void setDraft(boolean isDraft)
	{
		this.isDraft = isDraft;
	}

	public Image getIcon()
	{
		return icon;
	}

	public void setIcon(Image icon)
	{
		this.icon = icon;
	}

	public List<Image> getImages()
	{
		return images;
	}

	public void setImages(List<Image> images)
	{
		this.images = images;
	}

	public String getPackageName()
	{
		return packageName;
	}

	public void setPackageName(String packageName)
	{
		this.packageName = packageName;
	}

	public BigDecimal getVersionNumber()
	{
		return versionNumber;
	}

	public void setVersionNumber(BigDecimal versionNumber)
	{
		this.versionNumber = versionNumber;
	}
}
