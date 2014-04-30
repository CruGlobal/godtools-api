package org.cru.godtools.api.packages.domain;

import org.sql2o.Connection;

import javax.inject.Inject;

/**
 * Created by ryancarlson on 4/30/14.
 */
public class PackageStructureService
{
	Connection sqlConnection;

	@Inject
	public PackageStructureService(Connection sqlConnection)
	{
		this.sqlConnection = sqlConnection;
	}

	public void insert(PackageStructure packageStructure)
	{
		sqlConnection.createQuery(PackageStructureQueries.insert)
				.addParameter("id", packageStructure.getId())
				.addParameter("packageId", packageStructure.getPackageId())
				.addParameter("xmlContent", packageStructure.getXmlContent())
				.addParameter("versionNumber", packageStructure.getVersion_number())
				.executeUpdate();
	}

	public static class PackageStructureQueries
	{
		public static final String insert = "INSERT INTO package_structure(id, package_id, xml_content, version_number) VALUES(:id, :packageId, :xmlContent, :versionNumber)";
	}
}
