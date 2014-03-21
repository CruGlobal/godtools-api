package org.cru.godtools.api.languages;

import com.google.common.base.Strings;
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

    public List<Language> selectLanguageByCode(String code)
    {
        return sqlConnection.createQuery(LanguageQueries.selectByCode)
                .setAutoDeriveColumnNames(true)
                .addParameter("code", code)
                .executeAndFetch(Language.class);
    }

    public Language selectLanguageByCodeLocaleSubculture(String code, String locale, String subculture)
    {
        List<Language> possibleMatches = selectLanguageByCode(code);

        if(possibleMatches == null || possibleMatches.isEmpty()) return null;

        for(Language possibleMatch : possibleMatches)
        {
            boolean matched = true;

            if(!Strings.nullToEmpty(locale).equals(Strings.nullToEmpty(possibleMatch.getLocale()))) matched = false;
            if(!Strings.nullToEmpty(subculture).equals(Strings.nullToEmpty(possibleMatch.getSubculture()))) matched = false;

            if(matched) return possibleMatch;
        }

        return null;
    }

    /**
     * this method is required b/c databases stupidly don't equate null = null.
     * @param language
     * @return
     */
    public boolean languageExists(Language language)
    {
        List<Language> retrievedList = selectLanguageByCode(language.getCode());

        if(retrievedList == null || retrievedList.isEmpty()) return false;

        for(Language retrieved : retrievedList)
        {
            boolean matched = true;

            if(!Strings.nullToEmpty(language.getLocale()).equals(Strings.nullToEmpty(retrieved.getLocale()))) matched = false;
            if(!Strings.nullToEmpty(language.getSubculture()).equals(Strings.nullToEmpty(retrieved.getSubculture()))) matched = false;

            if(matched) return matched;
        }

        return false;
    }


    public Language selectLanguageByName(String name)
    {
        return sqlConnection.createQuery(LanguageQueries.selectByName)
                .setAutoDeriveColumnNames(true)
                .addParameter("name", name)
                .executeAndFetchFirst(Language.class);
    }

    public void insert(Language language)
    {
        sqlConnection.createQuery(LanguageQueries.insert)
                .addParameter("id", language.getId())
                .addParameter("code", language.getCode())
                .addParameter("name", language.getName())
                .addParameter("locale", language.getLocale())
                .addParameter("subculture", language.getSubculture())
                .executeUpdate();
    }

    public static class LanguageQueries
    {
        public final static String selectAll = "SELECT * FROM languages";
        public final static String selectById = "SELECT * FROM languages WHERE id = :id";
        public final static String selectByCode = "SELECT * FROM languages WHERE code = :code";
        public final static String selectByCodeLocaleSubculture = "SELECT * FROM languages WHERE code = :code AND locale = :locale AND subculture = :subculture";
        public final static String selectByName = "SELECT * FROM languages WHERE name = :name";
        public final static String insert = "INSERT INTO languages(id, name, code, locale, subculture) VALUES(:id, :name, :code, :locale, :subculture)";
    }
}
