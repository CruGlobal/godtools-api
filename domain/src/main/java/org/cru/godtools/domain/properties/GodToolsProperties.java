package org.cru.godtools.domain.properties;

import org.ccci.util.properties.CcciProperties;
import org.ccci.util.properties.PropertiesWithFallback;

/**
 * Created by ryancarlson on 3/26/14.
 */
public class GodToolsProperties extends PropertiesWithFallback
{
    private static final long serialVersionUID = 1L;

    public GodToolsProperties(CcciProperties.PropertyEncryptionSetup encryptionData, boolean firstSourceOnly, String propertiesFile, String propertiesFile2)
    {
        super(encryptionData, firstSourceOnly, propertiesFile, propertiesFile2);
    }

    public String getNonNullProperty(String property)
    {
        return getProperty(property) == null ? "" : getProperty(property);
    }
}
