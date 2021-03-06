package org.cru.godtools.domain.languages;

import com.google.common.base.Strings;

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
        if(!Strings.isNullOrEmpty(language.getLocale())) constructedLanguageCode += "-" + language.getLocale();
        if(!Strings.isNullOrEmpty(language.getSubculture())) constructedLanguageCode += "-" + language.getSubculture();
        return new LanguageCode(constructedLanguageCode);
    }

    @Override
    public String toString()
    {
        return getLanguageCode() +
                (Strings.isNullOrEmpty(getLocaleCode()) ? "" : "-" + getLocaleCode()) +
                (Strings.isNullOrEmpty(getSubculture()) ? "" : "-" + getSubculture());
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
        String[] languageCodeParts = providedLanguageCode.split("-");

        //if the languageCode isn't split, then there was no underscore. just return the the filenameWithoutSuffix.  it's the code
        if(languageCodeParts.length == 1) return providedLanguageCode;

        //if the languageCode was split, then the first item is always the code
        else return languageCodeParts[0];

    }

    public String getLocaleCode()
    {
        String[] languageCodeParts = providedLanguageCode.split("-");

        //if the languageCode isn't split, then there was no underscore. there is no locale (e.g. en.xml)
        if(languageCodeParts.length <= 1) return null;

        //if the length is greater than two, the locale is always the 2nd item, so return it.
        return languageCodeParts[1];
    }

    public String getSubculture()
    {
        String[] languageCodeParts = providedLanguageCode.split("-");

        //if the languageCode isn't split, then there was no underscore. there is no locale (e.g. en.xml)
        if(languageCodeParts.length <= 2) return null;

        return languageCodeParts[2];
    }
}
