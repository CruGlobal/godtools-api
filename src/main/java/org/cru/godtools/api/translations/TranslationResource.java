package org.cru.godtools.api.translations;

import com.google.common.base.Optional;
import org.ccci.util.time.Clock;
import org.cru.godtools.api.translations.model.PageFile;
import org.cru.godtools.domain.Simply;
import org.cru.godtools.domain.authentication.AuthorizationRecord;
import org.cru.godtools.domain.authentication.AuthorizationService;
import org.cru.godtools.domain.GodToolsVersion;
import org.cru.godtools.domain.languages.Language;
import org.cru.godtools.domain.languages.LanguageCode;
import org.cru.godtools.domain.languages.LanguageService;
import org.cru.godtools.domain.packages.Package;
import org.cru.godtools.domain.packages.PackageService;
import org.cru.godtools.domain.packages.PageStructure;
import org.cru.godtools.domain.packages.PageStructureService;
import org.cru.godtools.domain.packages.TranslationElement;
import org.cru.godtools.domain.packages.TranslationElementService;
import org.cru.godtools.domain.translations.Translation;
import org.cru.godtools.domain.translations.TranslationService;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

/**
 * Contains RESTful endpoints for delivering GodTools "translation" resources.
 *  - "translation" include translation XML files, but not images
 *
 * For more information: https://github.com/CruGlobal/godtools-api/wiki/The-Translations-Endpoint
 *
 * Created by ryancarlson on 4/8/14.
 */
@Path("/translations")
public class TranslationResource
{
	@Inject
	AuthorizationService authService;
	@Inject
	GodToolsTranslationRetrieval translationRetrieval;
	@Inject
	GodToolsTranslationService godToolsTranslationService;
	@Inject
	PageStructureService pageStructureService;
	@Inject
	LanguageService languageService;
	@Inject
	PackageService packageService;
	@Inject
	TranslationService translationService;
	@Inject
	TranslationElementService translationElementService;
	@Inject
	Clock clock;

	private Logger log = Logger.getLogger(TranslationResource.class);
	static boolean BYPASS_ASYNC_UPDATE = false;

	@GET
	@Path("/{language}")
	@Produces({"application/zip", "application/xml"})
	public Response getTranslations(@PathParam("language") String languageCode,
									@QueryParam("interpreter") Integer minimumInterpreterVersionParam,
									@HeaderParam("interpreter") Integer minimumInterpreterVersionHeader,
									@QueryParam("compressed") String compressed,
									@HeaderParam("Authorization") String authTokenHeader,
									@QueryParam("Authorization") String authTokenParam) throws IOException
	{
		log.info("Requesting all translations for language: " + languageCode);

		AuthorizationRecord.checkAuthorization(authService.getAuthorizationRecord(authTokenParam, authTokenHeader), clock.currentDateTime());

		return translationRetrieval
				.setLanguageCode(languageCode)
				.setMinimumInterpreterVersion(minimumInterpreterVersionHeader == null ? minimumInterpreterVersionParam : minimumInterpreterVersionHeader)
				.setCompressed(Boolean.parseBoolean(compressed))
				.loadTranslations()
				.buildResponse();
	}

	@GET
	@Path("/{language}/{package}")
	@Produces({"application/zip", "application/xml"})
	public Response getTranslation(@PathParam("language") String languageCode,
								   @PathParam("package") String packageCode,
								   @QueryParam("interpreter") Integer minimumInterpreterVersionParam,
								   @HeaderParam("interpreter") Integer minimumInterpreterVersionHeader,
								   @QueryParam("compressed") String compressed,
								   @QueryParam("version") BigDecimal versionNumber,
								   @HeaderParam("Authorization") String authTokenHeader,
								   @QueryParam("Authorization") String authTokenParam) throws IOException
	{
		log.info("Requesting translation for package: " + packageCode + " and language: " + languageCode);

		AuthorizationRecord.checkAuthorization(authService.getAuthorizationRecord(authTokenParam, authTokenHeader), clock.currentDateTime());

		return translationRetrieval
				.setLanguageCode(languageCode)
				.setPackageCode(packageCode)
				.setMinimumInterpreterVersion(minimumInterpreterVersionHeader == null ? minimumInterpreterVersionParam : minimumInterpreterVersionHeader)
				.setCompressed(Boolean.parseBoolean(compressed))
				.setVersionNumber(versionNumber == null ? GodToolsVersion.LATEST_VERSION : new GodToolsVersion(versionNumber))
				.loadTranslations()
				.buildResponse();
	}

	@POST
	@Path("/{language}/{package}")
	public Response createTranslation(@PathParam("language") String languageCode,
									  @PathParam("package") String packageCode,
									  @QueryParam("interpreter") Integer minimumInterpreterVersionParam,
									  @HeaderParam("interpreter") Integer minimumInterpreterVersionHeader,
									  @HeaderParam("Authorization") String authTokenHeader,
									  @QueryParam("Authorization") String authTokenParam) throws URISyntaxException
	{
		log.info("Creating new translation for package: " + packageCode + " and language: " + languageCode);

		AuthorizationRecord.checkAccessToDrafts(authService.getAuthorizationRecord(authTokenParam, authTokenHeader), clock.currentDateTime());

		Translation translation = godToolsTranslationService.setupNewTranslation(new LanguageCode(languageCode), packageCode);

		Simply.logObject(translation, TranslationResource.class);

		// FIXME: this isn't quite right yet... major version number should not be hard coded, but for now the API doesn't support updating it
		return Response
				.created(new URI("/" + languageCode + "/" + packageCode + "?version=1." + translation.getVersionNumber()))
				.build();
	}

	@PUT
	@Path("/{language}/{package}")
	public Response updateTranslation(@PathParam("language") String languageCode,
									  @PathParam("package") String packageCode,
									  @HeaderParam("Authorization") String authTokenHeader,
									  @QueryParam("Authorization") String authTokenParam,
									  @QueryParam("publish") String publish)
	{
		log.info("Updating translation for package: " + packageCode + " and language: " + languageCode);

		AuthorizationRecord.checkAccessToDrafts(authService.getAuthorizationRecord(authTokenParam, authTokenHeader), clock.currentDateTime());

		if(Boolean.parseBoolean(publish))
		{
			log.info("Publishing translation");
			godToolsTranslationService.publishDraftTranslation(new LanguageCode(languageCode), packageCode);
			log.info("Done publishing!");
		}
		return Response.noContent().build();
	}

	@GET
	@Path("/{language}/{package}/config")
	@Produces("application/json")
	public Response getJsonConfig(@PathParam("language") String languageCode,
							  @PathParam("package") String packageCode,
							  @QueryParam("interpreter") Integer minimumInterpreterVersionParam,
							  @HeaderParam("interpreter") Integer minimumInterpreterVersionHeader,
							  @QueryParam("compressed") String compressed,
							  @HeaderParam("Authorization") String authTokenHeader,
							  @QueryParam("Authorization") String authTokenParam) throws IOException
	{
		log.info("Requesting config.xml for package: " + packageCode + " and language: " + languageCode);

		AuthorizationRecord.checkAuthorization(authService.getAuthorizationRecord(authTokenParam, authTokenHeader), clock.currentDateTime());

		return Response
				.ok(godToolsTranslationService.getConfig(packageCode, new LanguageCode(languageCode)))
				.build();
	}

	@GET
	@Path("/{language}/{package}/pages/{pageId}")
	@Produces("application/xml")
	public Response getXmlPage(@PathParam("language") String languageCode,
							@PathParam("package") String packageCode,
							@PathParam("pageId") UUID pageId,
							@QueryParam("interpreter") Integer minimumInterpreterVersionParam,
							@HeaderParam("interpreter") Integer minimumInterpreterVersionHeader,
							@HeaderParam("Authorization") String authTokenHeader,
							@QueryParam("Authorization") String authTokenParam) throws IOException, ParserConfigurationException
	{
		log.info("Requesting draft page update for package: " + packageCode + " and language: " + languageCode + " and page ID: " + pageId);

		AuthorizationRecord.checkAuthorization(authService.getAuthorizationRecord(authTokenParam, authTokenHeader), clock.currentDateTime());

		Optional<Response> optionalBadRequestResponse = verifyRequestedPageBelongsToPageAndLanguage(packageCode, languageCode, pageId);

		if(optionalBadRequestResponse.isPresent())
		{
			return optionalBadRequestResponse.get();
		}

		PageStructure pageStructure = pageStructureService.selectByid(pageId);

		List<TranslationElement> translationElements = translationElementService.selectByTranslationIdPageStructureId(translationService.selectById(pageStructure.getTranslationId()).getId(),
				pageId);

		PageFile pageFile = PageFile.fromTranslationElements(translationElements);

		return Response
				.ok(pageFile)
				.build();
	}

	@GET
	@Path("/{language}/{package}/pages/{pageId}")
	@Produces("application/json")
	public Response getJsonPage(@PathParam("language") String languageCode,
							   @PathParam("package") String packageCode,
							   @PathParam("pageId") UUID pageId,
							   @QueryParam("interpreter") Integer minimumInterpreterVersionParam,
							   @HeaderParam("interpreter") Integer minimumInterpreterVersionHeader,
							   @HeaderParam("Authorization") String authTokenHeader,
							   @QueryParam("Authorization") String authTokenParam) throws IOException, ParserConfigurationException
	{
		log.info("Requesting draft page update for package: " + packageCode + " and language: " + languageCode + " and page ID: " + pageId);

		AuthorizationRecord.checkAuthorization(authService.getAuthorizationRecord(authTokenParam, authTokenHeader), clock.currentDateTime());

		Optional<Response> optionalBadRequestResponse = verifyRequestedPageBelongsToPageAndLanguage(packageCode, languageCode, pageId);

		if(optionalBadRequestResponse.isPresent())
		{
			return optionalBadRequestResponse.get();
		}

		PageStructure pageStructure = pageStructureService.selectByid(pageId);

		List<TranslationElement> translationElements = translationElementService.selectByTranslationIdPageStructureId(translationService.selectById(pageStructure.getTranslationId()).getId(),
				pageId);

		PageFile pageFile = PageFile.fromTranslationElements(translationElements);

		return Response
				.ok(pageFile)
				.build();
	}

	private Optional<Response> verifyRequestedPageBelongsToPageAndLanguage(String packageCode, String languageCode, UUID pageId)
	{
		Package packageFromCode = packageService.selectByCode(packageCode);
		Language languageFromCode = languageService.selectByLanguageCode(new LanguageCode(languageCode));
		PageStructure pageStructure = pageStructureService.selectByid(pageId);
		Translation translation = translationService.selectById(pageStructure.getTranslationId());
		Package packageDerivedFromPage = packageService.selectById(translation.getPackageId());
		Language languageDerivedFromPage = languageService.selectLanguageById(translation.getLanguageId());

		if(!packageFromCode.getId().equals(packageDerivedFromPage.getId()))
		{
			return Optional.fromNullable(Response
					.status(Response.Status.BAD_REQUEST)
					.entity(String.format("Requested page %s does not belong to package %s", pageId.toString(), packageCode))
					.build());

		}

		if(!languageFromCode.getId().equals(languageDerivedFromPage.getId()))
		{
			return Optional.fromNullable(Response
					.status(Response.Status.BAD_REQUEST)
					.entity(String.format("Requested page %s does not belong to language %s", pageId.toString(), languageCode))
					.build());
		}

		return Optional.absent();
	}
}
