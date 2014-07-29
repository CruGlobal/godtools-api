package org.cru.godtools.translate.client.onesky;

import com.google.common.base.Throwables;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.cru.godtools.translate.client.TranslationStatus;

import javax.ws.rs.core.Response;
import java.math.BigDecimal;

/**
 * Created by ryancarlson on 5/6/14.
 */
public class OneSkyTranslationStatus extends TranslationStatus
{
	@Override
	public OneSkyTranslationStatus createInitialJustUploadedStatus(String pageName)
	{
		OneSkyTranslationStatus oneSkyTranslationStatus = new OneSkyTranslationStatus();
		oneSkyTranslationStatus.setStatusCode(202); //accepted
		oneSkyTranslationStatus.setPercentCompleted(new BigDecimal(0f));
		oneSkyTranslationStatus.setStringCount(0);
		oneSkyTranslationStatus.setWordCount(0);
		oneSkyTranslationStatus.setFilename(pageName);

		return oneSkyTranslationStatus;
	}

	@Override
	public OneSkyTranslationStatus createFromResponse(Response response)
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
}
