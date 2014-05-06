package org.cru.godtools.api.packages.domain;

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

	public List<PageStructure> selectByPackageStructureId(UUID packageStructureId)
	{
		return sqlConnection.createQuery(PageStructureQueries.selectByPackageStructureId)
				.setAutoDeriveColumnNames(true)
				.addParameter("packageStructureId", packageStructureId)
				.executeAndFetch(PageStructure.class);
	}

	public void insert(PageStructure pageStructure)
	{
		sqlConnection.createQuery(PageStructureQueries.insert)
				.addParameter("id", pageStructure.getId())
				.addParameter("packageStructureId", pageStructure.getPackageStructureId())
				.addParameter("xmlContent", pageStructure.getXmlContent())
				.addParameter("description", pageStructure.getDescription())
				.addParameter("filename", pageStructure.getFilename())
				.executeUpdate();
	}

	public static final class PageStructureQueries
	{
		public static final String selectByPackageStructureId = "SELECT * FROM page_structure WHERE package_structure_id = :packageStructureId";
		public static final String insert = "INSERT INTO page_structure(id, package_structure_id, xml_content, description, filename) VALUES(:id, :packageStructureId, :xmlContent, :description, :filename)";
	}
}
