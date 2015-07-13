package org.cru.godtools.domain.packages;

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
    @Column(name="default_language_id")
    @Type(type="pg-uuid")
    UUID defaultLanguageId;
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

    public UUID getDefaultLanguageId()
    {
        return defaultLanguageId;
    }

    public void setDefaultLanguageId(UUID defaultLanguageId)
    {
        this.defaultLanguageId = defaultLanguageId;
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
