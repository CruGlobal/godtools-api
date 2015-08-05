package org.cru.godtools.domain;

import org.cru.godtools.domain.*;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by ryancarlson on 4/2/14.
 */
public class PixelDensityEnumTest
{

	@Test
	public void testGetEnum()
	{
		Assert.assertEquals(PixelDensity.HIGH, PixelDensity.getEnum("High"));
		Assert.assertEquals(PixelDensity.MEDIUM, PixelDensity.getEnum("Medium"));
		Assert.assertEquals(PixelDensity.LOW, PixelDensity.getEnum("Low"));
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testGetInvalidEnum()
	{
		PixelDensity.getEnum("Foo");
	}

	@Test
	public void testGetEnumWithFallback()
	{
		Assert.assertEquals(PixelDensity.HIGH, PixelDensity.getEnumWithFallback("High", PixelDensity.HIGH));
		Assert.assertEquals(PixelDensity.HIGH, PixelDensity.getEnumWithFallback("High", PixelDensity.MEDIUM));
		Assert.assertEquals(PixelDensity.HIGH, PixelDensity.getEnumWithFallback("Foo", PixelDensity.HIGH));
		Assert.assertEquals(PixelDensity.MEDIUM, PixelDensity.getEnumWithFallback("Foo", PixelDensity.MEDIUM));
	}
}
