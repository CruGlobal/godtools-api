package org.cru.godtools.api.translations;

import org.ccci.util.time.Clock;
import org.cru.godtools.domain.languages.LanguageCode;
import org.cru.godtools.domain.packages.PageStructure;
import org.cru.godtools.domain.packages.PageStructureService;
import org.cru.godtools.domain.packages.TranslationElement;
import org.cru.godtools.domain.packages.TranslationElementService;
import org.cru.godtools.domain.translations.Translation;
import org.cru.godtools.translate.client.TranslationDownload;
import org.cru.godtools.translate.client.TranslationResults;
import org.cru.godtools.translate.client.TranslationStatus;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

/**
 * Created by ryancarlson on 7/24/14.
 */
public class DraftTranslationUpdate
{

	@Inject TranslationDownload translationDownload;
	@Inject TranslationElementService translationElementService;
	@Inject PageStructureService pageStructureService;
	@Inject Clock clock;

	public void updateFromTranslationTool(Integer translationProjectId,
															Translation translation,
															List<PageStructure> pageStructures,
															LanguageCode languageCode)
	{
		for(PageStructure pageStructure : pageStructures)
		{
			TranslationStatus remoteStatus = loadRemoteStatus(translationProjectId, languageCode, pageStructure);

			// this feature for optimization is removed until oneskyapp.com reviews my request
			// to have string count and word count reflect the status of the translation, not
			// the base content.  for now, all files are downloaded every time... :(
			// if(remoteStatus.differsFrom(cachedStatus))
			{
				TranslationResults downloadResults = translationDownload.doDownload(translationProjectId,
						languageCode.toString(),
						pageStructure.getFilename());

				updateLocalTranslationElements(downloadResults, translation);
				pageStructure.updateCachedStatus(remoteStatus.getPercentCompleted(),
						remoteStatus.getWordCount(),
						remoteStatus.getStringCount(),
						clock.currentDateTime());
				pageStructureService.update(pageStructure);
			}
		}
	}

	private void updateLocalTranslationElements(TranslationResults translationResults, Translation translation)
	{
		for(UUID elementId : translationResults.keySet())
		{
			TranslationElement element = translationElementService.selectyByIdTranslationId(elementId, translation.getId());
			element.setTranslatedText(translationResults.get(elementId));
			translationElementService.update(element);
		}
	}

	private TranslationStatus loadRemoteStatus(Integer projectId, LanguageCode languageCode, PageStructure pageStructure)
	{
		return translationDownload.checkTranslationStatus(projectId, languageCode.toString(), pageStructure.getFilename());
	}
}
