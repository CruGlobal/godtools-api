package org.cru.godtools.migration;

import org.cru.godtools.api.languages.Language;
import org.cru.godtools.api.packages.utils.LanguageCode;

/**
 * Created by ryancarlson on 4/11/14.
 */
public class EstonianLanguageCode
{
	/**
	 * This method is here as a special case.  The functional owners want the Estonian translation to be
	 * recorded as et instead of et_heartbeat (dropping the heartbeat)
	 *
	 * @param language
	 */
	public void removeHeartbeatSubculture(Language language)
	{
		if(LanguageCode.fromLanguage(language).equals(new LanguageCode("et_heartbeat")))
		{
			language.setSubculture(null);
		}
	}

	/**
	 * Sometimes we need to add the parameter back in, mainly to load things off the filesystem where the
	 * path is en_heartbeat
	 *
	 * @param language
	 */
	public void addHeartbeatSubculture(Language language)
	{
		if(LanguageCode.fromLanguage(language).equals(new LanguageCode("et")))
		{
			language.setSubculture("heartbeat");
		}
	}
}
