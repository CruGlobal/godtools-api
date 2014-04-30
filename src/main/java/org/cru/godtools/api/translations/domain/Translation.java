package org.cru.godtools.api.translations.domain;

import org.cru.godtools.api.languages.Language;
import org.cru.godtools.api.packages.domain.Package;

import java.util.UUID;

/**
 * Created by ryancarlson on 3/20/14.
 */
public class Translation
{
    UUID id;
    UUID packageId;
    UUID languageId;
	Integer versionNumber;

    public Translation()
    {

    }

    public Translation(Package gtPackage, Language language)
    {
        setId(UUID.randomUUID());
        setPackageId(gtPackage.getId());
        setLanguageId(language.getId());
    }

    public UUID getId()
    {
        return id;
    }

    public void setId(UUID id)
    {
        this.id = id;
    }

    public UUID getPackageId()
    {
        return packageId;
    }

    public void setPackageId(UUID packageId)
    {
        this.packageId = packageId;
    }

    public UUID getLanguageId()
    {
        return languageId;
    }

    public void setLanguageId(UUID languageId)
    {
        this.languageId = languageId;
    }


	public Integer getVersionNumber()
	{
		return versionNumber;
	}

	public void setVersionNumber(Integer versionNumber)
	{
		this.versionNumber = versionNumber;
	}
}
