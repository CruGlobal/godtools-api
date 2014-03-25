package org.cru.godtools.api.packages;

import org.cru.godtools.api.packages.exceptions.LanguageNotFoundException;
import org.cru.godtools.api.packages.exceptions.MissingVersionException;
import org.cru.godtools.api.packages.exceptions.NoTranslationException;
import org.cru.godtools.api.packages.exceptions.PackageNotFoundException;
import org.cru.godtools.api.packages.utils.LanguageCode;

import java.util.Set;

/**
 * Created by ryancarlson on 3/20/14.
 */
public interface IGodToolsPackageService
{
//    GodToolsPackage getPackage(LanguageCode languageCode, String packageCode) throws LanguageNotFoundException,
//            PackageNotFoundException,
//            NoTranslationException,
//            MissingVersionException;

    GodToolsPackage getPackage(LanguageCode languageCode, String packageCode, Integer revisionNumber, Integer minimumInterpreterVersion) throws LanguageNotFoundException,
            PackageNotFoundException,
            NoTranslationException,
            MissingVersionException;

//    Set<GodToolsPackage> getPackagesForLanguage(LanguageCode languageCode) throws LanguageNotFoundException,
//            PackageNotFoundException,
//            NoTranslationException,
//            MissingVersionException;

    Set<GodToolsPackage> getPackagesForLanguage(LanguageCode languageCode, Integer revisionNumber, Integer minimumInterpreterVersion) throws LanguageNotFoundException,
            PackageNotFoundException,
            NoTranslationException,
            MissingVersionException;
}
