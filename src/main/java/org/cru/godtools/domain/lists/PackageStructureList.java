package org.cru.godtools.domain.lists;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ForwardingList;
import org.cru.godtools.domain.model.*;

import java.util.List;
import java.util.UUID;

/**
 * Created by ryancarlson on 4/28/15.
 */
public class PackageStructureList extends ForwardingList<PackageStructure>
{
	final List<PackageStructure> packageStructureList;

	public PackageStructureList(List<PackageStructure> packageStructureList)
	{
		this.packageStructureList = packageStructureList;
	}

	public Optional<PackageStructure> getByPackageId(final UUID packageId)
	{
		return FluentIterable.from(packageStructureList).firstMatch(new Predicate<PackageStructure>()
		{
			public boolean apply(PackageStructure input)
			{
				return packageId.equals(input.getPackage() != null ? input.getPackage().getId() : null);
			}
		});
	}

	@Override
	protected List<PackageStructure> delegate()
	{
		return packageStructureList;
	}
}
