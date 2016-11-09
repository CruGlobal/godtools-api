package org.cru.godtools.api.v2;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.cru.godtools.api.v2.functions.NewPackage;
import org.cru.godtools.s3.AmazonS3GodToolsConfig;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

@Path("v2/packages")
public class PackageResource
{
	private Logger log = Logger.getLogger(this.getClass());

	@Inject
	NewPackage newPackage;

	@GET
	@Path("/{language}")
	public Response getAllPackagesForLanguage(@PathParam("language") String languageCode) throws MalformedURLException
	{
		log.info("Requesting all packages for language: " + languageCode);

		return Response
				.status(Response.Status.MOVED_PERMANENTLY)
				.header("location", AmazonS3GodToolsConfig.getPackagesRedirectUrl(languageCode))
				.build();
	}

	@GET
	@Path("/{language}/{package}")
	public Response getPackage(@PathParam("language") String languageCode,
							   @PathParam("package") String packageCode) throws MalformedURLException
	{
		log.info("Requesting package " + packageCode + " for language: " + languageCode);

		return Response
				.status(Response.Status.NOT_FOUND)
				.build();
	}

	@POST
	public Response createPackage(org.cru.godtools.domain.packages.Package gtPackage,
								  @QueryParam("numPages") Integer numberOfPages,
								  @QueryParam("languages") String languages
								  )
	{
		List<String> languageCodes;

		if(!Strings.isNullOrEmpty(languages)) {
			languageCodes = Lists.newArrayList(languages.split(","));
		} else {
			languageCodes = new ArrayList<>();
		}

		newPackage.create(gtPackage,
				numberOfPages == null ? 1 : numberOfPages,
				languageCodes);


		return Response
				.ok()
				.build();
	}

}
