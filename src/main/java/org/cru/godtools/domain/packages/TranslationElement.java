package org.cru.godtools.domain.packages;

import com.google.common.collect.Maps;
import org.cru.godtools.domain.translations.*;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by ryancarlson on 4/30/14.
 */
@Entity
@Table(name="translation_elements")
public class TranslationElement
{
	@EmbeddedId
	/*@AttributeOverrides({
			@AttributeOverride(name="id", column = @Column(name="id")),
			@AttributeOverride(name="translationId", column = @Column(name="translation_id"))
	})*/
	private TranslationElementKey translationElementId = new TranslationElementKey();

	@ManyToOne
	@JoinColumn(name="page_structure_id")
	private PageStructure pageStructure;
	@Column(name="base_text")
	private String baseText;
	@Column(name="translated_text")
	private String translatedText;
	@Column(name="element_type")
	private String elementType;
	@Column(name="page_name")
	private String pageName;
	@Column(name="display_order")
	private Integer displayOrder;

	public static Map<UUID, TranslationElement> createMapOfTranslationElements(List<TranslationElement> translationElementList)
	{
		Map<UUID, TranslationElement> translationElementMap = Maps.newHashMap();

		for(TranslationElement translationElement : translationElementList)
		{
			translationElementMap.put(translationElement.getId(), translationElement);
		}

		return translationElementMap;
	}

	public static TranslationElement copyOf(TranslationElement translationElement)
	{
		TranslationElement translationElementCopy = new TranslationElement();

		translationElementCopy.setPageStructure(translationElement.getPageStructure());
		translationElementCopy.setTranslation((translationElement.getTranslation()));
		translationElementCopy.setId(translationElement.getId());
		translationElementCopy.setBaseText(translationElement.getBaseText());
		translationElementCopy.setTranslatedText(translationElement.getTranslatedText());
		translationElementCopy.setElementType(translationElement.getElementType());
		translationElementCopy.setPageName(translationElement.getPageName());
		translationElementCopy.setDisplayOrder(translationElement.getDisplayOrder());

		return translationElementCopy;
	}

	public UUID getId() { return translationElementId.getId(); }

	public void setId(UUID id) { translationElementId.setId(id); }

	public PageStructure getPageStructure() { return pageStructure; }

	public void setPageStructure(PageStructure pageStructure) { this.pageStructure = pageStructure; }

	public Translation getTranslation() { return translationElementId.getTranslation(); }

	public void setTranslation(Translation translation) { translationElementId.setTranslation(translation); }

	//deprecated method, keep for SQL2O
	public void setPageStructureId(UUID pageStructureId)
	{
		if(pageStructure == null)
		{
			pageStructure = new PageStructure();
			pageStructure.setId(pageStructureId);
		}
	}

	//deprecated mathod, keep for SQL2O
	public void setTranslationId(UUID translationId)
	{
		if(translationElementId.getTranslation() == null)
		{
			translationElementId.setTranslation(new Translation());
			translationElementId.getTranslation().setId(translationId);
		}
	}

	public String getBaseText()
	{
		return baseText;
	}

	public void setBaseText(String baseText)
	{
		this.baseText = baseText;
	}

	public String getTranslatedText()
	{
		return translatedText;
	}

	public void setTranslatedText(String translatedText)
	{
		this.translatedText = translatedText;
	}

	public String getElementType()
	{
		return elementType;
	}

	public void setElementType(String elementType)
	{
		this.elementType = elementType;
	}

	public String getPageName()
	{
		return pageName;
	}

	public void setPageName(String pageName)
	{
		this.pageName = pageName;
	}

	public Integer getDisplayOrder()
	{
		return displayOrder;
	}

	public void setDisplayOrder(Integer displayOrder)
	{
		this.displayOrder = displayOrder;
	}
}
