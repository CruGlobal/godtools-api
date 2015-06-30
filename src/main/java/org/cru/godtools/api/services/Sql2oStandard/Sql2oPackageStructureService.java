package org.cru.godtools.api.services.Sql2oStandard;

import org.cru.godtools.api.services.*;
import org.cru.godtools.domain.packages.*;
import org.sql2o.*;

import javax.inject.*;
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

    public Connection getSqlConnection()
    {
        return sqlConnection;
    }
}
