package org.cru.godtools.onesky.io;

import com.google.common.collect.Multimap;
import org.ccci.util.time.Clock;
import org.cru.godtools.api.packages.OneSkyDataService;
import org.cru.godtools.domain.packages.TranslationElement;
import org.cru.godtools.onesky.client.OneSkyTranslationStatus;
import org.cru.godtools.onesky.client.TranslationClient;
import org.cru.godtools.onesky.client.TranslationResults;

import javax.inject.Inject;
import java.util.UUID;

/**
 *
 * Created by ryancarlson on 5/6/14.
 */
public class TranslationDownload
{
	private TranslationClient translationClient;

	@Inject
	public TranslationDownload(TranslationClient translationClient)
	{
		this.translationClient = translationClient;
	}

	public TranslationResults doDownload(Integer oneSkyProjectId, String locale, String pageName)
	{
		return translationClient.export(oneSkyProjectId, locale, pageName);
	}

	private OneSkyTranslationStatus getRemoteTranslationStatus(Integer oneSkyProjectId, String locale)
	{
		return null;
	}
}
