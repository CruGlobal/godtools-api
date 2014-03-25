package org.cru.godtools.api.packages.exceptions;

/**
 * Created by ryancarlson on 3/25/14.
 */
public class LanguageNotFoundException extends Exception
{
    public final String languageCode;

    public LanguageNotFoundException(String languageCode)
    {
        super("Language " + languageCode + " was not found.");
        this.languageCode = languageCode;
    }

    public String getLanguageCode()
    {
        return languageCode;
    }
}
