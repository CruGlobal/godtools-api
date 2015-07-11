package org.cru.godtools.api.packages;

import org.ccci.util.time.Clock;
import org.cru.godtools.domain.authentication.AuthorizationRecord;
import org.cru.godtools.domain.authentication.AuthorizationService;
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
import java.math.BigDecimal;

/**
 * Contains RESTful endpoints for delivering GodTools "package" resources.
 *  - "packages" include translation XML files as well as images.
 *
 * For more information: https://github.com/CruGlobal/godtools-api/wiki/The-Packages-Endpoint
 *
 * Created by ryancarlson on 3/14/14.
 */

@Path("/packages")
public class PackageResource
{
    @Inject
    AuthorizationService authService;
	@Inject
	Clock clock;

	private Logger log = Logger.getLogger(this.getClass());

	/**
	 * GET - get all packages for the language specified by @param languageCode.
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

		AuthorizationRecord.checkAuthorization(authService.getAuthorizationRecord(authTokenParam, authTokenHeader), clock.currentDateTime());

		return Response.noContent().build();
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
		log.info("Requesting package " + packageCode + " for language: " + languageCode);

		AuthorizationRecord.checkAuthorization(authService.getAuthorizationRecord(authTokenParam, authTokenHeader), clock.currentDateTime());

		return Response.noContent().build();
	}
}
