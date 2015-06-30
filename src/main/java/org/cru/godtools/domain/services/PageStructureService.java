package org.cru.godtools.domain.services;

import org.cru.godtools.domain.packages.*;
import org.sql2o.Connection;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

/**
 * Created by ryancarlson on 4/30/14.
 */
public interface PageStructureService
{
	PageStructure selectByid(UUID id);

	List<PageStructure> selectByTranslationId(UUID translationId);

	PageStructure selectByTranslationIdAndFilename(UUID translationId, String filename);

	void insert(PageStructure pageStructure);

	void update(PageStructure pageStructure);

	Connection getSqlConnection();
}
