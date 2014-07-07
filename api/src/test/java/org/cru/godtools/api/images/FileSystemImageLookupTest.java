package org.cru.godtools.api.images;

import org.testng.annotations.Test;

/**
 * Created by ryancarlson on 4/25/14.
 */
public class FileSystemImageLookupTest
{

	@Test
	public void testFindBridged()
	{
		FileSystemImageLookup imageLookup = new FileSystemImageLookup("/data/SnuffyPackages/kgp");
		imageLookup.findByFilename("Bridged.png");

	}

}
