package org.cru.godtools.api.packages;

import org.cru.godtools.api.authentication.AuthorizationService;
import org.cru.godtools.api.packages.domain.PixelDensity;
import org.cru.godtools.api.packages.utils.GodToolsVersion;
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
 * Created by ryancarlson on 3/14/14.
 */

@Path("/packages")
public class PackageResource
{

    @Inject
	GodToolsPackageRetrievalProcess packageRetievalProcess;
    @Inject
    AuthorizationService authService;

    @GET
	@Path("/{language}")
    @Produces({"application/zip", "application/xml"})
    public Response getAllPackagesForLanguage(@PathParam("language") String languageCode,
											  @QueryParam("interpreter") Integer minimumInterpreterVersionParam,
											  @HeaderParam("interpreter") Integer minimumInterpreterVersionHeader,
											  @QueryParam("compressed") String compressed,
											  @QueryParam("version") BigDecimal versionNumber,
											  @QueryParam("density") String desiredPixelDensity,
											  @HeaderParam("authorization") String authTokenHeader,
											  @QueryParam("authorization") String authTokenParam) throws ParserConfigurationException, SAXException, IOException
    {
        authService.checkAuthorization(authTokenParam, authTokenHeader);

        return packageRetievalProcess
                .setLanguageCode(languageCode)
                .setCompressed(Boolean.parseBoolean(compressed))
                .setVersionNumber(versionNumber == null ? GodToolsVersion.LATEST_VERSION : new GodToolsVersion(versionNumber))
                .setPixelDensity(PixelDensity.getEnumWithFallback(desiredPixelDensity, PixelDensity.HIGH))
                .setMinimumInterpreterVersion(minimumInterpreterVersionHeader == null ? minimumInterpreterVersionParam : minimumInterpreterVersionHeader)
				.loadPackages()
                .buildResponse();
    }

	@GET
	@Path("/{language}/{package}")
	@Produces({"application/zip", "application/xml"})
	public Response getPackage(@PathParam("language") String languageCode,
							   @PathParam("package") String packageCode,
							   @QueryParam("interpreter") Integer minimumInterpreterVersionParam,
							   @HeaderParam("interpreter") Integer minimumInterpreterVersionHeader,
							   @QueryParam("compressed") String compressed,
							   @QueryParam("version") BigDecimal versionNumber,
							   @QueryParam("density") String desiredPixelDensity,
							   @HeaderParam("authorization") String authTokenHeader,
							   @QueryParam("authorization") String authTokenParam) throws ParserConfigurationException, SAXException, IOException
	{
		authService.checkAuthorization(authTokenParam, authTokenHeader);

		return packageRetievalProcess
				.setLanguageCode(languageCode)
				.setPackageCode(packageCode)
				.setCompressed(Boolean.parseBoolean(compressed))
				.setVersionNumber(versionNumber == null ? GodToolsVersion.LATEST_VERSION : new GodToolsVersion(versionNumber))
				.setPixelDensity(PixelDensity.getEnumWithFallback(desiredPixelDensity, PixelDensity.HIGH))
				.setMinimumInterpreterVersion(minimumInterpreterVersionHeader == null ? minimumInterpreterVersionParam : minimumInterpreterVersionHeader)
				.loadPackages()
				.buildResponse();
	}
}
