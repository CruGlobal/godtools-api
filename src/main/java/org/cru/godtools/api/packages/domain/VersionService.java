package org.cru.godtools.api.packages.domain;

import org.sql2o.Connection;
import org.w3c.dom.Document;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

/**
 * Created by ryancarlson on 3/20/14.
 */
public class VersionService
{
    Connection sqlConnection;

    @Inject
    public VersionService(Connection sqlConnection)
    {
        this.sqlConnection = sqlConnection;
    }

    public List<Version> selectByTranslationId(UUID translationId)
    {
        return sqlConnection.createQuery(VersionQueries.selectByTranslationId)
                .setAutoDeriveColumnNames(true)
                .addParameter("translationId", translationId)
                .executeAndFetch(Version.class);
    }

    public Version selectLatestVersionForTranslation(UUID translationId)
    {
        List<Version> versions = selectByTranslationId(translationId);

        if(versions != null && !versions.isEmpty())
        {
            Version max = versions.get(0);
            for(Version version : versions)
            {
                if(version.getVersionNumber().compareTo(max.getVersionNumber()) > 0)
                {
                    max = version;
                }
            }

            return max;
        }

        return null;
    }

    public void insert(Version version)
    {
        sqlConnection.createQuery(VersionQueries.insert)
                .addParameter("id", version.getId())
                .addParameter("versionNumber", version.getVersionNumber())
                .addParameter("released", version.isReleased())
                .addParameter("packageId", version.getPackageId())
                .addParameter("translationId", version.getTranslationId())
                .addParameter("minimumInterpreterVersion", version.getMinimumInterpreterVersion())
                .addParameter("packageStructure", version.getPackageStructure())
                .executeUpdate();
    }

    public List<Version> selectAllVersions()
    {
        return sqlConnection.createQuery(VersionQueries.selectAll)
                .setAutoDeriveColumnNames(true)
                .executeAndFetch(Version.class);
    }

    public void update(Version version)
    {
        sqlConnection.createQuery(VersionQueries.update)
                .addParameter("id", version.getId())
                .addParameter("versionNumber", version.getVersionNumber())
                .addParameter("released", version.isReleased())
                .addParameter("packageId", version.getPackageId())
                .addParameter("translationId", version.getTranslationId())
                .addParameter("minimumInterpreterVersion", version.getMinimumInterpreterVersion())
                .addParameter("packageStructure", version.getPackageStructure())
                .executeUpdate();
    }

    public static class VersionQueries
    {
        public static final String selectByTranslationId = "SELECT * FROM versions WHERE translation_id = :translationId";
        public static final String insert = "INSERT INTO versions(id, version_number, released, package_id, translation_id, minimum_interpreter_version, package_structure) " +
                "VALUES(:id, :versionNumber, :released, :packageId, :translationId, :minimumInterpreterVersion, :packageStructure)";
        public static final String selectAll = "SELECT * FROM versions";
        public static final String update = "UPDATE versions SET version_number = :versionNumber, released = :released, package_id = :packageId, " +
                "translation_id = :translationId, minimum_interpreter_version = :minimumInterpreterVersion, package_structure = :packageStructure WHERE id = :id";
    }
}
