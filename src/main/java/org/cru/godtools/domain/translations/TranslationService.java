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
			return returnLatestVersion(languageId, packageId);
		}
		else if(godToolsVersion == GodToolsVersion.LATEST_PUBLISHED_VERSION)
		{
			return returnLatestPublishedVersion(languageId, packageId);
		}
		else if(godToolsVersion == GodToolsVersion.DRAFT_VERSION)
		{
			return returnDraftVersion(languageId, packageId);
		}
		else
		{
			Translation translation = sqlConnection.createQuery(TranslationQueries.selectByLanguageIdPackageIdVersionNumber)
					.setAutoDeriveColumnNames(true)
					.addParameter("packageId", packageId)
					.addParameter("languageId", languageId)
					.addParameter("versionNumber", godToolsVersion.getTranslationVersion())
					.executeAndFetchFirst(Translation.class);

			return translation;
		}
	}

	private Translation returnDraftVersion(UUID languageId, UUID packageId)
	{
		for(Translation translation : selectByLanguageIdPackageId(languageId, packageId))
		{
			if(!translation.isReleased())
			{
				return translation;
			}
		}
		return null;
	}

	private Translation returnLatestPublishedVersion(UUID languageId, UUID packageId)
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

	private Translation returnLatestVersion(UUID languageId, UUID packageId)
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

	public void insert(Translation translation)
    {
        sqlConnection.createQuery(TranslationQueries.insert)
                .addParameter("id", translation.getId())
                .addParameter("packageId", translation.getPackageId())
                .addParameter("languageId", translation.getLanguageId())
				.addParameter("versionNumber", translation.getVersionNumber())
				.addParameter("translatedName", translation.getTranslatedName())
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
				.addParameter("translatedName", translation.getTranslatedName())
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
        public static final String insert = "INSERT INTO translations(id, language_id, package_id, version_number, translated_name, released) VALUES(:id, :languageId, :packageId, :versionNumber, :translatedName, :released)";
		public static final String update = "UPDATE translations SET language_id = :languageId, package_id = :packageId, version_number = :versionNumber, translated_name = :translatedName, released = :released WHERE id = :id";
    }

}
