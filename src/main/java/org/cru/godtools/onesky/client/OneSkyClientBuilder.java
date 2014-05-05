package org.cru.godtools.onesky.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by ryancarlson on 5/5/14.
 */
public class OneSkyClientBuilder
{
	public static final String ROOT = "https://platform.api.onesky.io/1";
	public static final String PATH = "/projects";

	public static WebTarget buildTarget(String projectId, String subPath)
	{
		Client client = ClientBuilder.newBuilder().build();
		return client.target(ROOT + PATH + "/" + projectId + subPath);
	}

	public static String createDevHash(long millisSinceEpoch, String secretKey) throws NoSuchAlgorithmException, UnsupportedEncodingException
	{
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		String millisSinceEpochString = String.valueOf(millisSinceEpoch);
		String millisAndSecretKey = millisSinceEpochString + secretKey;
		byte[] md5Hash = md5.digest(millisAndSecretKey.getBytes("UTF-8"));

		StringBuilder sb = new StringBuilder(2 * md5Hash.length);
		for(byte b : md5Hash)
		{
			sb.append(String.format("%02x", b&0xff));
		}

		return sb.toString();
	}
}
