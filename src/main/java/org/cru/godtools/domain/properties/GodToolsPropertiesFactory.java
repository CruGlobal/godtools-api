package org.cru.godtools.domain.properties;

import javax.enterprise.inject.Produces;

/**
 * Created by ryancarlson on 3/26/14.
 */
public class GodToolsPropertiesFactory
{
    private final String propertiesFile = "/apps/apps-config/godtools-api-properties.xml";
    private final String propertiesFile2 = "/default-properties.xml";

    @Produces
    public GodToolsProperties get()
    {
        return new GodToolsProperties(null, false, propertiesFile, propertiesFile2);
    }

}
