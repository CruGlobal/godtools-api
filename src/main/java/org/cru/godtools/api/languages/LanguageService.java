package org.cru.godtools.api.languages;

import org.sql2o.Connection;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

/**
 * Created by ryancarlson on 3/20/14.
 */
public class LanguageService
{

    Connection sqlConnection;

    @Inject
    public LanguageService(Connection sqlConnection)
    {
        this.sqlConnection = sqlConnection;
    }

    public List<Language> selectAllLanguages()
    {
        return sqlConnection.createQuery(LanguageQueries.selectAll)
                .setAutoDeriveColumnNames(true)
                .executeAndFetch(Language.class);
    }

    public Language selectLanguageById(UUID id)
    {
        return sqlConnection.createQuery(LanguageQueries.selectById)
                .setAutoDeriveColumnNames(true)
                .addParameter("id", id)
                .executeAndFetchFirst(Language.class);
    }

    public Language selectLanguageByCode(String code)
    {
        return sqlConnection.createQuery(LanguageQueries.selectByCode)
                .setAutoDeriveColumnNames(true)
                .addParameter("code", code)
                .executeAndFetchFirst(Language.class);
    }

    public Language selectLanguageByName(String name)
    {
        return sqlConnection.createQuery(LanguageQueries.selectByName)
                .setAutoDeriveColumnNames(true)
                .addParameter("name", name)
                .executeAndFetchFirst(Language.class);
    }

    public static class LanguageQueries
    {
        public final static String selectAll = "SELECT * FROM languages";
        public final static String selectById = "SELECT * FROM languages WHERE id = :id";
        public final static String selectByCode = "SELECT * FROM languages WHERE code = :code";
        public final static String selectByName = "SELECT * FROM languages WHERE name = :name";
    }
}
