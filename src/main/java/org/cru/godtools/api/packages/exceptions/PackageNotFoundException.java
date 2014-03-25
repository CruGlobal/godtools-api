package org.cru.godtools.api.packages.exceptions;

/**
 * Created by ryancarlson on 3/25/14.
 */
public class PackageNotFoundException extends Exception
{
    public final String packageCode;

    public PackageNotFoundException(String packageCode)
    {
        super("Package with code: " + packageCode + " was not found.");
        this.packageCode = packageCode;
    }

    public String getPackageCode()
    {
        return packageCode;
    }
}
