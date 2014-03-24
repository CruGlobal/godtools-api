package org.cru.godtools.migration;

import com.beust.jcommander.internal.Sets;

import java.util.Set;

/**
 * Created by ryancarlson on 3/20/14.
 */
public class KnownGodtoolsPackages
{
    public static final Set<String> packageNames = Sets.newHashSet();

    static
    {
        packageNames.add("cwg");
        packageNames.add("fourlaws");
        packageNames.add("kgp");
        packageNames.add("satisfied");
    }
}
