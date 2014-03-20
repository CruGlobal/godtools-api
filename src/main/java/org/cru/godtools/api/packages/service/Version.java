package org.cru.godtools.api.packages.service;

import java.util.UUID;

/**
 * Created by ryancarlson on 3/20/14.
 */
public class Version
{
    UUID id;
    Integer versionNumber;
    boolean released;
    UUID packageId;
    UUID translationId;
    Integer minimumInterpreterVersion;

    public UUID getId()
    {
        return id;
    }

    public void setId(UUID id)
    {
        this.id = id;
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

    public UUID getPackageId()
    {
        return packageId;
    }

    public void setPackageId(UUID packageId)
    {
        this.packageId = packageId;
    }

    public UUID getTranslationId()
    {
        return translationId;
    }

    public void setTranslationId(UUID translationId)
    {
        this.translationId = translationId;
    }

    public Integer getMinimumInterpreterVersion()
    {
        return minimumInterpreterVersion;
    }

    public void setMinimumInterpreterVersion(Integer minimumInterpreterVersion)
    {
        this.minimumInterpreterVersion = minimumInterpreterVersion;
    }
}
