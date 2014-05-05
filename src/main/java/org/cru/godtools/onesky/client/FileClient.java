package org.cru.godtools.onesky.client;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.cru.godtools.api.packages.domain.TranslationElement;
import org.cru.godtools.properties.GodToolsProperties;
import org.cru.godtools.properties.GodToolsPropertiesFactory;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;

/**
 * Created by ryancarlson on 5/1/14.
 */
public class FileClient
{
	public static final String ROOT = "https://platform.api.onesky.io/1";
	public static final String PATH = "/projects";
	public static final String SUB_PATH = "/files";

	private final GodToolsProperties properties = new GodToolsPropertiesFactory().get();

	public void uploadFile(String projectId, String pageName, Collection<TranslationElement> translationElementList) throws Exception
	{
		WebTarget target = buildTarget(projectId);

		MultipartFormDataOutput upload = buildMultipartFormDataOutput();
		upload.addFormData("file", new ObjectMapper().writeValueAsString(buildFile(translationElementList)), MediaType.MULTIPART_FORM_DATA_TYPE, pageName);

		GenericEntity<MultipartFormDataOutput> entity = new GenericEntity<MultipartFormDataOutput>(upload){};

		Response response = target.request().post(Entity.entity(entity,MediaType.MULTIPART_FORM_DATA_TYPE));

		System.out.println("File: " + pageName);
		System.out.println("Status: " + response.getStatus());
		System.out.println("*****************************************");
	}

	private WebTarget buildTarget(String projectId)
	{
		Client client = ClientBuilder.newBuilder().build();
		return client.target(ROOT + PATH + "/" + projectId + SUB_PATH);
	}

	private MultipartFormDataOutput buildMultipartFormDataOutput() throws Exception
	{
		long timestamp = System.currentTimeMillis() / 1000;

		MultipartFormDataOutput upload = new MultipartFormDataOutput();
		upload.addFormData("api_key", properties.get("oneskyApiKey"), MediaType.MULTIPART_FORM_DATA_TYPE);
		upload.addFormData("timestamp", String.valueOf(timestamp), MediaType.MULTIPART_FORM_DATA_TYPE);
		upload.addFormData("dev_hash", createDevHash(timestamp), MediaType.MULTIPART_FORM_DATA_TYPE);
		upload.addFormData("file_format", "HIERARCHICAL_JSON", MediaType.MULTIPART_FORM_DATA_TYPE);

		return upload;
	}

	private String createDevHash(long millisSinceEpoch) throws NoSuchAlgorithmException, UnsupportedEncodingException
	{
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		String millisSinceEpochString = String.valueOf(millisSinceEpoch);
		String millisAndSecretKey = millisSinceEpochString + properties.get("oneskySecretKey");
		byte[] md5Hash = md5.digest(millisAndSecretKey.getBytes("UTF-8"));

		StringBuilder sb = new StringBuilder(2 * md5Hash.length);
		for(byte b : md5Hash)
		{
			sb.append(String.format("%02x", b&0xff));
		}

		return sb.toString();
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
