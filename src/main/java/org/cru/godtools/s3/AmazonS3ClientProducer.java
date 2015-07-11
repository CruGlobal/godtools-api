package org.cru.godtools.s3;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3Client;

import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;

/**
 * Created by ryancarlson on 7/11/15.
 */
public class AmazonS3ClientProducer
{

	@Produces
	@Default
	public AmazonS3Client amazonS3ClientProducer()
	{
		return new AmazonS3Client(new ProfileCredentialsProvider());
	}

}
