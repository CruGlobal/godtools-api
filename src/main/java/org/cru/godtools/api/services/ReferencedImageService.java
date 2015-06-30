package org.cru.godtools.api.services;

import org.cru.godtools.domain.images.*;
import java.util.List;
import java.util.UUID;

/**
 * Created by ryancarlson on 4/25/14.
 */
public interface ReferencedImageService
{
	List<ReferencedImage> selectByPackageStructureId(UUID packageStructureId);

	List<ReferencedImage> selectByPackageStructureId(UUID packageStructureId, boolean filter);

	void insert(ReferencedImage referencedImage);

}
