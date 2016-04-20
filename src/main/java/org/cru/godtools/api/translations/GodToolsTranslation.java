package org.cru.godtools.api.translations;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.cru.godtools.domain.images.Image;
import org.cru.godtools.domain.languages.Language;
import org.cru.godtools.domain.packages.Package;
import org.cru.godtools.domain.packages.PackageStructure;
import org.cru.godtools.domain.packages.PageStructure;
import org.cru.godtools.domain.packages.TranslationElement;
import org.cru.godtools.domain.translations.Translation;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Contains the XML structure files for a GodTools translation.
 *
 * Created by ryancarlson on 3/18/14.
 */
public class GodToolsTranslation implements Serializable
{
	Package gtPackage;
	Language language;
	Translation translation;
	PackageStructure packageStructure;
	List<PageStructure> pageStructureList;
	private Image icon;
	private List<Image> images = Lists.newArrayList();

    public GodToolsTranslation()
    {
    }

	public static GodToolsTranslation assembleFromComponents(Package gtPackage,
															 Language language,
															 Translation translation,
															 PackageStructure packageStructure,
															 List<PageStructure> pageStructures,
															 List<TranslationElement> translationElementList,
															 List<Image> referencedImages,
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
		godToolsTranslation.gtPackage = gtPackage;
		godToolsTranslation.language = language;

		godToolsTranslation.setImages(referencedImages);
		godToolsTranslation.setIcon(icon);

		godToolsTranslation.translation = translation;

		return godToolsTranslation;
	}

	public Optional<PageStructure> getPage(String pageName)
	{
		for(PageStructure pageStructure : pageStructureList)
		{
			if(Objects.equal(pageStructure.getFilename(), pageName))
			{
				return Optional.fromNullable(pageStructure);
			}
		}

		return Optional.absent();
	}

	/**
	 * You can't have one without the other
	 * @return
	 */
	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(17, 83)  // two randomly chosen prime numbers (as random as random can be...)
				.append(gtPackage.getCode())
				.append(translation.isDraft())
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
		GodToolsTranslation godToolsTranslation = (GodToolsTranslation) obj;

		return new EqualsBuilder()
				.append(this.gtPackage.getCode(), godToolsTranslation.gtPackage.getCode())
				.append(this.translation.isDraft(), godToolsTranslation.translation.isDraft())
				.isEquals();
	}

	public String getPackageCode()
	{
		return gtPackage.getCode();
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
		return translation.isDraft();
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
		return translation.getTranslatedName();
	}

	public BigDecimal getVersionNumber()
	{
		return new BigDecimal(packageStructure.getVersionNumber() + "." + translation.getVersionNumber());
	}

	public Translation getTranslation()
	{
		return translation;
	}

	public Package getPackage()
	{
		return gtPackage;
	}

	public Language getLanguage()
	{
		return language;
	}
}
