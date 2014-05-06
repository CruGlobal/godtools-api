package org.cru.godtools.api.translations.domain;

import org.cru.godtools.api.utilities.ResourceNotFoundException;
import org.sql2o.Connection;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

/**
 * Created by ryancarlson on 3/20/14.
 */
public class TranslationService
{

    Connection sqlConnection;

    @Inject
    public TranslationService(Connection sqlConnection)
    {
        this.sqlConnection = sqlConnection;
    }

	public Translation selectById(UUID id)
	{
		return sqlConnection.createQuery(TranslationQueries.selectById)
				.setAutoDeriveColumnNames(true)
				.addParameter("id", id)
				.executeAndFetchFirst(Translation.class);
	}

    public List<Translation> selectByLanguageId(UUID languageId)
    {
        return sqlConnection.createQuery(TranslationQueries.selectByLanguageId)
                .setAutoDeriveColumnNames(true)
                .addParameter("languageId", languageId)
                .executeAndFetch(Translation.class);
    }

    public List<Translation> selectByPackageId(UUID packageId)
    {
        return sqlConnection.createQuery(TranslationQueries.selectByPackageId)
                .setAutoDeriveColumnNames(true)
                .addParameter("packageId", packageId)
                .executeAndFetch(Translation.class);
    }

    public Translation selectByLanguageIdPackageId(UUID languageId, UUID packageId)
    {
        Translation translation = sqlConnection.createQuery(TranslationQueries.selectByLanguageIdPackageId)
                .setAutoDeriveColumnNames(true)
                .addParameter("packageId", packageId)
                .addParameter("languageId", languageId)
                .executeAndFetchFirst(Translation.class);

        if(translation == null) throw new ResourceNotFoundException(Translation.class);

        return translation;
    }

	public Translation selectByLanguageIdPackageIdVersionNumber(UUID languageId, UUID packageId, Integer versionNumber)
	{
		Translation translation = sqlConnection.createQuery(TranslationQueries.selectByLanguageIdPackageIdVersionNumber)
				.setAutoDeriveColumnNames(true)
				.addParameter("packageId", packageId)
				.addParameter("languageId", languageId)
				.addParameter("versionNumber", versionNumber)
				.executeAndFetchFirst(Translation.class);

		if(translation == null) throw new ResourceNotFoundException(Translation.class);

		return translation;
	}

    public void insert(Translation translation)
    {
        sqlConnection.createQuery(TranslationQueries.insert)
                .addParameter("id", translation.getId())
                .addParameter("packageId", translation.getPackageId())
                .addParameter("languageId", translation.getLanguageId())
				.addParameter("versionNumber", translation.getVersionNumber())
                .executeUpdate();
    }


    public static class TranslationQueries
    {
		public static final String selectById = "SELECT * FROM translations WHERE id = :id";
        public static final String selectByLanguageId = "SELECT * FROM translations WHERE language_id = :languageId";
        public static final String selectByPackageId = "SELECT * FROM translations WHERE package_id = :packageId";
        public static final String selectByLanguageIdPackageId = "SELECT * FROM translations WHERE package_id = :packageId AND language_id = :languageId";
		public static final String selectByLanguageIdPackageIdVersionNumber = "SELECT * FROM translations WHERE package_id = :packageId AND language_id = :languageId AND version_number = :versionNumber";
        public static final String insert = "INSERT INTO translations(id, language_id, package_id, version_number) VALUES(:id, :languageId, :packageId, :versionNumber)";
    }

}
