package org.cru.godtools.api.packages.domain;

import org.sql2o.Connection;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

/**
 * Created by ryancarlson on 3/20/14.
 */
public class PackageService
{
    Connection sqlConnection;

    @Inject
    public PackageService(Connection sqlConnection, VersionService versionService)
    {
        this.sqlConnection = sqlConnection;
    }

    public List<Package> selectAll()
    {
        return sqlConnection.createQuery(PackageQueries.selectAll)
                .setAutoDeriveColumnNames(true)
                .executeAndFetch(Package.class);
    }

    public Package selectById(UUID id)
    {
        return sqlConnection.createQuery(PackageQueries.selectById)
                .setAutoDeriveColumnNames(true)
                .addParameter("id", id)
                .executeAndFetchFirst(Package.class);
    }

    public Package selectByCode(String code)
    {
        return sqlConnection.createQuery(PackageQueries.selectByCode)
                .setAutoDeriveColumnNames(true)
                .addParameter("code", code)
                .executeAndFetchFirst(Package.class);
    }

    public static class PackageQueries
    {
        public static final String selectAll = "SELECT * FROM packages";
        public static final String selectById = "SELECT * FROM packages WHERE id = :id";
        public static final String selectByCode = "SELECT * FROM packages WHERE code = :code";
    }
}
