package org.cru.godtools.domain.services.Sql2oStandard;

import org.cru.godtools.domain.packages.*;
import org.cru.godtools.domain.services.*;
import org.sql2o.*;
import org.sql2o.Connection;

import javax.inject.*;
import java.sql.*;
import java.util.*;

/**
 * Created by justinsturm on 6/30/15.
 */
public class Sql2oTranslationElementService implements TranslationElementService
{
    @Inject
    private Connection sqlConnection;

    public Sql2oTranslationElementService(){}

    public Sql2oTranslationElementService(Connection sqlConnection)
    {
        this.sqlConnection = sqlConnection;
    }

    public List<TranslationElement> selectByTranslationId(UUID translationId, String ... orderByFields)
    {
        StringBuilder orderByBuilder = null;

        if(orderByFields != null && orderByFields.length > 0)
        {
            orderByBuilder = new StringBuilder(" ORDER BY ");
            boolean isFirst = true;
            for(String orderByField : orderByFields)
            {
                if(!isFirst) orderByBuilder.append(", ");
                orderByBuilder.append(orderByField);
                isFirst = false;
            }

        }
        return sqlConnection.createQuery(TranslationElementQueries.selectByTranslationId + (orderByBuilder != null ? orderByBuilder.toString() : ""))
                .setAutoDeriveColumnNames(true)
                .addParameter("translationId", translationId)
                .executeAndFetch(TranslationElement.class);
    }

    public List<TranslationElement> selectByTranslationIdPageStructureId(UUID translationId, UUID pageStructureId)
    {
        return sqlConnection.createQuery(TranslationElementQueries.selectByTranslationIdPageStructureId)
                .setAutoDeriveColumnNames(true)
                .addParameter("translationId", translationId)
                .addParameter("pageStructureId", pageStructureId)
                .executeAndFetch(TranslationElement.class);
    }

    public TranslationElement selectyByIdTranslationId(UUID id, UUID translationId)
    {
        return sqlConnection.createQuery(TranslationElementQueries.selectByIdTranslationId)
                .setAutoDeriveColumnNames(true)
                .addParameter("translationId", translationId)
                .addParameter("id", id)
                .executeAndFetchFirst(TranslationElement.class);
    }

    public void insert(TranslationElement translationElement)
    {
        sqlConnection.createQuery(TranslationElementQueries.insert)
                .addParameter("id", translationElement.getId())
                .addParameter("translationId", translationElement.getTranslationId())
                .addParameter("pageStructureId", translationElement.getPageStructureId())
                .addParameter("baseText", translationElement.getBaseText())
                .addParameter("translatedText", translationElement.getTranslatedText())
                .addParameter("elementType", translationElement.getElementType())
                .addParameter("pageName", translationElement.getPageName())
                .addParameter("displayOrder", translationElement.getDisplayOrder())
                .executeUpdate();
    }

    public void update(TranslationElement translationElement)
    {
        sqlConnection.createQuery(TranslationElementQueries.update)
                .addParameter("id", translationElement.getId())
                .addParameter("translationId", translationElement.getTranslationId())
                .addParameter("pageStructureId", translationElement.getPageStructureId())
                .addParameter("baseText", translationElement.getBaseText())
                .addParameter("translatedText", translationElement.getTranslatedText())
                .addParameter("elementType", translationElement.getElementType())
                .addParameter("pageName", translationElement.getPageName())
                .addParameter("displayOrder", translationElement.getDisplayOrder())
                .executeUpdate();
    }

    public static class TranslationElementQueries
    {
        public static final String selectByTranslationId = "SELECT * FROM translation_elements WHERE translation_id = :translationId";
        public static final String selectByIdTranslationId = "SELECT * FROM translation_elements WHERE id = :id AND translation_id = :translationId";
        public static final String selectByTranslationIdPageStructureId = "SELECT * from translation_elements WHERE translation_id = :translationId AND page_structure_id = :pageStructureId";
        public static final String insert = "INSERT INTO translation_elements(id, translation_id, page_structure_id, base_text, translated_text, element_type, page_name, display_order) " +
                "VALUES(:id, :translationId, :pageStructureId, :baseText, :translatedText, :elementType, :pageName, :displayOrder)";
        public static final String update = "UPDATE translation_elements SET base_text = :baseText, translated_text = :translatedText, " +
                "element_type = :elementType, page_name = :pageName, display_order = :displayOrder, page_structure_id = :pageStructureId WHERE id = :id AND translation_id = :translationId";
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
