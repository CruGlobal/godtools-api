package org.cru.godtools.domain.packages;

import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by ryancarlson on 4/30/14.
 */
public class TranslationElement
{
	private UUID id;
	private UUID pageStructureId;
	private UUID translationId;
	private String baseText;
	private String translatedText;
	private String elementType;
	private String pageName;
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
