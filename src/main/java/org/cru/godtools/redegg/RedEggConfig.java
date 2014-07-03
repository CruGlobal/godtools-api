package org.cru.godtools.redegg;

import java.net.URISyntaxException;

import com.google.common.collect.ImmutableList;
import org.cru.crs.utils.CrsProperties;
import org.cru.redegg.recording.api.NoOpParameterSanitizer;
import org.cru.redegg.recording.api.ParameterSanitizer;
import org.cru.redegg.reporting.errbit.ErrbitConfig;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by matthewfrederick on 7/3/14.
 */
public class RedEggConfig
{

    @Produces ParameterSanitizer sanitizer = new NoOpParameterSanitizer();


    public
    @Produces ErrbitConfig createConfig() throws URISyntaxException
    {
        ErrbitConfig config = new ErrbitConfig();
        config.setEndpoint(new URI("https://errors.uscm.org"));
        config.setKey(properties.getProperty("errbitApiKey"));
        config.setEnvironmentName(properties.getProperty("errbitEnvironment"));

        config.getApplicationBasePackage().addAll(ImmutableList.of("org.cru.goodTools"));

        return config;
    }
}
