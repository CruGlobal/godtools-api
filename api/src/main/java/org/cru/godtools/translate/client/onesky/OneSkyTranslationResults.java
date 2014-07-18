package org.cru.godtools.translate.client.onesky;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import org.cru.godtools.translate.client.TranslationResults;

import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by ryancarlson on 7/18/14.
 */
public class OneSkyTranslationResults extends TranslationResults
{

	public OneSkyTranslationResults()
	{
		this.translatedStringsMap = Maps.newHashMap();
	}

	public TranslationResults createFromResponse(Response response)
	{
		TranslationResults translationResults = new OneSkyTranslationResults();

		translationResults.setStatusCode(response.getStatus());
		if(response.getStatus() == 200)
		{
			// this extra step is required b/c i want the keys stored as UUID.  I don't trust Jackson to convert the String ID
			// to a UUID, b/c I don't think UUID has a valueOf or String constructor.  I could work.. just haven't tried.
			Map<String, String> stringMapOfJsonReturnedFromApi = parseResponse(response);

			// now take Map<String,String>  and convert it to Map<UUID,String>
			for (String translationElementIdAsString : stringMapOfJsonReturnedFromApi.keySet())
			{
				translationResults.put(UUID.fromString(translationElementIdAsString), stringMapOfJsonReturnedFromApi.get(translationElementIdAsString));
			}
		}
		return translationResults;
	}

	private Map<String, String> parseResponse(Response response)
	{
		try
		{
			TypeReference<HashMap<String, String>> typeReference = new TypeReference<HashMap<String, String>>() { };

			return new ObjectMapper().readValue(response.readEntity(String.class), typeReference);
		}
		catch (Exception e)
		{
			throw Throwables.propagate(e);
		}
	}
}
