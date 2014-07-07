package org.cru.godtools.onesky.client;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.cru.godtools.properties.GodToolsProperties;
import org.cru.godtools.properties.GodToolsPropertiesFactory;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;

/**
 *
 * Client for endpoint described here: https://github.com/onesky/api-documentation-platform/blob/master/resources/file.md
 *
 *
 * Created by ryancarlson on 5/1/14.
 */
public class FileClient
{
	public static final String SUB_PATH = "/files";

	private final GodToolsProperties properties = new GodToolsPropertiesFactory().get();

	public void uploadFile(Integer projectId, String pageName, Collection<TranslationElement> translationElementList) throws Exception
	{
		WebTarget target = OneSkyClientBuilder.buildTarget(projectId, SUB_PATH);

		MultipartFormDataOutput upload = buildMultipartFormDataOutput();
		upload.addFormData("file", new ObjectMapper().writeValueAsString(buildFile(translationElementList)), MediaType.MULTIPART_FORM_DATA_TYPE, pageName);

		GenericEntity<MultipartFormDataOutput> entity = new GenericEntity<MultipartFormDataOutput>(upload){};

		Response response = target.request().post(Entity.entity(entity,MediaType.MULTIPART_FORM_DATA_TYPE));

		System.out.println("File: " + pageName);
		System.out.println("Status: " + response.getStatus());
		System.out.println("*****************************************");
	}

	private MultipartFormDataOutput buildMultipartFormDataOutput() throws Exception
	{
		long timestamp = System.currentTimeMillis() / 1000;

		MultipartFormDataOutput upload = new MultipartFormDataOutput();
		upload.addFormData("api_key", properties.get("oneskyApiKey"), MediaType.MULTIPART_FORM_DATA_TYPE);
		upload.addFormData("timestamp", String.valueOf(timestamp), MediaType.MULTIPART_FORM_DATA_TYPE);
		upload.addFormData("dev_hash", OneSkyClientBuilder.createDevHash(timestamp, (String)properties.get("oneskySecretKey")), MediaType.MULTIPART_FORM_DATA_TYPE);
		upload.addFormData("file_format", "HIERARCHICAL_JSON", MediaType.MULTIPART_FORM_DATA_TYPE);

		return upload;
	}

	private ObjectNode buildFile(Collection<TranslationElement> translationElementList)
	{
		JsonNodeFactory factory = JsonNodeFactory.instance;

		ObjectNode objectNode = new ObjectNode(factory);

		for(TranslationElement translationElement : translationElementList)
		{
			objectNode.put(translationElement.getId().toString(), translationElement.getBaseText());
		}

		return objectNode;
	}
}
