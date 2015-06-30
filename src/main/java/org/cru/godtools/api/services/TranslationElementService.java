package org.cru.godtools.api.services;

import org.cru.godtools.domain.packages.*;
import org.sql2o.Connection;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

/**
 * Created by ryancarlson on 4/30/14.
 */
public interface TranslationElementService
{
	List<TranslationElement> selectByTranslationId(UUID translationId, String ... orderByFields);

	List<TranslationElement> selectByTranslationIdPageStructureId(UUID translationId, UUID pageStructureId);

	TranslationElement selectyByIdTranslationId(UUID id, UUID translationId);

	void insert(TranslationElement translationElement);

	void update(TranslationElement translationElement);

	Connection getSqlConnection();

}
