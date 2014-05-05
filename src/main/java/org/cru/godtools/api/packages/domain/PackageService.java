package org.cru.godtools.api.packages.domain;

import org.cru.godtools.api.utilities.ResourceNotFoundException;
import org.sql2o.Connection;

import javax.inject.Inject;
import java.util.UUID;

/**
 * Created by ryancarlson on 3/20/14.
 */
public class PackageService
{
    Connection sqlConnection;

    @Inject
    public PackageService(Connection sqlConnection)
    {
        this.sqlConnection = sqlConnection;
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
        Package gtPackage = sqlConnection.createQuery(PackageQueries.selectByCode)
                .setAutoDeriveColumnNames(true)
                .addParameter("code", code)
                .executeAndFetchFirst(Package.class);

        if(gtPackage == null) throw new ResourceNotFoundException(Package.class);

        return gtPackage;
    }

    public void insert(Package godToolsPackage)
    {
        sqlConnection.createQuery(PackageQueries.insert)
                .addParameter("id", godToolsPackage.getId())
                .addParameter("code", godToolsPackage.getCode())
                .addParameter("name", godToolsPackage.getName())
                .addParameter("defaultLanguageId", godToolsPackage.getDefaultLanguageId())
				.addParameter("oneskyPackageId", godToolsPackage.getOneskyProjectId())
                .executeUpdate();
    }


    public static class PackageQueries
    {
        public static final String selectById = "SELECT * FROM packages WHERE id = :id";
        public static final String selectByCode = "SELECT * FROM packages WHERE code = :code";
        public static final String insert = "INSERT INTO packages(id, code, name, default_language_id, onesky_package_id) VALUES(:id, :code, :name, :defaultLanguageId, :oneskyPackageId)";
    }
}
