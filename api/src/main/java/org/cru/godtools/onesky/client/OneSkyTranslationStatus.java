package org.cru.godtools.onesky.client;

import com.google.common.base.Throwables;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import javax.ws.rs.core.Response;
import java.math.BigDecimal;

/**
 * Created by ryancarlson on 5/6/14.
 */
public class OneSkyTranslationStatus
{
	private int statusCode;
	private String filename;
	private BigDecimal percentCompleted;
	private int stringCount;
	private int wordCount;

	public static OneSkyTranslationStatus createInitialJustUploadedStatus(String pageName)
	{
		OneSkyTranslationStatus oneSkyTranslationStatus = new OneSkyTranslationStatus();
		oneSkyTranslationStatus.setStatusCode(202); //accepted
		oneSkyTranslationStatus.setPercentCompleted(new BigDecimal(0f));
		oneSkyTranslationStatus.setStringCount(0);
		oneSkyTranslationStatus.setWordCount(0);
		oneSkyTranslationStatus.setFilename(pageName);

		return oneSkyTranslationStatus;
	}

	public static OneSkyTranslationStatus createFromResponse(Response response)
	{
		OneSkyTranslationStatus oneSkyTranslationStatus = new OneSkyTranslationStatus();
		JsonNode metaResponse = oneSkyTranslationStatus.getResponseEntity(response);

		if(response.getStatus() == 200)
		{
			oneSkyTranslationStatus.setStatusCode(metaResponse.get("meta").get("status").asInt());
			oneSkyTranslationStatus.setFilename(metaResponse.get("data").get("file_name").asText());
			oneSkyTranslationStatus.setPercentCompleted(new BigDecimal(metaResponse.get("data").get("progress").asText().replaceAll("%","")));
			oneSkyTranslationStatus.setStringCount(metaResponse.get("data").get("string_count").asInt());
			oneSkyTranslationStatus.setWordCount(metaResponse.get("data").get("word_count").asInt());

			return oneSkyTranslationStatus;
		}
		else
		{
			oneSkyTranslationStatus.setStatusCode(response.getStatus());
			return oneSkyTranslationStatus;
		}
	}

	private JsonNode getResponseEntity(Response response)
	{
		try
		{
			return new ObjectMapper().readTree(response.readEntity(String.class));
		}
		catch(Exception e)
		{
			Throwables.propagate(e);
			return null;
		}
	}

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
