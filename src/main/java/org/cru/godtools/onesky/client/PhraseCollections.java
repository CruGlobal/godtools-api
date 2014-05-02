package org.cru.godtools.onesky.client;

import com.google.common.base.Throwables;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.cru.godtools.api.packages.domain.TranslationElement;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.List;

/**
 * Created by ryancarlson on 5/1/14.
 */
public class PhraseCollections
{
	public static final String ROOT = "https://platform.api.onesky.io/1";
	public static final String PATH = "/projects";
	public static final String SUB_PATH = "/phrase-collections";


	public void importPhraseCollections(String projectId, String pageName, Collection<TranslationElement> translationElementList) throws Exception
	{

		for(TranslationElement translationElement : translationElementList)
		{
			WebTarget target = buildTarget(projectId);
			ObjectNode entity = addAuthentication(buildPhraseCollection(pageName, translationElement));
			String stringEntity = new ObjectMapper().writeValueAsString(entity);
			Response response = target.request().post(Entity.json(stringEntity));
			response = response;
		}
	}

	private WebTarget buildTarget(String projectId)
	{
		Client client = ClientBuilder.newBuilder().build();
		return client.target(ROOT + PATH + "/" + projectId + SUB_PATH);
	}

	private ObjectNode addAuthentication(ObjectNode json)
	{
		long epoch = System.currentTimeMillis() / 1000;
		json.put("api_key", "JSlA3a3tTcixcpEegcympv3HQCJB7q1H");
		json.put("timestamp", epoch);
		try
		{
			json.put("dev_hash", createDevHash(epoch));
		}
		catch(Exception e)
		{
			Throwables.propagate(e);
		}

		return json;
	}

	private String createDevHash(long millisSinceEpoch) throws NoSuchAlgorithmException, UnsupportedEncodingException
	{
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		String millisSinceEpochString = String.valueOf(millisSinceEpoch);
		String millisAndSecretKey = millisSinceEpochString + "";
		byte[] md5Hash = md5.digest(millisAndSecretKey.getBytes("UTF-8"));

		StringBuilder sb = new StringBuilder(2 * md5Hash.length);
		for(byte b : md5Hash)
		{
			sb.append(String.format("%02x", b&0xff));
		}


		return sb.toString();
	}

	private ObjectNode buildPhraseCollection(String pageName, TranslationElement translationElement)
	{
		JsonNodeFactory factory = JsonNodeFactory.instance;

		ObjectNode collectionNode = new ObjectNode(factory);
		ObjectNode elementNode = new ObjectNode(JsonNodeFactory.instance);

		elementNode.put(translationElement.getId().toString(), buildPhrase(translationElement));

		collectionNode.put(pageName, elementNode);

		ObjectNode rootNode = new ObjectNode(JsonNodeFactory.instance);
		rootNode.put("collections", collectionNode);

		return rootNode;
	}

	private ObjectNode buildPhrase(TranslationElement translationElement)
	{
		ObjectNode elementDetails = new ObjectNode(JsonNodeFactory.instance);
		ObjectNode lengthLimit = new ObjectNode(JsonNodeFactory.instance);

		elementDetails.put("string", translationElement.getBaseText());
		elementDetails.put("description", translationElement.getPageName() + "_" + translationElement.getElementType());

		lengthLimit.put("type", "absolute");
		lengthLimit.put("value", String.valueOf(translationElement.getBaseText().length()));
		lengthLimit.put("is_exceed_allowed", "true");

		elementDetails.put("length_limit", lengthLimit);

		return elementDetails;
	}

}
