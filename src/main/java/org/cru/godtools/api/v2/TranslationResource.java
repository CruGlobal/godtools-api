package org.cru.godtools.api.v2;

import org.cru.godtools.s3.AmazonS3GodToolsConfig;
import org.jboss.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.net.MalformedURLException;

@Path("/v2/translations")
public class TranslationResource
{
	private Logger log = Logger.getLogger(this.getClass());

	@GET
	@Path("/{language}")
	public Response getAllPackagesForLanguage(@PathParam("language") String languageCode) throws MalformedURLException
	{
		log.info("Requesting all packages for language: " + languageCode);

		return Response
				.status(Response.Status.MOVED_PERMANENTLY)
				.header("location", AmazonS3GodToolsConfig.getLanguagesRedirectUrl(languageCode))
				.build();
	}

	@GET
	@Path("/{language}/{package}")
	public Response getPackage(@PathParam("language") String languageCode,
							   @PathParam("package") String packageCode) throws MalformedURLException
	{
		log.info("Requesting package " + packageCode + " for language: " + languageCode);

		return Response
				.status(Response.Status.MOVED_PERMANENTLY)
				.header("location", AmazonS3GodToolsConfig.getLanguagesRedirectUrl(languageCode, packageCode))
				.build();
	}
}
