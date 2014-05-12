package org.cru.godtools.api.packages.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by ryancarlson on 5/9/14.
 */
public class GodToolsVersion
{
	public static final GodToolsVersion LATEST_VERSION = new GodToolsVersion(new BigDecimal(-76123));
	public static final GodToolsVersion LATEST_PUBLISHED_VERSION = new GodToolsVersion(new BigDecimal(-135126));

	final private int packageVersion;
	final private int translationVersion;

	public GodToolsVersion(BigDecimal bigDecimalVersion)
	{
		packageVersion = bigDecimalVersion.intValue();
		translationVersion = bigDecimalVersion.subtract(bigDecimalVersion.setScale(0, RoundingMode.FLOOR)).movePointRight(bigDecimalVersion.scale()).intValue();
	}

	public int getPackageVersion()
	{
		return packageVersion;
	}

	public int getTranslationVersion()
	{
		return translationVersion;
	}
}
