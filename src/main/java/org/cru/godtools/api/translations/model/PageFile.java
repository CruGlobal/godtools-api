package org.cru.godtools.api.translations.model;

import com.google.common.collect.Maps;
import org.cru.godtools.domain.model.TranslationElement;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

/**
 * Created by ryancarlson on 2/26/15.
 */
@XmlRootElement
public class PageFile
{
	Map<UUID, String> translatedStrings = Maps.newHashMap();

	public static PageFile fromTranslationElements(Collection<TranslationElement> translationElementCollection)
	{
		PageFile pageFile = new PageFile();

		for(TranslationElement translationElement : translationElementCollection)
		{
			pageFile.translatedStrings.put(translationElement.getId(), translationElement.getTranslatedText());
		}

		return pageFile;
	}

	public Map<UUID, String> getTranslatedStrings()
	{
		return translatedStrings;
	}

	public void setTranslatedStrings(Map<UUID, String> translatedStrings)
	{
		this.translatedStrings = translatedStrings;
	}
}
