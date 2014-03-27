package org.cru.godtools.api.packages.domain;

import java.util.UUID;

/**
 * Created by ryancarlson on 3/27/14.
 */
public class ImageResolution
{
    UUID id;
    Integer upperBound;
    Integer lowerBound;
    String resolution;

    public UUID getId()
    {
        return id;
    }

    public void setId(UUID id)
    {
        this.id = id;
    }

    public Integer getUpperBound()
    {
        return upperBound;
    }

    public void setUpperBound(Integer upperBound)
    {
        this.upperBound = upperBound;
    }

    public Integer getLowerBound()
    {
        return lowerBound;
    }

    public void setLowerBound(Integer lowerBound)
    {
        this.lowerBound = lowerBound;
    }

    public String getResolution()
    {
        return resolution;
    }

    public void setResolution(String resolution)
    {
        this.resolution = resolution;
    }
}
