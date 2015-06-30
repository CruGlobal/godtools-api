package org.cru.godtools.api.services;

import org.cru.godtools.domain.packages.*;
import org.sql2o.Connection;

import javax.inject.Inject;
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

	Connection getSqlConnection();
}
