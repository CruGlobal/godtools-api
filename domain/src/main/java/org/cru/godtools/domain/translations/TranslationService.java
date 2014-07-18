package org.cru.godtools.domain.translations;

import org.cru.godtools.domain.GodToolsVersion;
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

    public List<Translation> selectByLanguageIdPackageId(UUID languageId, UUID packageId)
    {
        return sqlConnection.createQuery(TranslationQueries.selectByLanguageIdPackageId)
                .setAutoDeriveColumnNames(true)
                .addParameter("packageId", packageId)
                .addParameter("languageId", languageId)
                .executeAndFetch(Translation.class);
    }

	public Translation selectByLanguageIdPackageIdVersionNumber(UUID languageId, UUID packageId, GodToolsVersion godToolsVersion)
	{
		if(godToolsVersion == GodToolsVersion.LATEST_VERSION)
		{
			Translation highestFoundVersionTranslation = null;

			for(Translation translation : selectByLanguageIdPackageId(languageId, packageId))
			{
				if(highestFoundVersionTranslation == null || translation.getVersionNumber().compareTo(highestFoundVersionTranslation.getVersionNumber()) > 0)
				{
					highestFoundVersionTranslation = translation;
				}
			}

			return highestFoundVersionTranslation;
		}
		else if(godToolsVersion == GodToolsVersion.LATEST_PUBLISHED_VERSION)
		{
			Translation highestFoundVersionTranslation = null;

			for(Translation translation : selectByLanguageIdPackageId(languageId, packageId))
			{
				if(translation.isReleased() && (highestFoundVersionTranslation == null || translation.getVersionNumber().compareTo(highestFoundVersionTranslation.getVersionNumber()) > 0))
				{
					highestFoundVersionTranslation = translation;
				}
			}

			return highestFoundVersionTranslation;
		}
		Translation translation = sqlConnection.createQuery(TranslationQueries.selectByLanguageIdPackageIdVersionNumber)
				.setAutoDeriveColumnNames(true)
				.addParameter("packageId", packageId)
				.addParameter("languageId", languageId)
				.addParameter("versionNumber", godToolsVersion.getTranslationVersion())
				.executeAndFetchFirst(Translation.class);

		return translation;
	}

    public void insert(Translation translation)
    {
        sqlConnection.createQuery(TranslationQueries.insert)
                .addParameter("id", translation.getId())
                .addParameter("packageId", translation.getPackageId())
                .addParameter("languageId", translation.getLanguageId())
				.addParameter("versionNumber", translation.getVersionNumber())
				.addParameter("released", translation.isReleased())
                .executeUpdate();
    }

	public void update(Translation translation)
	{
		sqlConnection.createQuery(TranslationQueries.update)
				.addParameter("id", translation.getId())
				.addParameter("packageId", translation.getPackageId())
				.addParameter("languageId", translation.getLanguageId())
				.addParameter("versionNumber", translation.getVersionNumber())
				.addParameter("released", translation.isReleased())
				.executeUpdate();
	}


	public static class TranslationQueries
    {
		public static final String selectById = "SELECT * FROM translations WHERE id = :id";
        public static final String selectByLanguageId = "SELECT * FROM translations WHERE language_id = :languageId";
        public static final String selectByPackageId = "SELECT * FROM translations WHERE package_id = :packageId";
        public static final String selectByLanguageIdPackageId = "SELECT * FROM translations WHERE package_id = :packageId AND language_id = :languageId";
		public static final String selectByLanguageIdPackageIdVersionNumber = "SELECT * FROM translations WHERE package_id = :packageId AND language_id = :languageId AND version_number = :versionNumber";
        public static final String insert = "INSERT INTO translations(id, language_id, package_id, version_number, released) VALUES(:id, :languageId, :packageId, :versionNumber, :released)";
		public static final String update = "UPDATE translations SET language_id = :languageId, package_id = :packageId, version_number = :versionNumber, released = :released WHERE id = :id";
    }

}
