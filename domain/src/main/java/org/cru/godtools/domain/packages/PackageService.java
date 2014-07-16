package org.cru.godtools.domain.packages;

import org.sql2o.Connection;

import javax.inject.Inject;
import java.util.UUID;

/**
 * Created by ryancarlson on 3/20/14.
 */
public class PackageService
{
    Connection sqlConnection;

    @Inject
    public PackageService(Connection sqlConnection)
    {
        this.sqlConnection = sqlConnection;
    }

    public Package selectById(UUID id)
    {
        return sqlConnection.createQuery(PackageQueries.selectById)
                .setAutoDeriveColumnNames(true)
                .addParameter("id", id)
                .executeAndFetchFirst(Package.class);
    }

    public Package selectByCode(String code)
    {
        return sqlConnection.createQuery(PackageQueries.selectByCode)
                .setAutoDeriveColumnNames(true)
                .addParameter("code", code)
                .executeAndFetchFirst(Package.class);
    }

	public Package selectByOneskyProjectId(Integer oneskyProjectId)
	{
		return sqlConnection.createQuery(PackageQueries.selectByOneskyProjectId)
				.setAutoDeriveColumnNames(true)
				.addParameter("oneskyProjectId", oneskyProjectId)
				.executeAndFetchFirst(Package.class);
	}

	public void insert(Package godToolsPackage)
    {
        sqlConnection.createQuery(PackageQueries.insert)
                .addParameter("id", godToolsPackage.getId())
                .addParameter("code", godToolsPackage.getCode())
                .addParameter("name", godToolsPackage.getName())
                .addParameter("defaultLanguageId", godToolsPackage.getDefaultLanguageId())
				.addParameter("oneskyProjectId", godToolsPackage.getOneskyProjectId())
                .executeUpdate();
    }

	public static class PackageQueries
    {
        public static final String selectById = "SELECT * FROM packages WHERE id = :id";
        public static final String selectByCode = "SELECT * FROM packages WHERE code = :code";
		public static final String selectByOneskyProjectId = "SELECT * FROM packages WHERE onesky_project_id = :oneskyProjectId";
        public static final String insert = "INSERT INTO packages(id, code, name, default_language_id, onesky_project_id) VALUES(:id, :code, :name, :defaultLanguageId, :oneskyProjectId)";
	}
}