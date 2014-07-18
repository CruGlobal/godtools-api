package org.cru.godtools.migration;

import org.cru.godtools.domain.packages.Package;
import com.google.common.collect.Sets;

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
		fourlaws.setTranslationProjectId(26580);

		Package kgp = new Package();
		kgp.setCode("kgp");
		kgp.setTranslationProjectId(26582);

		Package satisfied = new Package();
		satisfied.setCode("satisfied");
		satisfied.setTranslationProjectId(26581);

		packages.add(fourlaws);
		packages.add(kgp);
		packages.add(satisfied);
    }
}
