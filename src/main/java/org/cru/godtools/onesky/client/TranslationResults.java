package org.cru.godtools.onesky.client;

import com.google.common.base.Throwables;
import com.google.common.collect.ForwardingMap;
import com.google.common.collect.Maps;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by ryancarlson on 5/6/14.
 */
public class TranslationResults extends ForwardingMap<UUID, String>
{

	Map<UUID, String> translatedStringsMap;

	@Override
	protected Map<UUID, String> delegate()
	{
		return translatedStringsMap;
	}

	public static TranslationResults createFromResponse(Response response)
	{
		TranslationResults translationResults = new TranslationResults();
		translationResults.translatedStringsMap = Maps.newHashMap();

		//this extra step is required b/c i want the keys stored as UUID.  I don't trust Jackson to convert the String ID
		//to a UUID, b/c I don't think UUID has a valueOf or String constructor.  I could work.. just haven't tried.
		Map<String, String> stringMapOfJsonReturnedFromApi = translationResults.parseResponse(response);

		for(String translationElementIdAsString : stringMapOfJsonReturnedFromApi.keySet())
		{
			translationResults.translatedStringsMap.put(UUID.fromString(translationElementIdAsString), stringMapOfJsonReturnedFromApi.get(translationElementIdAsString));
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
			Throwables.propagate(e);
			return null;
		}
	}
}
