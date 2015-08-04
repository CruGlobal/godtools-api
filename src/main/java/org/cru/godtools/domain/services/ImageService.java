package org.cru.godtools.domain.services;

import org.cru.godtools.domain.model.*;

import java.util.UUID;

/**
 * Created by ryancarlson on 3/21/14.
 */
public interface ImageService
{
    Image selectById(UUID id);

    Image selectByFilename(String filename);

    void update(Image image);

    void insert(Image image);

    void setAutoCommit(boolean autoCommit);

    void rollback();

}
