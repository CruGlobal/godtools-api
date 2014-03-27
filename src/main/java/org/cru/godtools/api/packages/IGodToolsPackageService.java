package org.cru.godtools.api.packages;

import org.cru.godtools.api.packages.utils.LanguageCode;

import java.util.Set;

/**
 * Created by ryancarlson on 3/20/14.
 */
public interface IGodToolsPackageService
{
    GodToolsPackage getPackage(LanguageCode languageCode, String packageCode, Integer revisionNumber, Integer minimumInterpreterVersion);
    Set<GodToolsPackage> getPackagesForLanguage(LanguageCode languageCode, Integer revisionNumber, Integer minimumInterpreterVersion);
}
