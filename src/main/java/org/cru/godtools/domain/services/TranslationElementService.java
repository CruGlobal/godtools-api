package org.cru.godtools.domain.services;

import org.cru.godtools.domain.model.*;

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

	void setAutoCommit(boolean autoCommit);

	void rollback();

}