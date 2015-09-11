package org.cru.godtools.api.translations;

import com.google.common.base.Optional;
import org.ccci.util.time.Clock;
import org.cru.godtools.api.translations.model.PageFile;

import org.cru.godtools.api.v2.functions.DraftTranslation;
import org.cru.godtools.api.v2.functions.PublishedTranslation;
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
@Deprecated
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

	// included from v2 API so updates from older devices still make it to S3
	@Inject
	DraftTranslation draftTranslation;
	// included from v2 API so updates from older devices still make it to S3
	@Inject
	PublishedTranslation publishedTranslation;

	private Logger log = Logger.getLogger(this.getClass());

	@POST
	@Path("/{language}/{package}")
	@Deprecated
	public Response createTranslation(@PathParam("language") String languageCode,
									  @PathParam("package") String packageCode,
									  @QueryParam("interpreter") Integer minimumInterpreterVersionParam,
									  @HeaderParam("interpreter") Integer minimumInterpreterVersionHeader,
									  @HeaderParam("Authorization") String authTokenHeader,
									  @QueryParam("Authorization") String authTokenParam) throws URISyntaxException
	{
		log.info("Creating new translation for package: " + packageCode + " and language: " + languageCode);

		AuthorizationRecord.checkAccessToDrafts(authService.getAuthorizationRecord(authTokenParam, authTokenHeader), clock.currentDateTime());

		draftTranslation.create(languageCode,packageCode);

		// FIXME: this isn't quite right yet... major version number should not be hard coded, but for now the API doesn't support updating it
		return Response
				.status(Response.Status.CREATED)
				.build();
	}

	@PUT
	@Path("/{language}/{package}")
	@Deprecated
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
			draftTranslation.publish(languageCode,packageCode);
			publishedTranslation.pushToS3(languageCode, packageCode);
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

		return Response
				.ok(godToolsTranslationService.getConfig(packageCode, new LanguageCode(languageCode), GodToolsVersion.LATEST_PUBLISHED_VERSION))
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

		Optional<Response> optionalBadRequestResponse = verifyRequestedPageBelongsToPageAndLanguage(packageCode, languageCode, pageId);

		if(optionalBadRequestResponse.isPresent())
		{
			return optionalBadRequestResponse.get();
		}

		PageStructure pageStructure = pageStructureService.selectByid(pageId);

		if(pageStructure == null)
		{
			return Response
					.status(Response.Status.NOT_FOUND)
					.build();
		}

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
		if(packageFromCode == null)
		{
			return Optional.fromNullable(
					Response.status(Response.Status.NOT_FOUND)
							.entity(String.format("Requested package %s was not found", packageCode))
							.build());
		}

		Language languageFromCode = languageService.selectByLanguageCode(new LanguageCode(languageCode));
		if(languageFromCode == null)
		{
			return Optional.fromNullable(
					Response.status(Response.Status.NOT_FOUND)
							.entity(String.format("Requested language %s was not found", languageCode))
							.build());
		}

		PageStructure pageStructure = pageStructureService.selectByid(pageId);
		if(pageStructure == null)
		{
			return Optional.fromNullable(
					Response.status(Response.Status.NOT_FOUND)
							.entity(String.format("Requested page %s was not found", pageId.toString()))
							.build());
		}

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
