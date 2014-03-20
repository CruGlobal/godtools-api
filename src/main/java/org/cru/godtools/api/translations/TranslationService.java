package org.cru.godtools.api.translations;

import org.sql2o.Connection;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

/**
 * Created by ryancarlson on 3/20/14.
 */
public class TranslationService
{

    Connection sqlConnection;

    @Inject
    public TranslationService(Connection sqlConnection)
    {
        this.sqlConnection = sqlConnection;
    }

    public Translation selectById(UUID id)
    {
        return sqlConnection.createQuery(TranslationQueries.selectById)
                .setAutoDeriveColumnNames(true)
                .addParameter("id", id)
                .executeAndFetchFirst(Translation.class);
    }

    public List<Translation> selectByLanguageId(UUID languageId)
    {
        return sqlConnection.createQuery(TranslationQueries.selectByLanguageId)
                .setAutoDeriveColumnNames(true)
                .addParameter("languageId", languageId)
                .executeAndFetch(Translation.class);
    }

    public List<Translation> selectByPackageId(UUID packageId)
    {
        return sqlConnection.createQuery(TranslationQueries.selectByPackageId)
                .setAutoDeriveColumnNames(true)
                .addParameter("packageId", packageId)
                .executeAndFetch(Translation.class);
    }

    public void insert(Translation translation)
    {
        sqlConnection.createQuery(TranslationQueries.insert)
                .addParameter("id", translation.getId())
                .addParameter("packageId", translation.getPackageId())
                .addParameter("languageId", translation.getLanguageId())
                .executeUpdate();
    }

    public void update(Translation translation)
    {
        sqlConnection.createQuery(TranslationQueries.update)
                .addParameter("id", translation.getId())
                .addParameter("packageId", translation.getPackageId())
                .addParameter("languageId", translation.getLanguageId())
                .executeUpdate();
    }

    public static class TranslationQueries
    {
        public static final String selectById = "SELECT * FROM translations WHERE id = :id";
        public static final String selectByLanguageId = "SELECT * FROM translations WHERE language_id = :languageId";
        public static final String selectByPackageId = "SELECT * FROM translations WHERE package_id = :packageId";
        public static final String insert = "INSERT INTO translations(id, language_id, package_id) VALUES(:id, :languageId, :packageId)";
        public static final String update = "UPDATE translations SET language_id = :languageId, package_id = :packageId WHERE id = :id";
    }

}
