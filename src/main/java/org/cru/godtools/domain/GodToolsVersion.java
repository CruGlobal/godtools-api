package org.cru.godtools.domain;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.cru.godtools.domain.packages.PackageStructure;
import org.cru.godtools.domain.translations.Translation;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by ryancarlson on 5/9/14.
 */
public class GodToolsVersion
{
	public static final GodToolsVersion LATEST_VERSION = new GodToolsVersion(new BigDecimal(-76123));
	public static final GodToolsVersion LATEST_PUBLISHED_VERSION = new GodToolsVersion(new BigDecimal(-135126));
	public static final GodToolsVersion DRAFT_VERSION = new GodToolsVersion(new BigDecimal(-1289988));

	final private int packageVersion;
	final private int translationVersion;

	public GodToolsVersion(BigDecimal bigDecimalVersion)
	{
		packageVersion = bigDecimalVersion.intValue();
		translationVersion = bigDecimalVersion.subtract(bigDecimalVersion.setScale(0, RoundingMode.FLOOR)).movePointRight(bigDecimalVersion.scale()).intValue();
	}

	public GodToolsVersion(PackageStructure packageStructure, Translation translation)
	{
		packageVersion = packageStructure.getVersionNumber();
		translationVersion = translation.getVersionNumber();
	}

	public int getPackageVersion()
	{
		return packageVersion;
	}

	public int getTranslationVersion()
	{
		return translationVersion;
	}

	@Override
	public String toString()
	{
		return packageVersion + "." + translationVersion;
	}

	@Override
	public boolean equals(Object other)
	{
		if(other == null) return false;
		if(other == this) return true;
		if(!other.getClass().isAssignableFrom(GodToolsVersion.class)) return false;

		GodToolsVersion otherVersion = (GodToolsVersion) other;

		return packageVersion == otherVersion.packageVersion &&
				translationVersion == otherVersion.translationVersion;
	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
				append(packageVersion).
				append(translationVersion).
				toHashCode();
	}
}
