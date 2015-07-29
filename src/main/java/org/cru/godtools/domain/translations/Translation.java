package org.cru.godtools.domain.translations;


import org.cru.godtools.domain.languages.Language;
import org.cru.godtools.domain.packages.Package;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.UUID;

/**
 * Created by ryancarlson on 3/20/14.
 */
@Entity
@Table(name="translations")
public class Translation implements Serializable
{
	@Id
	@Column(name="id")
	@Type(type="pg-uuid")
	private UUID id;
	@ManyToOne
	@JoinColumn(name="package_id")
	Package gtPackage;
	@Transient
	private UUID packageId;
	@ManyToOne
	@JoinColumn(name="language_id")
	private Language language;
	@Transient
	private UUID languageId; //Keep for deprecated SQL2O code
	@Column(name="translated_name")
	private String translatedName;
	@Column(name="version_number")
	private Integer versionNumber;
	@Column(name="released")
	private boolean released;

    public Translation()
    {

    }

    public Translation(Package gtPackage, Language language)
    {
        setId(UUID.randomUUID());
        setPackage(gtPackage);
        setLanguage(language);
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

	public Package getPackage() {
		return gtPackage;
	}

	public void setPackage(Package gtPackage) {
		this.gtPackage = gtPackage;
		this.packageId = gtPackage != null ? gtPackage.getId() : null;
	}

	public UUID getPackageId()
    {
        return packageId;
    }

	//required for SQL2O tests to work
    public void setPackageId(UUID packageId)
    {
        if(gtPackage == null)
		{
			gtPackage = new Package();
			gtPackage.setId(packageId);
			this.packageId = packageId;
		}
		else
		{
			this.packageId = gtPackage.getId();
		}
    }

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
		this.languageId = language != null ? language.getId() : null;
	}

	//required for SQL2O tests to work
	public void setLanguageId(UUID languageId)
	{
		if(language == null)
		{
			language = new Language();
			language.setId(languageId);
			this.languageId = languageId;
		}
		else
		{
			this.languageId = language.getId();
		}
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

	public String getTranslatedName()
	{
		return translatedName;
	}

	public void setTranslatedName(String translatedName)
	{
		this.translatedName = translatedName;
	}
}
