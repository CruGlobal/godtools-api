package org.cru.godtools.domain.services;

import org.cru.godtools.domain.model.*;

import java.util.List;
import java.util.UUID;

/**
 * Created by ryancarlson on 4/30/14.
 */
public interface PackageStructureService
{
	void insert(PackageStructure packageStructure);

	PackageStructure selectByPackageId(UUID packageId);

	List<PackageStructure> selectAll();

	void setAutoCommit(boolean autoCommit);

	void rollback();
}
