package org.cru.godtools.translate.client.onesky;

import org.cru.godtools.translate.client.TranslationResults;
import org.cru.godtools.translate.client.TranslationDownload;

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

	@Override
	public TranslationResults doDownload(Integer oneSkyProjectId, String locale, String pageName)
	{
		return translationClient.export(oneSkyProjectId, locale, pageName);
	}
}
