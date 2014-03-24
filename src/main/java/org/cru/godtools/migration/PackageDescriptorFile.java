package org.cru.godtools.migration;

import java.io.File;

/**
 * This file encapsulates some logic for a "package descriptor file" (e.g: "Packages/kgp/en.xml")
 *
 * code_locale_subculture
 * code_subculture
 * code_locale
 * code
 */
public class PackageDescriptorFile
{
    File packageDescriptor;

    public PackageDescriptorFile(File packageDescriptor)
    {
        this.packageDescriptor = packageDescriptor;
    }

    public String getLanguageCode()
    {
        String filenameWithoutSuffix = getFilenameWithoutSuffix();
        String[] filenameParts = filenameWithoutSuffix.split("_");

        //if the filename isn't split, then there was no underscore. just return the the filenameWithoutSuffix.  it's the code
        if(filenameParts.length == 1) return filenameWithoutSuffix;

        //if the filename was split, then the first item is always the code
        else return filenameParts[0];

    }

    public String getLocaleCode()
    {
        String filenameWithoutSuffix = getFilenameWithoutSuffix();
        String[] filenameParts = filenameWithoutSuffix.split("_");

        //if the filename isn't split, then there was no underscore. there is no locale (e.g. en.xml)
        if(filenameParts.length == 1) return null;

        //if the length is greater than two, the locale is always the 2nd item, so return it.
        if(filenameParts.length > 2) return filenameParts[1];

        //we haven't returned yet, so there are two elements.  if the 2nd element is 2 or lesscharacters it's a locale, if not, return null.. it's a subculture
        if(filenameParts[1].length() <= 2) return filenameParts[1];

        return null;
    }

    public String getSubculture()
    {
        String filenameWithoutSuffix = getFilenameWithoutSuffix();
        String[] filenameParts = filenameWithoutSuffix.split("_");

        //if the filename isn't split, then there was no underscore. there is no locale (e.g. en.xml)
        if(filenameParts.length == 1) return null;

        //if the length is greater than two, the locale is always the 3rd item, so return it.
        if(filenameParts.length > 2) return filenameParts[2];

        //we haven't returned yet, so there are two elements.  if the 2nd element is more than 2 characters it's a locale, if not, return null.. it's a subculture
        if(filenameParts[1].length() > 2) return filenameParts[1];

        return null;
    }

    private String getFilenameWithoutSuffix()
    {
        return packageDescriptor.getName().substring(0, packageDescriptor.getName().length()-4);
    }
}
