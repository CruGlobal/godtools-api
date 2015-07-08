package org.cru.godtools.domain.migration.versions;

import com.googlecode.flyway.core.api.migration.jdbc.JdbcMigration;
import org.cru.godtools.domain.database.SqlConnectionProducer;
import org.cru.godtools.domain.packages.TranslationElement;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.List;

/**
 * migration class used to remove double quotes from translation text
 */
public class V2_5__remove_extra_quotes implements JdbcMigration
{
	org.sql2o.Connection sqlConnection;

	@Override
	public void migrate(Connection connection) throws Exception
	{
		sqlConnection = new SqlConnectionProducer().getSqlConnection();
		swapConnections(connection, sqlConnection);

		List<TranslationElement> elementList = sqlConnection.createQuery("SELECT * FROM translation_elements WHERE " +
				"translated_text like '%\"\"%'")
				.setAutoDeriveColumnNames(true)
				.executeAndFetch(TranslationElement.class);

		for (TranslationElement element : elementList)
		{
			String text = element.getTranslatedText();
			text = removeDoubleQuotes(text);
			text = removeDoubleQuotesWithPeriod(text);
			element.setTranslatedText(text);
			update(element);
		}
	}

	private void swapConnections(Connection connection, org.sql2o.Connection sqlConnection)
			throws NoSuchFieldException, IllegalAccessException
	{
		Field connectionField = sqlConnection.getClass().getDeclaredField("jdbcConnection");
		connectionField.setAccessible(true);
		connectionField.set(sqlConnection, connection);
	}

	private String removeDoubleQuotes(String text)
	{
		return text.replace("\"\"", "\"");
	}

	private String removeDoubleQuotesWithPeriod(String text)
	{
		return text.replace("\".\"", "\".");
	}

	private void update(TranslationElement element)
	{
		sqlConnection.createQuery("UPDATE translation_elements SET translated_text = :translatedText " +
				"WHERE id = :id AND translation_id = :translationId")
				.addParameter("translatedText", element.getTranslatedText())
				.addParameter("id", element.getId())
				.addParameter("translationId", element.getTranslationId())
				.executeUpdate();
	}
}
