package org.cru.godtools.api.packages.domain;

import java.util.UUID;

/**
 * Created by ryancarlson on 4/30/14.
 */
public class TranslationElement
{
	private UUID id;
	private UUID translationId;
	private String baseText;
	private String translatedText;
	private String elementType;
	private String pageName;
	private Integer displayOrder;

	public UUID getId()
	{
		return id;
	}

	public void setId(UUID id)
	{
		this.id = id;
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
