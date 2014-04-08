package org.cru.godtools.api.translations;

import org.cru.godtools.api.packages.GodToolsPackageService;
import org.cru.godtools.api.packages.domain.Page;
import org.cru.godtools.api.packages.domain.Version;
import org.cru.godtools.api.packages.utils.LanguageCode;
import org.cru.godtools.api.utilities.ResourceNotFoundException;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by ryancarlson on 4/8/14.
 */
public class GodToolsPOSTResponseBuilder
{
	private GodToolsPackageService godToolsPackageService;

	private String packageCode;
	private LanguageCode languageCode;

	private Version currentDraftVersion;
	private List<Page> currentDraftPages;

	@Inject
	public GodToolsPOSTResponseBuilder(GodToolsPackageService godToolsPackageService)
	{
		this.godToolsPackageService = godToolsPackageService;
	}

	public GodToolsPOSTResponseBuilder setPackageCode(String packageCode)
	{
		this.packageCode = packageCode;
		return this;
	}

	public GodToolsPOSTResponseBuilder setLanguageCode(String languageCode)
	{
		this.languageCode = new LanguageCode(languageCode);
		return this;
	}

	public GodToolsPOSTResponseBuilder loadVersion()
	{
		try
		{
			currentDraftVersion = godToolsPackageService.getCurrentDraftVersion(packageCode,languageCode);

		}
		catch(ResourceNotFoundException e)
		{
			currentDraftVersion = godToolsPackageService.createNewVersion(packageCode, languageCode);
		}
		return this;
	}


	public GodToolsPOSTResponseBuilder saveTranslation(NewTranslation newTranslation)
	{
		for(String filename : newTranslation.keySet())
		{
			if(!filename.contains("/"))
			{
				currentDraftVersion.setPackageStructure(newTranslation.get(filename));
			}
			else
			{
				godToolsPackageService.saveDraftPage(newTranslation.get(filename), filename, currentDraftVersion);
			}
		}

		currentDraftVersion.calculateHash();

		godToolsPackageService.updateVersion(currentDraftVersion);

		return this;
	}

	public Response buildResponse() throws URISyntaxException
	{
		return Response.created(new URI("/translations/" + languageCode.toString() + "/" + packageCode + "?revision-number=" + currentDraftVersion.getVersionNumber())).build();
	}

}
