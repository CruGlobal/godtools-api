package org.cru.godtools.api.packages;

import org.cru.godtools.api.packages.exceptions.LanguageNotFoundException;
import org.cru.godtools.api.packages.exceptions.MissingVersionException;
import org.cru.godtools.api.packages.exceptions.NoTranslationException;
import org.cru.godtools.api.packages.exceptions.PackageNotFoundException;

import java.util.Set;

/**
 * Created by ryancarlson on 3/20/14.
 */
public interface IGodToolsPackageService
{
    GodToolsPackage getPackage(String languageCode, String packageCode) throws LanguageNotFoundException,
            PackageNotFoundException,
            NoTranslationException,
            MissingVersionException;

    GodToolsPackage getPackage(String languageCode, String packageCode, Integer revisionNumber) throws LanguageNotFoundException,
            PackageNotFoundException,
            NoTranslationException,
            MissingVersionException;

    Set<GodToolsPackage> getPackagesForLanguage(String languageCode) throws LanguageNotFoundException,
            PackageNotFoundException,
            NoTranslationException,
            MissingVersionException;

    public Set<GodToolsPackage> getPackagesForLanguage(String languageCode, Integer revisionNumber) throws LanguageNotFoundException,
            PackageNotFoundException,
            NoTranslationException,
            MissingVersionException;
}
