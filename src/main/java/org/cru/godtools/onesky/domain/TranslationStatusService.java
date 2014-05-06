package org.cru.godtools.onesky.domain;

import org.sql2o.Connection;

import javax.inject.Inject;
import java.util.UUID;

/**
 * Created by ryancarlson on 5/6/14.
 */
public class TranslationStatusService
{
	private Connection sqlConnection;

	@Inject
	public TranslationStatusService(Connection sqlConnection)
	{
		this.sqlConnection = sqlConnection;
	}

	public TranslationStatus selectByTranslationIdPageStructureId(UUID translationId, UUID pageStructureId)
	{
		return sqlConnection.createQuery(TranslationStatusQueries.selectByTranslationIdPageStructureId)
				.setAutoDeriveColumnNames(true)
				.addParameter("translationId", translationId)
				.addParameter("pageStructureId", pageStructureId)
				.executeAndFetchFirst(TranslationStatus.class);
	}

	public void insert(TranslationStatus translationStatus)
	{
		sqlConnection.createQuery(TranslationStatusQueries.insert)
				.addParameter("translationId", translationStatus.getTranslationId())
				.addParameter("pageStructureId", translationStatus.getPageStructureId())
				.addParameter("percentCompleted", translationStatus.getPercentCompleted())
				.addParameter("stringCount", translationStatus.getStringCount())
				.addParameter("wordCount", translationStatus.getWordCount())
				.addParameter("lastUpdated", translationStatus.getLastUpdated())
				.executeUpdate();
	}

	public void update(TranslationStatus translationStatus)
	{
		sqlConnection.createQuery(TranslationStatusQueries.update)
				.addParameter("translationId", translationStatus.getTranslationId())
				.addParameter("pageStructureId", translationStatus.getPageStructureId())
				.addParameter("percentCompleted", translationStatus.getPercentCompleted())
				.addParameter("stringCount", translationStatus.getStringCount())
				.addParameter("wordCount", translationStatus.getWordCount())
				.addParameter("lastUpdated", translationStatus.getLastUpdated())
				.executeUpdate();
	}

	private class TranslationStatusQueries
	{
		public static final String selectByTranslationIdPageStructureId = "SELECT * FROM translation_status WHERE translation_id = :translationId AND page_structure_id = :pageStructureId";
		public static final String insert = "INSERT INTO translation_status(translation_id, page_structure_id, percent_completed, string_count, word_count, last_updated) " +
			"VALUES(:translationId, :pageStructureId, :percentCompleted, :stringCount, :wordCount, :lastUpdated)";
		public static final String update = "UPDATE translation_status SET percent_completed = :percentCompleted, string_count = :stringCount, word_count = :wordCount, last_updated = :lastUpdated " +
				"WHERE translation_id = :translationId AND page_structure_id = :pageStructureId)";
	}
}
