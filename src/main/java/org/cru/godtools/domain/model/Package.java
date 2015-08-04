package org.cru.godtools.domain.model;

import org.cru.godtools.domain.model.*;
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
@Table(name="packages")
public class Package implements Serializable
{
    @Id
    @Column(name="id")
    @Type(type="pg-uuid")
    UUID id;
    @Column(name="name")
    String name;
    @Column(name="code")
    String code;
    @ManyToOne
    @JoinColumn(name="default_language_id")
    Language defaultLanguage;
    @Transient
    UUID defaultLanguageId; //Keep for deprecated SQL2O auto-column names
	@Column(name="translation_project_id")
    Integer translationProjectId;


    public UUID getId()
    {
        return id;
    }

    public void setId(UUID id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public Language getDefaultLanguage() {
        return defaultLanguage;
    }

    public void setDefaultLanguage(Language defaultLanguage)
    {
        this.defaultLanguage = defaultLanguage;
    }

    //Required by SQL2O to test properly
    public void setDefaultLanguageId(UUID defaultLanguageId)
    {
        if(defaultLanguage == null)
        {
            defaultLanguage = new Language();
            defaultLanguage.setId(defaultLanguageId);
            this.defaultLanguageId = defaultLanguageId;
        }
        else
        {
            this.defaultLanguageId = defaultLanguage.getId();
        }
    }

	public Integer getTranslationProjectId()
	{
		return translationProjectId;
	}

	public void setTranslationProjectId(Integer oneskyProjectId)
	{
		this.translationProjectId = oneskyProjectId;
	}
}
