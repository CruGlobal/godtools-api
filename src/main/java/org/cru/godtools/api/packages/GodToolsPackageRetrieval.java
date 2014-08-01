package org.cru.godtools.api.packages;


import com.google.common.base.Throwables;
import com.google.common.collect.Sets;
import org.cru.godtools.api.packages.utils.ProvidesImages;
import org.cru.godtools.api.translations.GodToolsTranslation;
import org.cru.godtools.api.translations.GodToolsTranslationRetrieval;

import org.cru.godtools.domain.GodToolsVersion;
import org.cru.godtools.domain.packages.PixelDensity;
import org.jboss.logging.Logger;

import javax.inject.Inject;

import java.util.Set;
import java.util.zip.ZipOutputStream;

/**
 * Stateful object with logic to take a single or multiple GodTools package(s) and perform necessary steps to return a javax.ws.rs.core.Response with the package data.
 *
 * If compressed = true, the content is zipped into a zip file by using a ZipOutputStream.
 *
 * Created by ryancarlson on 3/17/14.
 */
@ProvidesImages
public class GodToolsPackageRetrieval extends GodToolsTranslationRetrieval
{
	Logger log = Logger.getLogger(GodToolsPackageRetrieval.class);

	@Override
	public GodToolsPackageRetrieval setPackageCode(String packageCode)
	{
		super.setPackageCode(packageCode);
		return this;
	}

	@Override
	public GodToolsPackageRetrieval setLanguageCode(String languageCode)
	{
		super.setLanguageCode(languageCode);
		return this;
	}

	@Override
	public GodToolsPackageRetrieval setMinimumInterpreterVersion(Integer minimumInterpreterVersion)
	{
		super.setMinimumInterpreterVersion(minimumInterpreterVersion);
		return this;
	}

	@Override
	public GodToolsPackageRetrieval setVersionNumber(GodToolsVersion godToolsVersion)
	{
		super.setVersionNumber(godToolsVersion);
		return this;
	}

	@Override
	public GodToolsPackageRetrieval setCompressed(boolean compressed)
	{
		super.setCompressed(compressed);
		return this;
	}

	@Override
	public GodToolsPackageRetrieval setPixelDensity(PixelDensity pixelDensity)
	{
		super.setPixelDensity(pixelDensity);
		return this;
	}

	@Override
	protected void createZipFolder(ZipOutputStream zipOutputStream)
	{
		try
		{
			Set<String> imagesAlreadyZipped = Sets.newHashSet();

			for(GodToolsTranslation godToolsTranslation : godToolsTranslations)
			{
				fileZipper.zipPackageFile(godToolsTranslation.getPackageStructure(), zipOutputStream);

				fileZipper.zipPageFiles(godToolsTranslation.getPageStructureList(), zipOutputStream);

				fileZipper.zipImageFiles(godToolsTranslation.getImages(), zipOutputStream, imagesAlreadyZipped);
			}

			fileZipper.zipContentsFile(createContentsFile(), zipOutputStream);

			zipOutputStream.close();
		}
		catch(Exception e)
		{
			Throwables.propagate(e);
		}
	}
}
