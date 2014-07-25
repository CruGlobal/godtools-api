package org.cru.godtools.domain.translations;


import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.cru.godtools.domain.languages.Language;
import org.cru.godtools.domain.packages.Package;
import java.util.UUID;

/**
 * Created by ryancarlson on 3/20/14.
 */
public class Translation
{
	private UUID id;
	private UUID packageId;
	private UUID languageId;
	private Integer versionNumber;
	private boolean released;

    public Translation()
    {

    }

    public Translation(Package gtPackage, Language language)
    {
        setId(UUID.randomUUID());
        setPackageId(gtPackage.getId());
        setLanguageId(language.getId());
    }

	/**
	 * In some contexts it's nicer to read isDraft() instead of !isReleased()
	 *
	 * Purely for ease of reading code elsewhere.
	 *
	 */
	public boolean isDraft()
	{
		return !isReleased();
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

	public boolean isReleased()
	{
		return released;
	}

	public void setReleased(boolean released)
	{
		this.released = released;
	}
}
