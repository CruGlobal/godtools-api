package org.cru.godtools.api.cache;

import com.google.common.base.Optional;
import org.cru.godtools.api.translations.GodToolsTranslation;

import java.util.UUID;

/**
 * Created by ryancarlson on 9/26/14.
 */
public class NoOpCache implements GodToolsCache
{
	@Override
	public Optional<GodToolsTranslation> get(UUID translationId)
	{
		return Optional.absent();
	}

	@Override
	public void add(GodToolsTranslation godToolsTranslation)
	{

	}

	@Override
	public Optional<GodToolsTranslation> remove(UUID translationId)
	{
		return Optional.absent();
	}

	@Override
	public void replace(GodToolsTranslation godToolsTranslation)
	{

	}
}
