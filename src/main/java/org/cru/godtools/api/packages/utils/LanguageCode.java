package org.cru.godtools.api.packages.utils;

import com.google.common.base.Strings;
import org.cru.godtools.api.languages.Language;

/**
 * Created by ryancarlson on 3/24/14.
 */
public class LanguageCode
{
    private final String providedLanguageCode;

    public LanguageCode(String providedLanguageCode)
    {
        this.providedLanguageCode = providedLanguageCode;
    }

    public static LanguageCode fromLanguage(Language language)
    {
        String constructedLanguageCode = language.getCode();
        if(!Strings.isNullOrEmpty(language.getLocale())) constructedLanguageCode += "_" + language.getLocale();
        if(!Strings.isNullOrEmpty(language.getSubculture())) constructedLanguageCode += "_" + language.getSubculture();
        return new LanguageCode(constructedLanguageCode);
    }

    @Override
    public String toString()
    {
        return getLanguageCode() +
                (Strings.isNullOrEmpty(getLocaleCode()) ? "" : "_" + getLocaleCode()) +
                (Strings.isNullOrEmpty(getSubculture()) ? "" : "_" + getSubculture());
    }

	@Override
	public int hashCode()
	{
		if(providedLanguageCode != null) return 31 * providedLanguageCode.hashCode();
		else return 31;
	}

	@Override
	public boolean equals(Object obj)
	{
		if(obj == null) return false;
		if(! (obj instanceof LanguageCode)) return false;

		LanguageCode languageCode = (LanguageCode) obj;

		if(providedLanguageCode == null && languageCode.providedLanguageCode != null) return false;
		if(providedLanguageCode == null && languageCode.providedLanguageCode == null) return true;

		return languageCode.providedLanguageCode.equals(providedLanguageCode);
	}

	public String getLanguageCode()
    {
        String[] languageCodeParts = providedLanguageCode.split("_");

        //if the languageCode isn't split, then there was no underscore. just return the the filenameWithoutSuffix.  it's the code
        if(languageCodeParts.length == 1) return providedLanguageCode;

        //if the languageCode was split, then the first item is always the code
        else return languageCodeParts[0];

    }

    public String getLocaleCode()
    {
        String[] languageCodeParts = providedLanguageCode.split("_");

        //if the languageCode isn't split, then there was no underscore. there is no locale (e.g. en.xml)
        if(languageCodeParts.length == 1) return null;

        //if the length is greater than two, the locale is always the 2nd item, so return it.
        if(languageCodeParts.length > 2) return languageCodeParts[1];

        //we haven't returned yet, so there are two elements.  if the 2nd element is 2 or less characters it's a locale, if not, return null.. it's a subculture
        if(languageCodeParts[1].length() <= 2) return languageCodeParts[1];

        return null;
    }

    public String getSubculture()
    {
        String[] languageCodeParts = providedLanguageCode.split("_");

        //if the languageCode isn't split, then there was no underscore. there is no locale (e.g. en.xml)
        if(languageCodeParts.length == 1) return null;

        //if the length is greater than two, the locale is always the 3rd item, so return it.
        if(languageCodeParts.length > 2) return languageCodeParts[2];

        //we haven't returned yet, so there are two elements.  if the 2nd element is more than 2 characters it's a locale, if not, return null.. it's a subculture
        if(languageCodeParts[1].length() > 2) return languageCodeParts[1];

        return null;
    }
}
