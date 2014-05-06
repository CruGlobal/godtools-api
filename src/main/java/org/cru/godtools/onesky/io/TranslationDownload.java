package org.cru.godtools.onesky.io;

import com.google.common.collect.Multimap;
import org.ccci.util.time.Clock;
import org.cru.godtools.api.packages.OneSkyDataService;
import org.cru.godtools.api.packages.domain.TranslationElement;
import org.cru.godtools.onesky.client.TranslationClient;
import org.cru.godtools.onesky.client.TranslationResults;
import org.cru.godtools.onesky.domain.TranslationStatus;

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
		org.cru.godtools.onesky.client.TranslationStatus remoteTranslationStatus = getRemoteTranslationStatus(translationId, pageStructureId);

		if(force || isDownloadRequired(oneSkyDataService.getLocalTranslationStatus(translationId, pageStructureId), remoteTranslationStatus))
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
			oneSkyDataService.updateLocalTranslationStatuses(translationId, remoteTranslationStatus);
		}
	}

	private boolean isDownloadRequired(TranslationStatus localTranslationStatus, org.cru.godtools.onesky.client.TranslationStatus remoteTranslationStatus)
	{
		//if we're less than one hour from the last update, then don't bother querying the endpoint
		if(clock.currentDateTime().isBefore(localTranslationStatus.getLastUpdated().plusHours(1))) return false;

		return remoteTranslationStatus.getPercentCompleted().compareTo(localTranslationStatus.getPercentCompleted()) == 0 &&
				remoteTranslationStatus.getWordCount() == localTranslationStatus.getWordCount() &&
				remoteTranslationStatus.getStringCount() == localTranslationStatus.getStringCount();
	}

	private org.cru.godtools.onesky.client.TranslationStatus getRemoteTranslationStatus(UUID translationId, UUID pageStructureId)
	{
		return translationClient.getStatus(oneSkyDataService.getOneskyProjectId(translationId),
				oneSkyDataService.getLocale(translationId),
				oneSkyDataService.getPageFilename(pageStructureId));
	}
}
