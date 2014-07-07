package org.cru.godtools.domain.images;

import java.awt.image.BufferedImage;

/**
 * Created by ryancarlson on 4/25/14.
 */
public interface ImageLookup
{
	BufferedImage findByFilename(String filename);
}
