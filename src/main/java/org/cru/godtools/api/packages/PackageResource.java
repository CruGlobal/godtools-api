package org.cru.godtools.api.packages;

import com.amazonaws.services.s3.model.S3Object;
import org.ccci.util.time.Clock;
import org.cru.godtools.domain.authentication.AuthorizationRecord;
import org.cru.godtools.domain.authentication.AuthorizationService;
import org.cru.godtools.s3.GodToolsS3Client;
import org.jboss.logging.Logger;
import org.xml.sax.SAXException;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * Contains RESTful endpoints for delivering GodTools "package" resources.
 *  - "packages" include translation XML files as well as images.
 *
 * For more information: https://github.com/CruGlobal/godtools-api/wiki/The-Packages-Endpoint
 *
 * Created by ryancarlson on 3/14/14.
 */
@Deprecated
@Path("/packages")
public class PackageResource
{
    @Inject
    AuthorizationService authService;

	@Inject
	GodToolsS3Client godToolsS3Client;

	@Inject
	Clock clock;

	private Logger log = Logger.getLogger(this.getClass());

	/**
	 * GET - get all packages for the language specified by @param languageCode.
	 *
	 * NOTE: "application/xml" has to be included as an accepted header b/c the android app currently passes that along.  the old version of this endpoint
	 * could have returned just the XML "contents" or the whole zipped folder if compressed=true were passed.  Now it only returns the zipped content, but
	 * Android clients who don't update their app will break b/c they currently & incorrectly pass an Accept: application/xml header
	 */
    @GET
	@Path("/{language}")
    @Produces({"application/zip", "application/xml"})
    public Response getAllPackagesForLanguage(@PathParam("language") String languageCode,
											  @HeaderParam("interpreter") Integer minimumInterpreterVersionHeader,
											  @QueryParam("compressed") String compressed,
											  @HeaderParam("Authorization") String authTokenHeader,
											  @QueryParam("Authorization") String authTokenParam) throws ParserConfigurationException, SAXException, IOException
    {
		log.info("Requesting all packages for language: " + languageCode);

		S3Object packagesZippedFolder = godToolsS3Client.getPackagesZippedFolder(languageCode);

		return Response
				.ok(packagesZippedFolder.getObjectContent())
				.type("application/zip")
				.build();
	}

	/**
	 * GET - get all the package specified by @param packageCode for the language specified by @param languageCode.
	 */
	@GET
	@Path("/{language}/{package}")
	@Produces({"application/zip", "application/xml"})
	public Response getPackage(@PathParam("language") String languageCode,
							   @PathParam("package") String packageCode,
							   @QueryParam("compressed") String compressed,
							   @HeaderParam("Authorization") String authTokenHeader,
							   @QueryParam("Authorization") String authTokenParam) throws Exception
	{
		return Response
				.status(Response.Status.NOT_FOUND)
				.build();
	}
}
