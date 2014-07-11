package org.cru.godtools.migration;


import org.cru.godtools.domain.languages.LanguageCode;

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
        return new LanguageCode(getFilenameWithoutSuffix()).getLanguageCode();
    }

    public String getLocaleCode()
    {
        return new LanguageCode(getFilenameWithoutSuffix()).getLocaleCode();
    }

    public String getSubculture()
    {
        return new LanguageCode(getFilenameWithoutSuffix()).getSubculture();
    }

    private String getFilenameWithoutSuffix()
    {
        return packageDescriptor.getName().substring(0, packageDescriptor.getName().length()-4);
    }
}
