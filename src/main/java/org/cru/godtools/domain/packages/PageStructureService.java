package org.cru.godtools.domain.packages;

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

	public List<PageStructure> selectByTranslationId(UUID translationId)
	{
		return sqlConnection.createQuery(PageStructureQueries.selectByTranslationId)
				.setAutoDeriveColumnNames(true)
				.addParameter("translationId", translationId)
				.executeAndFetch(PageStructure.class);
	}

	public PageStructure selectByTranslationIdAndFilename(UUID translationId, String filename)
	{
		return sqlConnection.createQuery(PageStructureQueries.selectByTranslationIdAndFilename)
				.setAutoDeriveColumnNames(true)
				.addParameter("translationId", translationId)
				.addParameter("filename", filename)
				.executeAndFetchFirst(PageStructure.class);
	}

	public void insert(PageStructure pageStructure)
	{
		sqlConnection.createQuery(PageStructureQueries.insert)
				.addParameter("id", pageStructure.getId())
				.addParameter("translationId", pageStructure.getTranslationId())
				.addParameter("xmlContent", pageStructure.getXmlContent())
				.addParameter("description", pageStructure.getDescription())
				.addParameter("filename", pageStructure.getFilename())
				.addParameter("percentCompleted", pageStructure.getPercentCompleted())
				.addParameter("stringCount", pageStructure.getStringCount())
				.addParameter("wordCount", pageStructure.getWordCount())
				.addParameter("lastUpdated", pageStructure.getLastUpdated())
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
				.addParameter("percentCompleted", pageStructure.getPercentCompleted())
				.addParameter("stringCount", pageStructure.getStringCount())
				.addParameter("wordCount", pageStructure.getWordCount())
				.addParameter("lastUpdated", pageStructure.getLastUpdated())
				.executeUpdate();
	}

	public static final class PageStructureQueries
	{
		public static final String selectById = "SELECT * FROM page_structure WHERE id = :id";
		public static final String selectByTranslationId = "SELECT * FROM page_structure WHERE translation_id = :translationId";
		public static final String selectByTranslationIdAndFilename = "SELECT * FROM page_structure WHERE translation_id = :translationId AND filename = :filename";
		public static final String insert = "INSERT INTO page_structure(id, xml_content, translation_id, description, filename, percent_completed, string_count, word_count, last_updated) " +
				"VALUES(:id, :xmlContent, :translationId, :description, :filename, :percentCompleted, :stringCount, :wordCount, :lastUpdated)";
		public static final String update = "UPDATE page_structure SET xml_content = :xmlContent, translation_id = :translationId, description = :description, filename = :filename, " +
				"percent_completed = :percentCompleted, string_count = :stringCount, word_count = :wordCount, last_updated = :lastUpdated WHERE id = :id";
	}
}
