package org.cru.godtools.api.packages.domain;

import org.w3c.dom.Document;

import java.util.UUID;

/**
 * Created by ryancarlson on 3/20/14.
 */
public class Package
{
    UUID id;
    String name;
    String code;
    UUID defaultLanguageId;

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
}
