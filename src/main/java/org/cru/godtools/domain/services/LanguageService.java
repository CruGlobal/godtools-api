package org.cru.godtools.domain.services;

import org.cru.godtools.domain.languages.*;
import org.cru.godtools.domain.model.*;

import java.util.List;
import java.util.UUID;

/**
 * Created by ryancarlson on 3/20/14.
 */
public interface LanguageService
{
    Language getOrCreateLanguage(LanguageCode languageCode);

    List<Language> selectAllLanguages();

    Language selectLanguageById(UUID id);

    Language selectByLanguageCode(LanguageCode languageCode);

    boolean languageExists(Language language);

    void insert(Language language);

    void setAutoCommit(boolean autoCommit);

    void rollback();
}
