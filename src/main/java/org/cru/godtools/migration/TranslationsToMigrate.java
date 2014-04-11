package org.cru.godtools.migration;

import com.google.common.collect.Sets;
import org.cru.godtools.api.languages.Language;
import org.cru.godtools.api.packages.utils.LanguageCode;

import java.util.Set;

/**
 * Created by ryancarlson on 4/11/14.
 */
public class TranslationsToMigrate
{
	private Set<LanguageCode> desiredTranslations = Sets.newHashSet();

	public TranslationsToMigrate()
	{
		desiredTranslations.add(new LanguageCode("en"));
		desiredTranslations.add(new LanguageCode("et"));
		desiredTranslations.add(new LanguageCode("et_heartbeat"));
	}

	public boolean isDesiredTranslation(LanguageCode languageCode)
	{
		return desiredTranslations.contains(languageCode);
	}

}
