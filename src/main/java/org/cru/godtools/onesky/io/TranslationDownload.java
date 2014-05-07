package org.cru.godtools.onesky.io;

import com.google.common.collect.Multimap;
import org.ccci.util.time.Clock;
import org.cru.godtools.api.packages.OneSkyDataService;
import org.cru.godtools.api.packages.domain.TranslationElement;
import org.cru.godtools.onesky.client.OneSkyTranslationStatus;
import org.cru.godtools.onesky.client.TranslationClient;
import org.cru.godtools.onesky.client.TranslationResults;
import org.cru.godtools.onesky.domain.LocalTranslationStatus;

import javax.inject.Inject;
import java.util.UUID;

/**
 *
 * Created by ryancarlson on 5/6/14.
 */
public class TranslationDownload
{
	private OneSkyDataService oneSkyDataService;
	private TranslationClient translationClient;
	private Clock clock;

	@Inject
	public TranslationDownload(OneSkyDataService oneSkyDataService, TranslationClient translationClient, Clock clock)
	{
		this.oneSkyDataService = oneSkyDataService;
		this.translationClient = translationClient;
		this.clock = clock;
	}

	public void doDownload(UUID translationId, UUID pageStructureId, boolean force)
	{
		OneSkyTranslationStatus oneskyTranslationStatus = getRemoteTranslationStatus(translationId, pageStructureId);

		if(force || isDownloadRequired(oneSkyDataService.getLocalTranslationStatus(translationId, pageStructureId), oneskyTranslationStatus))
		{
			Multimap<String, TranslationElement> translationElementMultimap = oneSkyDataService.getTranslationElements(translationId);

			for (String pageName : translationElementMultimap.keySet())
			{
				try
				{
					TranslationResults translationResults = translationClient.export(oneSkyDataService.getOneskyProjectId(translationId),
							oneSkyDataService.getLocale(translationId),
							pageName);

					for (TranslationElement localTranslationElement : translationElementMultimap.get(pageName))
					{
						if (translationResults.containsKey(localTranslationElement.getId()))
						{
							localTranslationElement.setTranslatedText(translationResults.get(localTranslationElement.getId()));
						}
					}
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}

			oneSkyDataService.saveTranslationElements(translationElementMultimap);
			oneSkyDataService.updateLocalTranslationStatuses(translationId, oneskyTranslationStatus);
		}
	}

	private boolean isDownloadRequired(LocalTranslationStatus localTranslationStatus, OneSkyTranslationStatus oneSkyTranslationStatus)
	{
		//if we're less than one hour from the last update, then don't bother querying the endpoint
		if(clock.currentDateTime().isBefore(localTranslationStatus.getLastUpdated().plusHours(1))) return false;

		return oneSkyTranslationStatus.getPercentCompleted().compareTo(localTranslationStatus.getPercentCompleted()) == 0 &&
				oneSkyTranslationStatus.getWordCount() == localTranslationStatus.getWordCount() &&
				oneSkyTranslationStatus.getStringCount() == localTranslationStatus.getStringCount();
	}

	private OneSkyTranslationStatus getRemoteTranslationStatus(UUID translationId, UUID pageStructureId)
	{
		return translationClient.getStatus(oneSkyDataService.getOneskyProjectId(translationId),
				oneSkyDataService.getLocale(translationId),
				oneSkyDataService.getPageFilename(pageStructureId));
	}
}
