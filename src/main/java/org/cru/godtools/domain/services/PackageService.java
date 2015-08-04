package org.cru.godtools.domain.services;

import org.cru.godtools.domain.model.Package;

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

    void setAutoCommit(boolean autoCommit);

    void rollback();
}
