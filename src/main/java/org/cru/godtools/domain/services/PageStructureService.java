package org.cru.godtools.domain.services;

import org.cru.godtools.domain.model.*;

import java.util.List;
import java.util.UUID;

/**
 * Created by ryancarlson on 4/30/14.
 */
public interface PageStructureService
{
	PageStructure selectById(UUID id);

	List<PageStructure> selectByTranslationId(UUID translationId);

	PageStructure selectByTranslationIdAndFilename(UUID translationId, String filename);

	void insert(PageStructure pageStructure);

	void update(PageStructure pageStructure);

	void setAutoCommit(boolean autoCommit);

	void rollback();
}