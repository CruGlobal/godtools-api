package org.cru.godtools.api.packages.domain;

import org.sql2o.Connection;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import java.util.Iterator;
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
        List<Version> versions = sqlConnection.createQuery(VersionQueries.selectByTranslationId)
                .setAutoDeriveColumnNames(true)
                .addParameter("translationId", translationId)
                .executeAndFetch(Version.class);

        if(versions == null || versions.isEmpty()) throw new NotFoundException();

        return versions;
    }

    public Version selectLatestVersionForTranslation(UUID translationId)
    {
        return selectLatestVersionForTranslation(translationId, -1);
    }

    public Version selectLatestVersionForTranslation(UUID translationId, Integer minimumInterpreterVersion)
    {
        List<Version> versions = selectByTranslationId(translationId);

        Iterator<Version> i = versions.iterator();

        for( ; i.hasNext();)
        {
            Version nextVersion = i.next();
            if(nextVersion.getMinimumInterpreterVersion().compareTo(minimumInterpreterVersion) < 0) i.remove();
        }

        if(versions.isEmpty()) throw new NotFoundException();

        Version max = versions.get(0);
        for(Version version : versions)
        {
            if(version.getVersionNumber().compareTo(max.getVersionNumber()) > 0
                    && version.getMinimumInterpreterVersion().compareTo(minimumInterpreterVersion) >= 0)
            {
                max = version;
            }
        }

        return max;
    }

    public Version selectSpecificVersionForTranslation(UUID translationId, Integer versionNumber)
    {
        return selectSpecificVersionForTranslation(translationId, versionNumber, -1);
    }

    public Version selectSpecificVersionForTranslation(UUID translationId, Integer versionNumber, Integer minimumInterpreterVersion)
    {
        Version version = sqlConnection.createQuery(VersionQueries.selectByTranslationIdVersionNumber)
                .setAutoDeriveColumnNames(true)
                .addParameter("translationId", translationId)
                .addParameter("versionNumber", versionNumber)
                .executeAndFetchFirst(Version.class);

        if(version == null || version.getMinimumInterpreterVersion().compareTo(minimumInterpreterVersion) < 0) throw new NotFoundException();

        return version;
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
                .addParameter("packageStructureHash", version.getPackageStructureHash())
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
                .addParameter("packageStructureHash", version.getPackageStructureHash())
                .executeUpdate();
    }

    public static class VersionQueries
    {
        public static final String selectByTranslationId = "SELECT * FROM versions WHERE translation_id = :translationId";
        public static String selectByTranslationIdVersionNumber = "SELECT * FROM versions WHERE translation_id = :translationId AND version_number = :versionNumber";
        public static final String insert = "INSERT INTO versions(id, version_number, released, package_id, translation_id, minimum_interpreter_version, package_structure, package_structure_hash) " +
                "VALUES(:id, :versionNumber, :released, :packageId, :translationId, :minimumInterpreterVersion, :packageStructure, :packageStructureHash)";
        public static final String selectAll = "SELECT * FROM versions";
        public static final String update = "UPDATE versions SET version_number = :versionNumber, released = :released, package_id = :packageId, " +
                "translation_id = :translationId, minimum_interpreter_version = :minimumInterpreterVersion, package_structure = :packageStructure, " +
                "package_structure_hash = :packageStructureHash WHERE id = :id";
    }
}
