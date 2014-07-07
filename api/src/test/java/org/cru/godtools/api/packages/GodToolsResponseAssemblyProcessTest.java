package org.cru.godtools.api.packages;

import org.cru.godtools.api.packages.utils.FileZipper;
import org.cru.godtools.api.packages.utils.GodToolsVersion;
import org.cru.godtools.tests.AbstractFullPackageServiceTest;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.math.BigDecimal;

/**
 * Created by ryancarlson on 4/2/14.
 */
public class GodToolsResponseAssemblyProcessTest extends AbstractFullPackageServiceTest
{

	GodToolsPackageRetrievalProcess responseAssemblyProcess;

	@BeforeClass
	@Override
	public void setup()
	{
		super.setup();

		responseAssemblyProcess = new GodToolsPackageRetrievalProcess(godToolsPackageService, new FileZipper());
	}

	@Test
	public void testBuildResponse() throws IOException
	{
		Response response = responseAssemblyProcess.setVersionNumber(new GodToolsVersion(new BigDecimal(1)))
				.setCompressed(false)
				.setLanguageCode("en")
				.setPackageCode("kgp")
				.setMinimumInterpreterVersion(1)
				.setPixelDensity(PixelDensity.getEnum("High"))
				.loadPackages()
				.buildResponse();

		Assert.assertNotNull(response);
		Assert.assertEquals(response.getStatus(), 200);
	}
}
