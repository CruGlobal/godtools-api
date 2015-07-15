package org.cru.godtools.domain.packages;

import com.google.common.collect.Maps;
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
	@Id
	private TranslationElementKey translationElementId;
	@Column(insertable = false, updatable = false)
	private UUID id;
	@Column(insertable = false, updatable = false)
	private UUID translationId;

	@Column(name="page_structure_id")
	@Type(type="pg-uuid")
	private UUID pageStructureId;
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

		translationElementCopy.setPageStructureId(translationElement.getPageStructureId());
		translationElementCopy.setTranslationId((translationElement.getTranslationId()));
		translationElementCopy.setId(translationElement.getId());
		translationElementCopy.setBaseText(translationElement.getBaseText());
		translationElementCopy.setTranslatedText(translationElement.getTranslatedText());
		translationElementCopy.setElementType(translationElement.getElementType());
		translationElementCopy.setPageName(translationElement.getPageName());
		translationElementCopy.setDisplayOrder(translationElement.getDisplayOrder());

		return translationElementCopy;
	}

	public TranslationElementKey getTranslationElementId() { return translationElementId; }

	public void setTranslationElementId() { this.translationElementId = translationElementId; }

	public UUID getId()
	{
		return id;
	}

	public void setId(UUID id)
	{
		this.id = id;
	}

	public UUID getPageStructureId()
	{
		return pageStructureId;
	}

	public void setPageStructureId(UUID pageStructureId)
	{
		this.pageStructureId = pageStructureId;
	}

	public UUID getTranslationId()
	{
		return translationId;
	}

	public void setTranslationId(UUID translationId)
	{
		this.translationId = translationId;
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
