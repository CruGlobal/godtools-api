package org.cru.godtools.onesky.client;

import com.google.common.base.Throwables;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import javax.ws.rs.core.Response;
import java.math.BigDecimal;

/**
 * Created by ryancarlson on 5/6/14.
 */
public class TranslationStatus
{
	private int statusCode;
	private String filename;
	private BigDecimal percentCompleted;
	private int stringCount;
	private int wordCount;

	public static TranslationStatus createFromResponse(Response response)
	{
		TranslationStatus translationStatus = new TranslationStatus();
		JsonNode metaResponse = translationStatus.getResponseEntity(response);

		if(response.getStatus() == 200)
		{
			translationStatus.setStatusCode(metaResponse.get("meta").get("status").asInt());
			translationStatus.setFilename(metaResponse.get("data").get("file_name").asText());
			translationStatus.setPercentCompleted(new BigDecimal(metaResponse.get("data").get("progress").asText().replaceAll("%","")));
			translationStatus.setStringCount(metaResponse.get("data").get("string_count").asInt());
			translationStatus.setWordCount(metaResponse.get("data").get("word_count").asInt());

			return translationStatus;
		}
		else
		{
			translationStatus.setStatusCode(502);
			return translationStatus;
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
