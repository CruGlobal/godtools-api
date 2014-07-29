package org.cru.godtools.domain.translations;

import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created by ryancarlson on 5/6/14.
 */
public class TranslationStatus
{
	private UUID pageStructureId;
	private UUID translationId;
	private BigDecimal percentCompleted;
	private Integer stringCount;
	private Integer wordCount;
	private DateTime lastUpdated;

	public TranslationStatus()
	{
	}

	public TranslationStatus(UUID translationId, UUID pageStructureId, BigDecimal percentCompleted, Integer stringCount, Integer wordCount, DateTime currentTime)
	{
		this.pageStructureId = pageStructureId;
		this.translationId = translationId;
		this.percentCompleted = percentCompleted;
		this.stringCount = stringCount;
		this.wordCount = wordCount;
		this.lastUpdated = currentTime;
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

	public BigDecimal getPercentCompleted()
	{
		return percentCompleted;
	}

	public void setPercentCompleted(BigDecimal percentCompleted)
	{
		this.percentCompleted = percentCompleted;
	}

	public Integer getStringCount()
	{
		return stringCount;
	}

	public void setStringCount(Integer stringCount)
	{
		this.stringCount = stringCount;
	}

	public Integer getWordCount()
	{
		return wordCount;
	}

	public void setWordCount(Integer wordCount)
	{
		this.wordCount = wordCount;
	}

	public DateTime getLastUpdated()
	{
		return lastUpdated;
	}

	public void setLastUpdated(DateTime lastUpdated)
	{
		this.lastUpdated = lastUpdated;
	}
}
