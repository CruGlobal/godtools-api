package org.cru.godtools.api.packages;

import org.w3c.dom.Document;

/**
 * Created by ryancarlson on 4/25/14.
 */
public interface PageLookup
{
	Document findByFilename(String filename);
}
