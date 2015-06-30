package org.cru.godtools.domain.services.Sql2oStandard;

import org.cru.godtools.domain.packages.*;
import org.cru.godtools.domain.services.*;
import org.sql2o.*;
import org.sql2o.Connection;

import javax.inject.*;
import java.sql.*;
import java.util.*;

/**
 * Created by justinsturm on 6/30/15.
 */
public class Sql2oPackageStructureService implements PackageStructureService
{
    private Connection sqlConnection;

    @Inject
    public Sql2oPackageStructureService(Connection sqlConnection)
    {
        this.sqlConnection = sqlConnection;
    }

    public void insert(PackageStructure packageStructure)
    {
        sqlConnection.createQuery(PackageStructureQueries.insert)
                .addParameter("id", packageStructure.getId())
                .addParameter("packageId", packageStructure.getPackageId())
                .addParameter("xmlContent", packageStructure.getXmlContent())
                .addParameter("versionNumber", packageStructure.getVersionNumber())
                .executeUpdate();
    }

    public PackageStructure selectByPackageId(UUID packageId)
    {
        return sqlConnection.createQuery(PackageStructureQueries.selectById)
                .setAutoDeriveColumnNames(true)
                .addParameter("packageId", packageId)
                .executeAndFetchFirst(PackageStructure.class);
    }

    public List<PackageStructure> selectAll()
    {
        return sqlConnection.createQuery(PackageStructureQueries.selectAll)
                .setAutoDeriveColumnNames(true)
                .executeAndFetch(PackageStructure.class);
    }
    public static class PackageStructureQueries
    {
        public static final String insert = "INSERT INTO package_structure(id, package_id, xml_content, version_number) VALUES(:id, :packageId, :xmlContent, :versionNumber)";
        public static final String selectById = "SELECT * FROM package_structure WHERE package_id = :packageId";
        public static final String selectAll = "SELECT * FROM package_structure";
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
