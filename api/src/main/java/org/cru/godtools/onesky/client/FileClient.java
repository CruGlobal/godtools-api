package org.cru.godtools.onesky.client;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;
import org.cru.godtools.domain.packages.TranslationElement;
import org.cru.godtools.domain.properties.GodToolsProperties;
import org.cru.godtools.domain.properties.GodToolsPropertiesFactory;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;

/**
 * Client for endpoint described here: https://github.com/onesky/api-documentation-platform/blob/master/resources/file.md
 *
 *
 * Created by ryancarlson on 5/1/14.
 */
public class FileClient
{
	public static final String SUB_PATH = "/files";

	private final GodToolsProperties properties = new GodToolsPropertiesFactory().get();

	/**
	 * Uploads a file to oneskyapp.com.  A "file" represents a joint group of strings that should be translated.
	 * For this project it is equivalent to one page of GodTools content (ex: 01_Home.xml)
	 *
	 * @param projectId - the project's ID in oneskyapp. project would be one of (kgp, fourlaws, satisfied, etc...).
	 *                     the id itself is numeric
	 * @param pageName - the name of the current page being uploaded. should be user-friendly name so that users can
	 *                 know the context for what's being translated (ex: 01_Home.xml)
	 * @param translationElementList - list of elements from the database, contains base translation, translated value,
	 *                               unique identifier for the element being translated.
	 *
	 */
	public void uploadFile(Integer projectId, String pageName, String locale, Collection<TranslationElement> translationElementList) throws Exception
	{
		WebTarget target = OneSkyClientBuilder.buildTarget(projectId, SUB_PATH);

		GenericEntity<MultipartFormDataOutput> entity = new GenericEntity<MultipartFormDataOutput>(buildMultipartFormDataOutput(pageName, locale, translationElementList)){};

		Response response = target
				.request()
				.post(Entity.entity(entity,MediaType.MULTIPART_FORM_DATA_TYPE));

		System.out.println("File: " + pageName);
		System.out.println("Status: " + response.getStatus());
		System.out.println("*****************************************");
	}

	/**
	 * Builds and prepares the form data that will be uploaded.  The type will be "multipart/form-data".
	 *
	 * Sets the following values:
	 *  - api_key (oneskyapp public key for Cru)
	 *  - timestamp (current timestamp in milliseconds)
	 *  - dev_hash (a hash of the current timestamp and oneskyapp private key)
	 *  - file_format (for now, always HIERARCHICAL_JSON)
	 *  - the text file build from the translation element list.
	 *
	 *  Returns an instance of MultipartFormDataOutput with the above values set.
	 */
	private MultipartFormDataOutput buildMultipartFormDataOutput(String pageName, String locale, Collection<TranslationElement> translationElementList) throws Exception
	{
		long timestamp = System.currentTimeMillis() / 1000;

		MultipartFormDataOutput upload = new MultipartFormDataOutput();
		upload.addFormData("api_key", properties.get("oneskyApiKey"), MediaType.MULTIPART_FORM_DATA_TYPE);
		upload.addFormData("timestamp", String.valueOf(timestamp), MediaType.MULTIPART_FORM_DATA_TYPE);
		upload.addFormData("dev_hash", OneSkyClientBuilder.createDevHash(timestamp, (String)properties.get("oneskySecretKey")), MediaType.MULTIPART_FORM_DATA_TYPE);
		upload.addFormData("file_format", "HIERARCHICAL_JSON", MediaType.MULTIPART_FORM_DATA_TYPE);
		upload.addFormData("file", new ObjectMapper().writeValueAsString(buildFile(translationElementList)), MediaType.MULTIPART_FORM_DATA_TYPE, pageName);
		if(!Strings.isNullOrEmpty(locale)) upload.addFormData("locale", locale, MediaType.MULTIPART_FORM_DATA_TYPE);

		return upload;
	}

	/**
	 * Builds and returns an ObjectNode.  This node is a hash of unique identifier and base translation
	 * text (English).
	 */
	private ObjectNode buildFile(Collection<TranslationElement> translationElementList)
	{
		JsonNodeFactory factory = JsonNodeFactory.instance;

		ObjectNode objectNode = new ObjectNode(factory);

		for(TranslationElement translationElement : translationElementList)
		{
			objectNode.put(translationElement.getId().toString(), translationElement.getTranslatedText());
		}

		return objectNode;
	}
}
