package org.cru.godtools.api.packages.domain;

import org.sql2o.Connection;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

/**
 * Created by ryancarlson on 3/20/14.
 */
public class PageService
{

    Connection sqlConnection;

    @Inject
    public PageService(Connection sqlConnection)
    {
        this.sqlConnection = sqlConnection;
    }

    public List<Page> selectByVersionId(UUID versionId)
    {
        return sqlConnection.createQuery(PageQueries.selectByVersionId)
                .setAutoDeriveColumnNames(true)
                .addParameter("versionId", versionId)
                .executeAndFetch(Page.class);
    }

    public Page selectById(UUID id)
    {
        return sqlConnection.createQuery(PageQueries.selectById)
                .setAutoDeriveColumnNames(true)
                .addParameter("id", id)
                .executeAndFetchFirst(Page.class);
    }

    public void insert(Page page)
    {
        sqlConnection.createQuery(PageQueries.insert)
                .addParameter("id", page.getId())
                .addParameter("versionId", page.getVersionId())
                .addParameter("filename", page.getFilename())
                .addParameter("ordinal", page.getOrdinal())
                .addParameter("xmlContent", page.getXmlContent())
                .addParameter("description", page.getDescription())
                .addParameter("filename", page.getFilename())
                .executeUpdate();
    }


    public static class PageQueries
    {
        public static final String selectById = "SELECT * FROM pages WHERE id = :id";
        public static final String selectByVersionId = "SELECT * FROM pages WHERE version_id = :versionId";
        public static final String insert = "INSERT INTO pages(id, version_id, filename, ordinal, xml_content, description) VALUES" +
                "(:id, :versionId, :filename, :ordinal, :xmlContent, :description)";
    }
}
