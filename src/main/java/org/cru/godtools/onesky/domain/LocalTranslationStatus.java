package org.cru.godtools.onesky.domain;

import org.cru.godtools.onesky.client.OneSkyTranslationStatus;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created by ryancarlson on 5/6/14.
 */
public class LocalTranslationStatus
{
	private UUID pageStructureId;
	private UUID translationId;
	private BigDecimal percentCompleted;
	private Integer stringCount;
	private Integer wordCount;
	private DateTime lastUpdated;

	public LocalTranslationStatus()
	{
	}

	public LocalTranslationStatus(UUID translationId, UUID pageStructureId, OneSkyTranslationStatus oneSkyTranslationStatus, DateTime currentTime)
	{
		this.pageStructureId = pageStructureId;
		this.translationId = translationId;
		this.percentCompleted = oneSkyTranslationStatus.getPercentCompleted();
		this.stringCount = oneSkyTranslationStatus.getStringCount();
		this.wordCount = oneSkyTranslationStatus.getWordCount();
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
