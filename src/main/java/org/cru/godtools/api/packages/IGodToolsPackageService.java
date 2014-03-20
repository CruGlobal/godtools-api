package org.cru.godtools.api.packages;

import java.util.Set;

/**
 * Created by ryancarlson on 3/20/14.
 */
public interface IGodToolsPackageService
{
    public GodToolsPackage getPackage(String languageCode, String packageCode);
    public Set<GodToolsPackage> getPackagesForLanguage(String languageCode);
}
