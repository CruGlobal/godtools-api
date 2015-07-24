package org.cru.godtools.domain.services.Sql2oStandard;

import org.cru.godtools.domain.packages.Package;
import org.cru.godtools.domain.services.*;
import org.sql2o.*;
import org.sql2o.Connection;

import javax.inject.*;
import java.sql.*;
import java.util.*;

/**
 * Created by justinsturm on 6/30/15.
 */
public class Sql2oPackageService implements PackageService
{
    Connection sqlConnection;

    @Inject
    public Sql2oPackageService(Connection sqlConnection)
    {
        this.sqlConnection = sqlConnection;
    }

    public org.cru.godtools.domain.packages.Package selectById(UUID id)
    {
        return sqlConnection.createQuery(PackageQueries.selectById)
                .setAutoDeriveColumnNames(true)
                .addParameter("id", id)
                .executeAndFetchFirst(org.cru.godtools.domain.packages.Package.class);
    }

    public Package selectByCode(String code)
    {
        return sqlConnection.createQuery(PackageQueries.selectByCode)
                .setAutoDeriveColumnNames(true)
                .addParameter("code", code)
                .executeAndFetchFirst(Package.class);
    }

    public List<Package> selectAllPackages()
    {
        return sqlConnection.createQuery(PackageQueries.selectAll)
                .setAutoDeriveColumnNames(true)
                .executeAndFetch(Package.class);
    }
    public Package selectByOneskyProjectId(Integer translationProjectId)
    {
        return sqlConnection.createQuery(PackageQueries.selectByTranslationProjectId)
                .setAutoDeriveColumnNames(true)
                .addParameter("translationProjectId", translationProjectId)
                .executeAndFetchFirst(Package.class);
    }

    public void insert(Package godToolsPackage)
    {
        sqlConnection.createQuery(PackageQueries.insert)
                .addParameter("id", godToolsPackage.getId())
                .addParameter("code", godToolsPackage.getCode())
                .addParameter("name", godToolsPackage.getName())
                .addParameter("defaultLanguageId", godToolsPackage.getDefaultLanguage() != null
                        ? godToolsPackage.getDefaultLanguage().getId() : null)
                .addParameter("translationProjectId", godToolsPackage.getTranslationProjectId())
                .executeUpdate();
    }

    public static class PackageQueries
    {
        public static final String selectAll = "SELECT * FROM packages";
        public static final String selectById = "SELECT * FROM packages WHERE id = :id";
        public static final String selectByCode = "SELECT * FROM packages WHERE code = :code";
        public static final String selectByTranslationProjectId = "SELECT * FROM packages WHERE translation_project_id = :translationProjectId";
        public static final String insert = "INSERT INTO packages(id, code, name, default_language_id, translation_project_id) VALUES(:id, :code, :name, :defaultLanguageId, :translationProjectId)";
    }

    public void setAutoCommit(boolean autoCommit)
    {
        try
        {
            sqlConnection.getJdbcConnection().setAutoCommit(autoCommit);
        }
        catch(SQLException e)
        {
            /*Do Nothing*/
        }
    }

    public void rollback()
    {
        try
        {
            sqlConnection.getJdbcConnection().rollback();
        }
        catch(SQLException e)
        {
            /*Do Nothing*/
        }
    }
}
