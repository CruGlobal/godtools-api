package org.cru.godtools.api.packages.domain;

import org.sql2o.Connection;

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

    public static class VersionQueries
    {
        public static final String selectByTranslationId = "SELECT * FROM versions WHERE translation_id = :translationId";
    }
}
