package org.cru.godtools.api.packages.domain;

import org.cru.godtools.migration.PackageDescriptorFile;

/**
 * Created by ryancarlson on 3/27/14.
 */
public enum PixelDensity
{
    HIGH("High"), MEDIUM("Medium"), LOW("Low");

    private PixelDensity(String friendlyValue)
    {
        this.friendlyValue = friendlyValue;
    }

    private String friendlyValue;

    public String toString()
    {
        return friendlyValue;
    }

    public static PixelDensity getEnum(String str)
    {
        for(PixelDensity density : values())
        {
            if(density.friendlyValue.equalsIgnoreCase(str)) return density;
        }
        throw new IllegalArgumentException();
    }

    public static PixelDensity getEnumWithFallback(String str, PixelDensity defaultValue)
    {
        try
        {
            return getEnum(str);
        }
        catch(IllegalArgumentException e)
        {
            return defaultValue;
        }
    }

}
