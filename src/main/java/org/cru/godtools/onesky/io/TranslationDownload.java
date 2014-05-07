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
		LocalTranslationStatus localTranslationStatus = oneSkyDataService.getLocalTranslationStatus(translationId, pageStructureId);

		if (force || (isDownloadRequiredBasedOnLastUpdatedTime(localTranslationStatus)))
		{
			OneSkyTranslationStatus oneSkyTranslationStatus = getRemoteTranslationStatus(translationId, pageStructureId);

			if (force || isDownloadRequiredBasedOnUpdatedRemoteTranslations(localTranslationStatus, oneSkyTranslationStatus))
			{
				Multimap<String, TranslationElement> translationElementMultimap = oneSkyDataService.getTranslationElements(translationId);
				String pageName = oneSkyDataService.getPageFilename(pageStructureId);

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

				oneSkyDataService.saveTranslationElements(translationElementMultimap.get(pageName));
				oneSkyDataService.updateLocalTranslationStatus(translationId, pageStructureId, oneSkyTranslationStatus);
			}
		}
	}

	private boolean isDownloadRequiredBasedOnLastUpdatedTime(LocalTranslationStatus localTranslationStatus)
	{
		//if there's no record of querying OneSky, then yes, assume we want to update.
		if (localTranslationStatus == null) return true;

		//if we're less than one hour from the last update, then don't bother querying the endpoint
		return clock.currentDateTime().isAfter(localTranslationStatus.getLastUpdated().plusHours(1));
	}

	private boolean isDownloadRequiredBasedOnUpdatedRemoteTranslations(LocalTranslationStatus localTranslationStatus, OneSkyTranslationStatus oneSkyTranslationStatus)
	{
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
