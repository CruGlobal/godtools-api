package org.cru.godtools.onesky.client;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.cru.godtools.properties.GodToolsProperties;
import org.cru.godtools.properties.GodToolsPropertiesFactory;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

/**
 * Created by ryancarlson on 5/5/14.
 */
public class TranslationClient
{
	public static final String SUB_PATH = "/translations";

	private final GodToolsProperties properties = new GodToolsPropertiesFactory().get();

	public JsonNode exportTranslation(Integer projectId, String locale, String pageName) throws Exception
	{
		WebTarget target = OneSkyClientBuilder.buildTarget(projectId, SUB_PATH)
				.queryParam("locale", locale)
				.queryParam("source_file_name", pageName)
				.queryParam("export_file_name", pageName);

		target = addAuthentication(target);

		return parseResponse(target.request().get());
	}

	private WebTarget addAuthentication(WebTarget target) throws Exception
	{
		long timestamp = System.currentTimeMillis() / 1000;

		 return target.queryParam("api_key", properties.get("oneskyApiKey"))
						.queryParam("timestamp", timestamp)
						.queryParam("dev_hash", OneSkyClientBuilder.createDevHash(timestamp, (String)properties.get("oneskySecretKey")));
	}

	private JsonNode parseResponse(Response response) throws Exception
	{
		return new ObjectMapper().readTree(response.readEntity(String.class));
	}
}
