package org.cru.godtools.api.packages.service;

import org.sql2o.Connection;

import javax.inject.Inject;
import java.util.List;

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

    public Package selectById()
    {
        return sqlConnection.createQuery(PackageQueries.selectById)
                .setAutoDeriveColumnNames(true)
                .executeAndFetchFirst(Package.class);
    }

    public Package selectByCode()
    {
        return sqlConnection.createQuery(PackageQueries.selectByCode)
                .setAutoDeriveColumnNames(true)
                .executeAndFetchFirst(Package.class);
    }

    public static class PackageQueries
    {
        public static final String selectAll = "SELECT * FROM packages";
        public static final String selectById = "SELECT * FROM packages WHERE id = :id";
        public static final String selectByCode = "SELECT * FROM packages WHERE code = :code";
    }
}
