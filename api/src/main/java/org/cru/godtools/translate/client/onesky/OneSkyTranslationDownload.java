package org.cru.godtools.translate.client.onesky;

import org.cru.godtools.translate.client.TranslationResults;
import org.cru.godtools.translate.client.TranslationDownload;
import org.jboss.logging.Logger;

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

	private Logger log = Logger.getLogger(OneSkyTranslationUpload.class);

	@Override
	public TranslationResults doDownload(Integer oneSkyProjectId, String locale, String pageName)
	{
		log.info("Download translation file: " + pageName + " from OneSky for project ID: " + oneSkyProjectId + " and locale: " + locale);

		return translationClient.export(oneSkyProjectId, locale, pageName);
	}
}
