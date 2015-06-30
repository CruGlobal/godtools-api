package org.cru.godtools.api.services;

import org.cru.godtools.domain.images.*;
import org.sql2o.Connection;

import javax.inject.Inject;
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

    Connection getSqlConnection();

}
