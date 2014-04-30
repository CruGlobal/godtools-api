package org.cru.godtools.api.packages.domain;

import org.sql2o.Connection;

import javax.inject.Inject;

/**
 * Created by ryancarlson on 4/30/14.
 */
public class TranslationElementService
{

	Connection sqlConnection;

	@Inject
	public TranslationElementService(Connection sqlConnection)
	{
		this.sqlConnection = sqlConnection;
	}

	public void insert(TranslationElement translationElement)
	{
		sqlConnection.createQuery(TranslationElementQueries.insert)
				.addParameter("id", translationElement.getId())
				.addParameter("translationId", translationElement.getTranslationId())
				.addParameter("baseText", translationElement.getBaseText())
				.addParameter("translatedText", translationElement.getTranslatedText())
				.addParameter("elementType", translationElement.getElementType())
				.addParameter("pageName", translationElement.getPageName())
				.addParameter("displayOrder", translationElement.getDisplayOrder())
				.executeUpdate();
	}

	public static class TranslationElementQueries
	{
		public static String insert = "INSERT INTO translation_elements(id, translation_id, base_text, translated_text, element_type, page_name, display_order) " +
			"VALUES(:id, :translationId, :baseText, :translatedText, :elementType, :pageName, :displayOrder)";
	}
}
