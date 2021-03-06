package org.cru.godtools.api.v2;

import com.google.common.base.Optional;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerException;
import org.ccci.util.time.Clock;
import org.cru.godtools.api.translations.GodToolsTranslation;
import org.cru.godtools.api.translations.GodToolsTranslationService;
import org.cru.godtools.api.v2.functions.ChangeType;
import org.cru.godtools.api.v2.functions.DraftTranslation;
import org.cru.godtools.api.v2.functions.TranslationPackager;
import org.cru.godtools.domain.authentication.AuthorizationRecord;
import org.cru.godtools.domain.authentication.AuthorizationService;
import org.cru.godtools.domain.packages.Package;
import org.cru.godtools.domain.packages.PackageService;
import org.jboss.logging.Logger;
import org.w3c.dom.Document;

import javax.inject.Inject;

import javax.ws.rs.core.Response;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.UUID;
import org.xml.sax.SAXException;


@Path("/v2/drafts")
public class DraftResource
{

	@Inject
	AuthorizationService authService;

	@Inject
	DraftTranslation draftTranslation;

	@Inject
	PackageService packageService;

	@Inject
	Clock clock;

	@Inject
	org.cru.godtools.api.translations.DraftResource draftResourceV1;

	@Inject
	GodToolsTranslationService godToolsTranslationService;

	private Logger log = Logger.getLogger(getClass());

	@GET
	@Path("/{language}")
	@Produces("application/zip")
	public Response getTranslations(@PathParam("language") String languageCode,
									@HeaderParam("Authorization") String authTokenHeader,
									@QueryParam("Authorization") String authTokenParam) throws IOException
	{
		log.info("Requesting all drafts for language: " + languageCode);

		AuthorizationRecord.checkAccessToDrafts(authService.getAuthorizationRecord(authTokenParam, authTokenHeader), clock.currentDateTime());

		TranslationPackager packager = new TranslationPackager();

		List<GodToolsTranslation> godToolsTranslationList = draftTranslation.retrieve(languageCode);

		if(godToolsTranslationList.isEmpty())
		{
			return Response
					.status(Response.Status.NOT_FOUND)
					.build();
		}
		else
		{
			return Response
					.ok(packager.compress(godToolsTranslationList, true))
					.build();
		}
	}

	@GET
	@Path("/{language}/{package}")
	@Produces("application/zip")
	public Response getTranslation(@PathParam("language") String languageCode,
								   @PathParam("package") String packageCode,
								   @HeaderParam("Authorization") String authTokenHeader,
								   @QueryParam("Authorization") String authTokenParam) throws IOException
	{
		log.info("Requesting draft translation for package: " + packageCode + " and language: " + languageCode);

		AuthorizationRecord.checkAccessToDrafts(authService.getAuthorizationRecord(authTokenParam, authTokenHeader), clock.currentDateTime());

		TranslationPackager packager = new TranslationPackager();

		Optional<GodToolsTranslation> godToolsTranslationOptional = draftTranslation.retrieve(languageCode, packageCode);

		if(godToolsTranslationOptional.isPresent())
		{
			return Response
					.ok(packager.compress(godToolsTranslationOptional.get(), true))
					.build();
		}
		else
		{
			return Response
					.status(Response.Status.NOT_FOUND)
					.build();
		}
	}

	@GET
	@Path("/{language}/{package}/pages")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPages(@PathParam("language") String languageCode,
							 @PathParam("package") String packageCode,
							 @HeaderParam("Authorization") String authTokenHeader) throws MalformedURLException
	{
		log.info("Requesting draft pages for package: " + packageCode + " for language: " + languageCode);

		AuthorizationRecord.checkAccessToDrafts(authService.getAuthorizationRecord(null, authTokenHeader), clock.currentDateTime());

		Optional<GodToolsTranslation> godToolsTranslationOptional = draftTranslation.retrieve(languageCode, packageCode, false);

		if(!godToolsTranslationOptional.isPresent())
		{
			return Response
					.status(Response.Status.NOT_FOUND)
					.build();
		}

		return Response
				.ok(godToolsTranslationOptional.get().getPageStructureList())
				.build();
	}

	@GET
	@Path("/{language}/{package}/pages/{pageId}")
	@Produces({"application/zip", "application/xml"})
	public Response getPage(@PathParam("language") String languageCode,
							@PathParam("package") String packageCode,
							@PathParam("pageId") UUID pageId,
							@QueryParam("interpreter") Integer minimumInterpreterVersionParam,
							@HeaderParam("interpreter") Integer minimumInterpreterVersionHeader,
							@QueryParam("compressed") String compressed,
							@HeaderParam("Authorization") String authTokenHeader,
							@QueryParam("Authorization") String authTokenParam) throws IOException, ParserConfigurationException
	{
		return draftResourceV1.getPage(
				languageCode,
				packageCode,
				pageId,
				minimumInterpreterVersionParam,
				minimumInterpreterVersionHeader,
				compressed,
				authTokenHeader,
				authTokenParam);
	}

	@PUT
	@Path("/{language}/{package}/pages/{pageId}")
	@Consumes("application/xml")
	public Response updatePageLayoutForSpecificLanguage(@PathParam("language") String languageCode,
														@PathParam("package") String packageCode,
														@PathParam("pageId") UUID pageId,
														@QueryParam("interpreter") Integer minimumInterpreterVersionParam,
														@HeaderParam("interpreter") Integer minimumInterpreterVersionHeader,
														@HeaderParam("Authorization") String authTokenHeader,
														@QueryParam("Authorization") String authTokenParam,
														Document updatedPageLayout) throws IOException
	{
		return draftResourceV1.updatePageLayoutForSpecificLanguage(languageCode,
				packageCode,
				pageId,
				minimumInterpreterVersionParam,
				minimumInterpreterVersionHeader,
				authTokenHeader,
				authTokenParam,
				updatedPageLayout);
	}

	@PUT
	@Path("/{package}/pages/{pageName}")
	@Consumes("application/xml")
	public Response updatePageLayoutForAllLanguages(@PathParam("package") String packageCode,
													@PathParam("pageName") String pageName,
													@QueryParam("interpreter") Integer minimumInterpreterVersionParam,
													@HeaderParam("interpreter") Integer minimumInterpreterVersionHeader,
													@HeaderParam("Authorization") String authTokenHeader,
													@DefaultValue("ADD_ELEMENTS") @QueryParam("changeType") String changeTypeString,
													Document updatedPageLayout) throws IOException,
			XMLStreamException,TransformerException, ParserConfigurationException,SAXException
	{
		log.info("Updating draft page update for package: " + packageCode + " and page ID: " + pageName);

		Optional<AuthorizationRecord> authorizationRecord = authService.getAuthorizationRecord("", authTokenHeader);
		AuthorizationRecord.checkAdminAccess(authorizationRecord, clock.currentDateTime());

		authService.updateAdminRecordExpiration(authorizationRecord.get(), 4);

		Package gtPackage = packageService.selectByCode(packageCode);

		if(gtPackage == null)
		{
			return Response
					.status(Response.Status.BAD_REQUEST)
					.build();
		}

		ChangeType changeType = ChangeType.fromStringSafely(changeTypeString);

		if(changeType == null) changeType = ChangeType.ADD_ELEMENTS;

		switch (changeType)
		{
			case ADD_ELEMENTS:
				godToolsTranslationService.addToPageLayout(gtPackage.getId(), pageName, updatedPageLayout);
				break;
			case REMOVE_ELEMENTS:
				godToolsTranslationService.removeFromPageLayout(gtPackage.getId(), pageName, updatedPageLayout);
				break;
			case ADD_REMOVE_ELEMENTS:
				break;
			case UPDATE_ELEMENTS:
				break;
			case OVERWRITE:
				godToolsTranslationService.updatePageLayout(gtPackage.getId(), pageName, updatedPageLayout);
				break;
		}

		return Response
				.noContent()
				.build();
	}
}
