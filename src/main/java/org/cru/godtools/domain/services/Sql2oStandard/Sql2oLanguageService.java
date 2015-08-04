package org.cru.godtools.domain.services.Sql2oStandard;

import com.google.common.base.*;
import org.cru.godtools.domain.languages.*;
import org.cru.godtools.domain.model.*;
import org.cru.godtools.domain.services.*;
import org.sql2o.Connection;

import javax.inject.*;
import java.sql.*;
import java.util.*;

/**
 * Created by justinsturm on 6/30/15.
 */
public class Sql2oLanguageService implements LanguageService
{
    private Connection sqlConnection;

    @Inject
    public Sql2oLanguageService(Connection sqlConnection)
    {
        this.sqlConnection = sqlConnection;
    }


    public Language getOrCreateLanguage(LanguageCode languageCode)
    {

        Optional<Language> languageOptional = Optional.fromNullable(selectByLanguageCode(languageCode));

        if(languageOptional.isPresent()) return languageOptional.get();

        Language newLanguage = new Language();
        newLanguage.setId(UUID.randomUUID());
        //TODO: name is missing
        newLanguage.setFromLanguageCode(languageCode);
        insert(newLanguage);

        return newLanguage;
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

    public Language selectByLanguageCode(LanguageCode languageCode)
    {
        List<Language> possibleMatches = selectLanguageByStringCode(languageCode.getLanguageCode());

        for(Language possibleMatch : possibleMatches)
        {
            boolean matched = true;

            if(!Strings.nullToEmpty(languageCode.getLocaleCode()).equals(Strings.nullToEmpty(possibleMatch.getLocale()))) matched = false;
            if(!Strings.nullToEmpty(languageCode.getSubculture()).equals(Strings.nullToEmpty(possibleMatch.getSubculture()))) matched = false;

            if(matched) return possibleMatch;
        }
        return null;
    }

    private List<Language> selectLanguageByStringCode(String code)
    {
        return sqlConnection.createQuery(LanguageQueries.selectByCode)
                .setAutoDeriveColumnNames(true)
                .addParameter("code", code)
                .executeAndFetch(Language.class);
    }

    /**
     * this method is required b/c databases stupidly don't equate null = null.
     * @param language
     * @return
     */
    public boolean languageExists(Language language)
    {
        List<Language> retrievedList = selectLanguageByStringCode(language.getCode());

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
        public final static String selectByCode = "SELECT * FROM languages WHERE code = :code";        public final static String insert = "INSERT INTO languages(id, name, code, locale, subculture) VALUES(:id, :name, :code, :locale, :subculture)";
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
