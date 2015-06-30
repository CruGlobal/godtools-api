package org.cru.godtools.api.services;

import org.cru.godtools.domain.packages.Package;
import org.sql2o.Connection;
import java.util.List;
import java.util.UUID;

/**
 * Created by ryancarlson on 3/20/14.
 */
public interface PackageService
{
    Package selectById(UUID id);
    Package selectByCode(String code);
    List<Package> selectAllPackages();
	Package selectByOneskyProjectId(Integer translationProjectId);
	void insert(Package godToolsPackage);
    Connection getSqlConnection();
}
