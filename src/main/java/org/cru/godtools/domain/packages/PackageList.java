package org.cru.godtools.domain.packages;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ForwardingList;

import java.util.List;
import java.util.UUID;

/**
 * Created by ryancarlson on 4/28/15.
 */
public class PackageList extends ForwardingList<Package>
{
	final List<Package> packageList;

	public PackageList(List<Package> packageList)
	{
		this.packageList = packageList;
	}

	public Optional<Package> getPackageByCode(final String packageCode)
	{
		return FluentIterable.from(packageList).firstMatch(new Predicate<Package>()
		{
			public boolean apply(Package input)
			{
				return packageCode.equals(input.getCode());
			}
		});
	}

	public Optional<Package> getPackageById(final UUID packageId)
	{
		return FluentIterable.from(packageList).firstMatch(new Predicate<Package>()
		{
			public boolean apply(Package input)
			{
				return packageId.equals(input.getId());
			}
		});
	}

	@Override
	protected List<Package> delegate()
	{
		return packageList;
	}
}
