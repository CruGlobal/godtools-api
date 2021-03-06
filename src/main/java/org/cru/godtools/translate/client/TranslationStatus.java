package org.cru.godtools.translate.client;


import org.cru.godtools.domain.packages.PageStructure;

import javax.ws.rs.core.Response;
import java.math.BigDecimal;

/**
 * Created by ryancarlson on 7/18/14.
 */
public abstract class TranslationStatus
{
	protected int statusCode;
	protected String filename;
	protected BigDecimal percentCompleted;
	protected int stringCount;
	protected int wordCount;

	public boolean differsFrom(PageStructure pageStructure)
	{
		return percentCompleted.compareTo(pageStructure.getPercentCompleted()) != 0 ||
				Integer.valueOf(stringCount).compareTo(pageStructure.getStringCount()) != 0 ||
				Integer.valueOf(wordCount).compareTo(pageStructure.getWordCount()) != 0;
	}

	public abstract TranslationStatus createInitialJustUploadedStatus(String pageName);

	public abstract TranslationStatus createFromResponse(Response response);

	public int getStatusCode()
	{
		return statusCode;
	}

	public void setStatusCode(int statusCode)
	{
		this.statusCode = statusCode;
	}

	public String getFilename()
	{
		return filename;
	}

	public void setFilename(String filename)
	{
		this.filename = filename;
	}

	public BigDecimal getPercentCompleted()
	{
		return percentCompleted;
	}

	public void setPercentCompleted(BigDecimal percentCompleted)
	{
		this.percentCompleted = percentCompleted;
	}

	public int getStringCount()
	{
		return stringCount;
	}

	public void setStringCount(int stringCount)
	{
		this.stringCount = stringCount;
	}

	public int getWordCount()
	{
		return wordCount;
	}

	public void setWordCount(int wordCount)
	{
		this.wordCount = wordCount;
	}
}
