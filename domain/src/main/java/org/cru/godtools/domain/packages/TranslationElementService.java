package org.cru.godtools.domain.packages;

import org.cru.godtools.domain.translations.Translation;
import org.cru.godtools.domain.translations.TranslationService;
import org.sql2o.Connection;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

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

	public void createTranslatableElements(TranslationService translationService, Translation newTranslation, Package gtPackage)
	{
		for(Translation translation : translationService.selectByPackageId(gtPackage.getId()))
		{
			//don't use the translation we just saved.. hopefully there's another
			if(translation.getId().equals(newTranslation.getId())) continue;

			for(TranslationElement translationElement : selectByTranslationId(translation.getId()))
			{
				translationElement.setTranslationId(newTranslation.getId());
				translationElement.setTranslatedText(null);
				insert(translationElement);
			}
			return;
		}

		throw new IllegalStateException("no existing translation to go off of.. better figure this out");
	}

	public List<TranslationElement> selectByTranslationId(UUID translationId, String ... orderByFields)
	{
		StringBuilder orderByBuilder = null;

		if(orderByFields != null && orderByFields.length > 0)
		{
			orderByBuilder = new StringBuilder(" ORDER BY ");
			boolean isFirst = true;
			for(String orderByField : orderByFields)
			{
				if(!isFirst) orderByBuilder.append(", ");
				orderByBuilder.append(orderByField);
				isFirst = false;
			}

		}
		return sqlConnection.createQuery(TranslationElementQueries.selectByTranslationId + (orderByBuilder != null ? orderByBuilder.toString() : ""))
				.setAutoDeriveColumnNames(true)
				.addParameter("translationId", translationId)
				.executeAndFetch(TranslationElement.class);
	}

	public void insert(TranslationElement translationElement)
	{
		sqlConnection.createQuery(TranslationElementQueries.insert)
				.addParameter("id", translationElement.getId())
				.addParameter("translationId", translationElement.getTranslationId())
				.addParameter("pageStructureId", translationElement.getPageStructureId())
				.addParameter("baseText", translationElement.getBaseText())
				.addParameter("translatedText", translationElement.getTranslatedText())
				.addParameter("elementType", translationElement.getElementType())
				.addParameter("pageName", translationElement.getPageName())
				.addParameter("displayOrder", translationElement.getDisplayOrder())
				.executeUpdate();
	}

	public void update(TranslationElement translationElement)
	{
		sqlConnection.createQuery(TranslationElementQueries.update)
				.addParameter("id", translationElement.getId())
				.addParameter("translationId", translationElement.getTranslationId())
				.addParameter("pageStructureId", translationElement.getPageStructureId())
				.addParameter("baseText", translationElement.getBaseText())
				.addParameter("translatedText", translationElement.getTranslatedText())
				.addParameter("elementType", translationElement.getElementType())
				.addParameter("pageName", translationElement.getPageName())
				.addParameter("displayOrder", translationElement.getDisplayOrder())
				.executeUpdate();
	}

	public static class TranslationElementQueries
	{
		public static final String selectByTranslationId = "SELECT * FROM translation_elements WHERE translation_id = :translationId";
		public static final String insert = "INSERT INTO translation_elements(id, translation_id, page_structure_id, base_text, translated_text, element_type, page_name, display_order) " +
			"VALUES(:id, :translationId, :pageStructureId, :baseText, :translatedText, :elementType, :pageName, :displayOrder)";
		public static final String update = "UPDATE translation_elements SET base_text = :baseText, translated_text = :translatedText, " +
				"element_type = :elementType, page_name = :pageName, display_order = :displayOrder, page_structure_id = :pageStructureId WHERE id = :id AND translation_id = :translationId";
	}
}
