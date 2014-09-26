package org.cru.godtools.api.cache;

import com.google.common.base.Optional;
import org.cru.godtools.api.translations.GodToolsTranslation;

import java.util.UUID;

/**
 * Created by ryancarlson on 9/26/14.
 */
public interface GodToolsCache
{
	Optional<GodToolsTranslation> get(UUID translationId);
	void add(GodToolsTranslation godToolsTranslation);
	Optional<GodToolsTranslation> remove(UUID translationId);
	void replace(GodToolsTranslation godToolsTranslation);
}
