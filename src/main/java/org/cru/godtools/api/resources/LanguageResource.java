package org.cru.godtools.api.resources;

import com.google.common.base.Optional;
import org.cru.godtools.api.translations.GodToolsTranslationService;
import org.cru.godtools.api.translations.NewTranslationCreation;
import org.cru.godtools.domain.model.*;
import org.cru.godtools.domain.languages.LanguageCode;
import org.cru.godtools.domain.services.*;
import org.cru.godtools.domain.services.annotations.*;

import javax.ejb.*;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;

/**
 * Created by ryancarlson on 10/13/14.
 */
@Stateless
@Path("/languages")
public class LanguageResource extends GodToolsBaseResource
{

	@Inject @JPAStandard
	AuthorizationService authorizationService;
	@Inject @JPAStandard
	LanguageService languageService;
	@Inject @JPAStandard
	PackageService packageService;
	@Inject
	GodToolsTranslationService godToolsTranslationService;
	@Inject
	NewTranslationCreation translationCreation;

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addLanguage(Language language, @HeaderParam("Authorization") String authorization)
	{
		Optional<AuthorizationRecord> authorizationRecord = authorizationService.getAuthorizationRecord(authorization, null);

		if(!authorizationRecord.isPresent() || !authorizationRecord.get().isAdmin()) return Response.status(401).build();

		if(languageService.languageExists(language)) return Response.status(400).build();

		language.setId(UUID.randomUUID());

		languageService.insert(language);

		for(org.cru.godtools.domain.model.Package gtPackage : packageService.selectAllPackages())
		{
			godToolsTranslationService.setupNewTranslation(LanguageCode.fromLanguage(language),
					gtPackage.getCode());
		}
		return Response.status(201).build();
	}
}
