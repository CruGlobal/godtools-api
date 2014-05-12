package org.cru.godtools.api.meta;

import org.cru.godtools.api.authentication.AuthorizationService;
import org.cru.godtools.api.languages.Language;
import org.cru.godtools.api.languages.LanguageService;
import org.cru.godtools.api.packages.GodToolsPackage;
import org.cru.godtools.api.packages.domain.*;
import org.cru.godtools.api.packages.domain.Package;
import org.cru.godtools.api.packages.utils.LanguageCode;
import org.cru.godtools.api.translations.domain.Translation;
import org.cru.godtools.api.translations.domain.TranslationService;
import org.cru.godtools.api.utilities.ErrorResponse;
import org.cru.godtools.migration.KnownGodtoolsPackages;
import org.cru.godtools.onesky.io.TranslationUpload;
import org.xml.sax.SAXException;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * Created by ryancarlson on 3/14/14.
 */

@Path("/meta")
public class MetaResource
{

    @Inject
    MetaService metaService;
    @Inject
    AuthorizationService authService;

	@Inject
	PackageService packageService;
	@Inject
	TranslationService translationService;
	@Inject
	TranslationUpload translationUpload;
	@Inject
	private LanguageService languageService;

	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Response getAllMetaInfo(@QueryParam("interpreter") Integer minimumInterpreterVersionParam,
										@HeaderParam("interpreter") Integer minimumInterpreterVersionHeader,
										@QueryParam("authorization") String authCodeParam,
										@HeaderParam("authorization") String authCodeHeader) throws ParserConfigurationException, SAXException, IOException
	{
		return getLanguageAndPackageMetaInfo(null, null, minimumInterpreterVersionParam, minimumInterpreterVersionHeader, authCodeParam, authCodeHeader);
	}

    @GET
	@Path("/{language}")
    @Produces(MediaType.APPLICATION_XML)
    public Response getLanguageMetaInfo(@PathParam("language") String languageCode,
                                @QueryParam("interpreter") Integer minimumInterpreterVersionParam,
                                @HeaderParam("interpreter") Integer minimumInterpreterVersionHeader,
                                @QueryParam("authorization") String authCodeParam,
                                @HeaderParam("authorization") String authCodeHeader) throws ParserConfigurationException, SAXException, IOException
    {
       return getLanguageAndPackageMetaInfo(languageCode, null, minimumInterpreterVersionParam, minimumInterpreterVersionHeader, authCodeParam, authCodeHeader);
    }

	@GET
	@Path("/{language}/{package}")
	@Produces(MediaType.APPLICATION_XML)
	public Response getLanguageAndPackageMetaInfo(@PathParam("language") String languageCode,
								@PathParam("package") String packageCode,
								@QueryParam("interpreter") Integer minimumInterpreterVersionParam,
								@HeaderParam("interpreter") Integer minimumInterpreterVersionHeader,
								@QueryParam("authorization") String authCodeParam,
								@HeaderParam("authorization") String authCodeHeader) throws ParserConfigurationException, SAXException, IOException
	{
		authService.checkAuthorization(authCodeParam, authCodeHeader);
		Integer interpreterVersion = getMinimumInterpreterVersion(minimumInterpreterVersionParam, minimumInterpreterVersionHeader);

		if(interpreterVersion == null)
		{
			return Response.status(Response.Status.BAD_REQUEST)
					.entity(new ErrorResponse("parameter or header \"interpreter\" is required"))
					.build();
		}

		return Response.ok(metaService.getMetaResults(languageCode,
				packageCode,
				interpreterVersion,
				authService.canAccessOrCreateDrafts(authCodeParam, authCodeHeader))).build();
	}

	@POST
	@Path("/uploadAll")
	public Response temporaryEndpointToUploadAll(@QueryParam("authorization") String authCodeParam,
												 @HeaderParam("authorization") String authCodeHeader)
	{
		authService.checkAuthorization(authCodeParam, authCodeHeader);

		Language english = languageService.selectByLanguageCode(new LanguageCode("en"));
		for (Package gtPackage : KnownGodtoolsPackages.packages)
		{
			for(Translation translation : translationService.selectByPackageId(packageService.selectByCode(gtPackage.getCode()).getId()))
			{
				if(translation.getLanguageId().equals(english.getId())) translationUpload.doUpload(translation.getId());
			}
		}

		return Response.accepted().build();
	}

	private Integer getMinimumInterpreterVersion(Integer minimumInterpreterVersionParam, Integer minimumInterpreterVersionHeader)
    {
        return minimumInterpreterVersionParam == null ? minimumInterpreterVersionHeader : minimumInterpreterVersionParam;
    }
}
