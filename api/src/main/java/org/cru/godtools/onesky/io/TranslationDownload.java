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

	public void doDownload(UUID translationId, UUID pageStructureId)
	{
		Multimap<String, TranslationElement> translationElementMultimap = oneSkyDataService.getTranslationElements(translationId);
		String pageName = oneSkyDataService.getPageFilename(pageStructureId);

		try
		{
			TranslationResults translationResults = translationClient.export(oneSkyDataService.getOneskyProjectId(translationId),
					oneSkyDataService.getLocale(translationId),
					pageName);

			if(translationResults.getStatusCode() != 200) return;

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
		oneSkyDataService.updateLocalTranslationStatus(translationId, pageStructureId, getRemoteTranslationStatus(translationId, pageStructureId));
	}

	private OneSkyTranslationStatus getRemoteTranslationStatus(UUID translationId, UUID pageStructureId)
	{
		return translationClient.getStatus(oneSkyDataService.getOneskyProjectId(translationId),
				oneSkyDataService.getLocale(translationId),
				oneSkyDataService.getPageFilename(pageStructureId));
	}
}
