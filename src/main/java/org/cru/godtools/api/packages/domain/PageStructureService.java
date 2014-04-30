package org.cru.godtools.api.packages.domain;

import org.sql2o.Connection;

import javax.inject.Inject;

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

	public void insert(PageStructure pageStructure)
	{
		sqlConnection.createQuery(PageStructureQueries.insert)
				.addParameter("id", pageStructure.getId())
				.addParameter("packageStructureId", pageStructure.getPackageStructureId())
				.addParameter("xmlContent", pageStructure.getXmlContent())
				.addParameter("description", pageStructure.getDescription())
				.executeUpdate();
	}

	public static final class PageStructureQueries
	{
		public static final String insert = "INSERT INTO page_structure(id, package_structure_id, xml_content, description) VALUES(:id, :packageStructureId, :xmlContent, :description)";
	}
}
