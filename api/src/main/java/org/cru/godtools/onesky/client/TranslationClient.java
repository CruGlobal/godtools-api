package org.cru.godtools.onesky.client;

import com.google.common.base.Throwables;
import org.cru.godtools.domain.properties.GodToolsProperties;
import org.cru.godtools.domain.properties.GodToolsPropertiesFactory;

import javax.ws.rs.client.WebTarget;

/**
 * Created by ryancarlson on 5/5/14.
 */
public class TranslationClient
{
	public static final String SUB_PATH = "/translations";

	private final GodToolsProperties properties = new GodToolsPropertiesFactory().get();

	public TranslationResults export(Integer projectId, String locale, String pageName)
	{
		WebTarget target = OneSkyClientBuilder.buildTarget(projectId, SUB_PATH)
				.queryParam("locale", locale)
				.queryParam("source_file_name", pageName)
				.queryParam("export_file_name", pageName);

		target = addAuthentication(target);

		return TranslationResults.createFromResponse(target.request().get());
	}

	public OneSkyTranslationStatus getStatus(Integer projectId, String locale, String pageName)
	{
		WebTarget target = OneSkyClientBuilder.buildTarget(projectId, SUB_PATH + "/status")
				.queryParam("locale", locale)
				.queryParam("file_name", pageName);

		target = addAuthentication(target);

		return OneSkyTranslationStatus.createFromResponse(target.request().get());
	}

	private WebTarget addAuthentication(WebTarget target)
	{
		long timestamp = System.currentTimeMillis() / 1000;

		try
		{
			return target.queryParam("api_key", properties.get("oneskyApiKey"))
					.queryParam("timestamp", timestamp)
					.queryParam("dev_hash", OneSkyClientBuilder.createDevHash(timestamp, (String) properties.get("oneskySecretKey")));
		}
		catch(Exception e)
		{
			Throwables.propagate(e);
			return null;
		}
	}


}
