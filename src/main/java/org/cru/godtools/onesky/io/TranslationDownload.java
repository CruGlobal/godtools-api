package org.cru.godtools.onesky.io;

import com.google.common.collect.Multimap;
import org.cru.godtools.api.packages.OneSkyDataService;
import org.cru.godtools.api.packages.domain.TranslationElement;
import org.cru.godtools.onesky.client.TranslationClient;
import org.cru.godtools.onesky.client.TranslationResults;
import org.cru.godtools.onesky.domain.TranslationStatusService;

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
	private TranslationStatusService translationStatusService;

	@Inject
	public TranslationDownload(OneSkyDataService oneSkyDataService, TranslationClient translationClient, TranslationStatusService translationStatusService)
	{
		this.oneSkyDataService = oneSkyDataService;
		this.translationClient = translationClient;
		this.translationStatusService = translationStatusService;
	}

	public void doDownload(UUID translationId)
	{
		Multimap<String, TranslationElement> translationElementMultimap = oneSkyDataService.getTranslationElements(translationId);

		for(String pageName : translationElementMultimap.keySet())
		{
			try
			{
				TranslationResults translationResults = translationClient.export(oneSkyDataService.getOneskyProjectId(translationId),
																					oneSkyDataService.getLocale(translationId),
																					pageName);

				for(TranslationElement localTranslationElement : translationElementMultimap.get(pageName))
				{
					if(translationResults.containsKey(localTranslationElement.getId()))
					{
						localTranslationElement.setTranslatedText(translationResults.get(localTranslationElement.getId()));
					}
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();;
			}
		}

		oneSkyDataService.saveTranslationElements(translationElementMultimap);

//		translationStatusService...
	}
}
