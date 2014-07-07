package org.cru.godtools.api.utilities;

/**
 * Created by ryancarlson on 4/1/14.
 */
public class ResourceNotFoundException extends RuntimeException
{
	private Class<?> missingType;

	public ResourceNotFoundException(Class<?> missingType)
	{
		this.missingType = missingType;
	}

	public Class<?> getMissingType()
	{
		return missingType;
	}
}
