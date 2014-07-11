package org.cru.godtools.domain.packages;

import com.google.common.collect.Lists;
import org.sql2o.Connection;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

/**
 * Created by ryancarlson on 4/30/14.
 */
public class PageStructureService
{
	Connection sqlConnection;

	@Inject
	public PageStructureService(Connection sqlConnection)
	{
		this.sqlConnection = sqlConnection;
	}

	public PageStructure selectByid(UUID id)
	{
		return sqlConnection.createQuery(PageStructureQueries.selectById)
				.setAutoDeriveColumnNames(true)
				.addParameter("id", id)
				.executeAndFetchFirst(PageStructure.class);

	}

	public List<PageStructure> selectByPackageStructureId(UUID packageStructureId)
	{
		return Lists.newArrayList();
	}

	public void insert(PageStructure pageStructure)
	{
		sqlConnection.createQuery(PageStructureQueries.insert)
				.addParameter("id", pageStructure.getId())
				.addParameter("translationId", pageStructure.getTranslationId())
				.addParameter("xmlContent", pageStructure.getXmlContent())
				.addParameter("description", pageStructure.getDescription())
				.addParameter("filename", pageStructure.getFilename())
				.executeUpdate();
	}

	public void update(PageStructure pageStructure)
	{
		sqlConnection.createQuery(PageStructureQueries.update)
				.addParameter("id", pageStructure.getId())
				.addParameter("translationId", pageStructure.getTranslationId())
				.addParameter("xmlContent", pageStructure.getXmlContent())
				.addParameter("description", pageStructure.getDescription())
				.addParameter("filename", pageStructure.getFilename())
				.executeUpdate();
	}

	public static final class PageStructureQueries
	{
		public static final String selectById = "SELECT * FROM page_structure WHERE id = :id";
		public static final String insert = "INSERT INTO page_structure(id, xml_content, translation_id, description, filename) VALUES(:id, :xmlContent, :translationId, :description, :filename)";
		public static final String update = "UPDATE page_structure SET xml_content = :xmlContent, translation_id = :translationId, description = :description, filename = :filename WHERE id = :id";
	}
}
