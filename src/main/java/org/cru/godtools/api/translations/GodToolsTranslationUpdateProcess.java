package org.cru.godtools.api.translations;

import org.cru.godtools.api.packages.GodToolsPackageService;
import org.cru.godtools.api.images.domain.Image;
import org.cru.godtools.api.packages.domain.Version;
import org.cru.godtools.api.packages.utils.LanguageCode;
import org.cru.godtools.api.utilities.ResourceNotFoundException;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * Created by ryancarlson on 4/8/14.
 */
public class GodToolsTranslationUpdateProcess
{
	private GodToolsTranslationService godToolsTranslationService;

	private String packageCode;
	private LanguageCode languageCode;

	private Version currentDraftVersion;
	private Map<String, Image> currentTranslationImages;

	@Inject
	public GodToolsTranslationUpdateProcess(GodToolsPackageService godToolsTranslationService)
	{
		this.godToolsTranslationService = godToolsTranslationService;
	}

	public GodToolsTranslationUpdateProcess setPackageCode(String packageCode)
	{
		this.packageCode = packageCode;
		return this;
	}

	public GodToolsTranslationUpdateProcess setLanguageCode(String languageCode)
	{
		this.languageCode = new LanguageCode(languageCode);
		return this;
	}

	public GodToolsTranslationUpdateProcess loadVersion()
	{
		try
		{
			currentDraftVersion = godToolsTranslationService.getCurrentDraftVersion(packageCode,languageCode);

		}
		catch(ResourceNotFoundException e)
		{
			currentDraftVersion = godToolsTranslationService.createNewVersion(packageCode, languageCode);
		}
		return this;
	}

	public GodToolsTranslationUpdateProcess saveTranslation(NewTranslation newTranslation)
	{
		currentDraftVersion.setPackageStructure(newTranslation.getPackageFile());
		godToolsTranslationService.saveDraftPages(newTranslation, currentDraftVersion, currentTranslationImages);

//		currentDraftVersion.calculateHash();

		godToolsTranslationService.updateVersion(currentDraftVersion);

		return this;
	}

	public Response buildResponse() throws URISyntaxException
	{
		return Response.created(new URI("/translations/" + languageCode.toString() + "/" + packageCode + "?revision-number=" + currentDraftVersion.getVersionNumber())).build();
	}

}
