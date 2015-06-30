package org.cru.godtools.domain.services;

import org.cru.godtools.domain.GodToolsVersion;
import org.cru.godtools.domain.translations.*;
import org.sql2o.Connection;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

/**
 * Created by ryancarlson on 3/20/14.
 */
public interface TranslationService
{
	Translation selectById(UUID id);

	List<Translation> selectByLanguageId(UUID languageId);

	List<Translation> selectByLanguageIdReleased(UUID languageId, boolean released);

	List<Translation> selectByPackageId(UUID packageId);

	List<Translation> selectByLanguageIdPackageId(UUID languageId, UUID packageId);

	Translation selectByLanguageIdPackageIdVersionNumber(UUID languageId, UUID packageId, GodToolsVersion godToolsVersion);

	void insert(Translation translation);

	void update(Translation translation);

	Connection getSqlConnection();

}
