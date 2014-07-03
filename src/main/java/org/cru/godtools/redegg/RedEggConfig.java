package org.cru.godtools.redegg;



import com.google.common.collect.ImmutableList;
import org.cru.redegg.recording.api.NoOpParameterSanitizer;
import org.cru.redegg.recording.api.ParameterSanitizer;
import org.cru.redegg.reporting.errbit.ErrbitConfig;

import javax.enterprise.inject.Produces;
import java.net.URISyntaxException;
import java.net.URI;

/**
 * Created by matthewfrederick on 7/3/14.
 */
public class RedEggConfig
{

    @Produces ParameterSanitizer sanitizer = new NoOpParameterSanitizer();

    @Produces public ErrbitConfig createConfig() throws URISyntaxException
    {
        ErrbitConfig config = new ErrbitConfig();
        config.setEndpoint(new URI("https://errors.uscm.org"));
        config.setKey(properties.getProperty("errbitApiKey"));
        config.setEnvironmentName(properties.getProperty("errbitEnvironment"));

        config.getApplicationBasePackages().addAll(ImmutableList.of("org.cru.goodTools"));

        return config;
    }
}
