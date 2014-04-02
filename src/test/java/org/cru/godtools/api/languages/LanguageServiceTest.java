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
//        SqlConnectionProducer connectionProducer = new SqlConnectionProducer();
//
//        sqlConnection = connectionProducer.getSqlConnection();
//        languageService = new LanguageService(sqlConnection);
    }

    @Test
    public void getAllLanguages()
    {

    }

    @Test
    public void getLanguageById()
    {

    }

    @Test
    public void getLanguageByCode()
    {


    }
}
