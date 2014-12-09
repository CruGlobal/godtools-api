package org.cru.godtools.translate.client.onesky;

import org.cru.godtools.domain.properties.GodToolsProperties;
import org.cru.godtools.translate.client.TranslationResults;
import org.cru.godtools.translate.client.TranslationDownload;
import org.jboss.logging.Logger;
import org.cru.godtools.translate.client.TranslationStatus;

import javax.inject.Inject;

/**
 * Created by ryancarlson on 7/18/14.
 */
public class OneSkyTranslationDownload implements TranslationDownload
{
	private TranslationClient translationClient;

	@Inject
	public OneSkyTranslationDownload(TranslationClient translationClient)
	{
		this.translationClient = translationClient;
	}

	@Inject
	private GodToolsProperties properties;

	private Logger log = Logger.getLogger(OneSkyTranslationUpload.class);

	@Override
	public TranslationResults doDownload(Integer oneSkyProjectId, String locale, String pageName)
	{
		if(!Boolean.parseBoolean(properties.getProperty("oneskyIntegrationEnabled", "true")))
		{
			log.info("Onesky integration disabled on this server.  Check configuration settings");
			return new OneSkyTranslationResults();
		}

		log.info("Download translation file: " + pageName + " from OneSky for project ID: " + oneSkyProjectId + " and locale: " + locale);

		return translationClient.export(oneSkyProjectId, locale, pageName);
	}

	@Override
	public TranslationStatus checkTranslationStatus(Integer projectId, String locale, String pageName)
	{
		if(!Boolean.parseBoolean(properties.getProperty("oneskyIntegrationEnabled", "true")))
		{
			log.info("Onesky integration disabled on this server.  Check configuration settings");
			return new OneSkyTranslationStatus();
		}

		return translationClient.getStatus(projectId, locale, pageName);
	}
}
