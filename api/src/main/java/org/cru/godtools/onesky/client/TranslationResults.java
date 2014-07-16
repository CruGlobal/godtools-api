package org.cru.godtools.onesky.client;

import com.google.common.base.Throwables;
import com.google.common.collect.ForwardingMap;
import com.google.common.collect.Maps;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Used to return a Map, representing a Set of translated strings referenced by unique identifiers
 * along with an HTTP status code.  The Set of translated strings corresponds to a page of GodTools
 * content
 *
 * Created by ryancarlson on 5/6/14.
 */
public class TranslationResults extends ForwardingMap<UUID, String>
{
	int statusCode;
	Map<UUID, String> translatedStringsMap;

	@Override
	protected Map<UUID, String> delegate()
	{
		return translatedStringsMap;
	}

	public static TranslationResults createFromResponse(Response response)
	{
		TranslationResults translationResults = new TranslationResults();
		translationResults.statusCode = response.getStatus();
		if(response.getStatus() == 200)
		{
			translationResults.translatedStringsMap = Maps.newHashMap();

			// this extra step is required b/c i want the keys stored as UUID.  I don't trust Jackson to convert the String ID
			// to a UUID, b/c I don't think UUID has a valueOf or String constructor.  I could work.. just haven't tried.
			Map<String, String> stringMapOfJsonReturnedFromApi = translationResults.parseResponse(response);

			// now take Map<String,String>  and convert it to Map<UUID,String>
			for (String translationElementIdAsString : stringMapOfJsonReturnedFromApi.keySet())
			{
				translationResults.translatedStringsMap.put(UUID.fromString(translationElementIdAsString), stringMapOfJsonReturnedFromApi.get(translationElementIdAsString));
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

	public int getStatusCode()
	{
		return statusCode;
	}

	public void setStatusCode(int statusCode)
	{
		this.statusCode = statusCode;
	}
}