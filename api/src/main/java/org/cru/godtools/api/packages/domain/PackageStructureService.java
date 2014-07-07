package org.cru.godtools.api.packages.domain;

import org.postgresql.util.PSQLDriverVersion;
import org.sql2o.Connection;

import javax.inject.Inject;
import java.util.UUID;

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
				.addParameter("versionNumber", packageStructure.getVersionNumber())
				.executeUpdate();
	}

	public PackageStructure selectByPackageId(UUID packageId)
	{
		return sqlConnection.createQuery(PackageStructureQueries.selectById)
				.setAutoDeriveColumnNames(true)
				.addParameter("packageId", packageId)
				.executeAndFetchFirst(PackageStructure.class);
	}

	public static class PackageStructureQueries
	{
		public static final String insert = "INSERT INTO package_structure(id, package_id, xml_content, version_number) VALUES(:id, :packageId, :xmlContent, :versionNumber)";
		public static final String selectById = "SELECT * FROM package_structure WHERE package_id = :packageId";

	}
}
