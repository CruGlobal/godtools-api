package org.cru.godtools.translate.client.onesky;

import com.google.common.base.Throwables;
import org.cru.godtools.domain.properties.GodToolsProperties;
import org.cru.godtools.domain.properties.GodToolsPropertiesFactory;
import org.cru.godtools.translate.client.TranslationResults;
import org.cru.godtools.translate.client.TranslationStatus;
import org.jboss.logging.Logger;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

/**
 * * Client for endpoint described here: https://github.com/onesky/api-documentation-platform/blob/master/resources/translation.md
 *
 * Created by ryancarlson on 5/5/14.
 */
public class TranslationClient
{
	public static final String SUB_PATH = "/translations";

	private final GodToolsProperties properties = new GodToolsPropertiesFactory().get();

	private Logger log = Logger.getLogger(TranslationClient.class);

	/**
	 * Retrieves a file of translated text from oneskyapp.com
	 *
	 * @param projectId - the project's ID in oneskyapp. project would be one of (kgp, fourlaws, satisfied, etc...).
	 *                     the id itself is numeric
	 * @param locale - the locale of the page being download (ex: "fr" for French)
	 * @param pageName - the name of the current page being downloaded.
	 */
	public TranslationResults export(Integer projectId, String locale, String pageName)
	{
		WebTarget target = OneSkyClientBuilder.buildTarget(projectId, SUB_PATH)
				.queryParam("locale", locale)
				.queryParam("source_file_name", pageName)
				.queryParam("export_file_name", pageName);

		target = addAuthentication(target);

		Response response = target
				.request()
				.accept("application/json;charset=utf-8")
				.get();

		log.info("Download response status code: " + response.getStatus());

		return new OneSkyTranslationResults().createFromResponse(response);
	}

	/**
	 * Gets the status of a translated file in oneskyapp.com
	 *
	 * @param projectId - the project's ID in oneskyapp. project would be one of (kgp, fourlaws, satisfied, etc...).
	 *                     the id itself is numeric
	 * @param locale - the locale of the page being download (ex: "fr" for French)
	 * @param pageName - the name of the current page being downloaded.
	 */
	public TranslationStatus getStatus(Integer projectId, String locale, String pageName)
	{
		log.info("Getting translation status for file: " + pageName + " from OneSky for project ID: " + projectId + " and locale: " + locale);

		WebTarget target = OneSkyClientBuilder.buildTarget(projectId, SUB_PATH + "/status")
				.queryParam("locale", locale)
				.queryParam("file_name", pageName);

		target = addAuthentication(target);

		Response response = target.request().get();

		log.info("Status response code: " + response.getStatus());

		return new OneSkyTranslationStatus().createFromResponse(response);
	}

	/**
	 * Sets the api_key (public key), timestamp (in milliseconds) and hash of timestamp and private key to authenticate.
	 */
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
			throw Throwables.propagate(e);
		}
	}
}
