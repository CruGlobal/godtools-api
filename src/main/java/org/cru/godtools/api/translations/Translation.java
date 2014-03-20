package org.cru.godtools.api.translations;

import java.util.UUID;

/**
 * Created by ryancarlson on 3/20/14.
 */
public class Translation
{
    UUID id;
    UUID packageId;
    UUID languageId;

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
}
