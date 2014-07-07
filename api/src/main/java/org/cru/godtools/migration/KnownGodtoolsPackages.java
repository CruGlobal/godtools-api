package org.cru.godtools.migration;

import com.beust.jcommander.internal.Sets;
import org.cru.godtools.api.packages.domain.*;
import org.cru.godtools.api.packages.domain.Package;

import java.util.Set;

/**
 * Created by ryancarlson on 3/20/14.
 */
public class KnownGodtoolsPackages
{
    public static final Set<Package> packages = Sets.newHashSet();

    static
    {
		Package fourlaws = new Package();
		fourlaws.setCode("fourlaws");
		fourlaws.setOneskyProjectId(26580);

		Package kgp = new Package();
		kgp.setCode("kgp");
		kgp.setOneskyProjectId(26582);

		Package satisfied = new Package();
		satisfied.setCode("satisfied");
		satisfied.setOneskyProjectId(26581);

		packages.add(fourlaws);
		packages.add(kgp);
		packages.add(satisfied);
    }
}
