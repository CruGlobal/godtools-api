package org.cru.godtools.api.languages;

import java.util.UUID;

/**
 * Created by ryancarlson on 3/20/14.
 */
public class Language
{

    UUID id;
    String name;
    String code;

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
}