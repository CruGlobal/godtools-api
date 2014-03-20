package org.cru.godtools.api.languages;

import org.cru.godtools.api.database.SqlConnectionProducer;
import org.sql2o.Connection;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

/**
 * Created by ryancarlson on 3/20/14.
 */
public class LanguageServiceTest
{

    Connection sqlConnection;
    LanguageService languageService;

    @BeforeMethod
    public void setup()
    {
        SqlConnectionProducer connectionProducer = new SqlConnectionProducer();

        sqlConnection = connectionProducer.getTestSqlConnection();
        languageService = new LanguageService(sqlConnection);
    }

    @Test
    public void getAllLanguages()
    {
        try
        {
            List<Language> languageList = languageService.getAllLanguages();

            Assert.assertEquals(languageList.size(), 2);
        }
        finally
        {
            sqlConnection.rollback();
        }
    }

    @Test
    public void getLanguageById()
    {
        try
        {
            Language english = languageService.getLanguageById(UUID.fromString("5d469b6c-df1d-417a-a320-64039c2a898a"));

            Assert.assertNotNull(english);
            Assert.assertEquals(english.getCode(), "en");
            Assert.assertEquals(english.getName(), "English");
        }
        finally
        {
            sqlConnection.rollback();
        }

    }

    @Test
    public void getLanguageByCode()
    {
        try
        {
            Language french = languageService.getLanguageByCode("fr");

            Assert.assertNotNull(french);
            Assert.assertEquals(french.getId(), UUID.fromString("f0518a70-76c9-4bee-8c19-389ffc1d9742"));
            Assert.assertEquals(french.getName(), "French");
        }
        finally
        {
            sqlConnection.rollback();
        }

    }
}
