package org.cru.godtools.redegg;


import org.cru.godtools.domain.properties.GodToolsProperties;
import com.google.common.collect.ImmutableList;
import org.cru.redegg.recording.api.NoOpParameterSanitizer;
import org.cru.redegg.recording.api.ParameterSanitizer;
import org.cru.redegg.reporting.errbit.ErrbitConfig;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.net.URISyntaxException;
import java.net.URI;

/**
 * Created by matthewfrederick on 7/3/14.
 */
public class RedEggConfig
{
    @Inject GodToolsProperties properties;

    @Produces ParameterSanitizer sanitizer = new NoOpParameterSanitizer();

    @Produces public ErrbitConfig createConfig() throws URISyntaxException
    {
        ErrbitConfig config = new ErrbitConfig();
        config.setEndpoint(new URI(properties.getProperty("errbitEndpoint")));
        config.setKey(properties.getProperty("errbitApiKey"));
        config.setEnvironmentName(properties.getProperty("errbitEnvironment"));
        config.getApplicationBasePackages().addAll(ImmutableList.of("org.cru.godtools"));
        return config;
    }
}
