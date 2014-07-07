package org.cru.godtools.api.images;

import com.google.common.base.Throwables;
import org.apache.log4j.Logger;
import org.cru.godtools.domain.images.ImageLookup;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by ryancarlson on 4/25/14.
 */
public class FileSystemImageLookup implements ImageLookup
{
	private final String SHARED_IMAGE_DIRECTORY_PATH = "/data/SnuffyPackages/shared";

	private File SHARED_IMAGE_DIRECTORY;
	private File packageImageDirectory;

	public FileSystemImageLookup(String packagePath)
	{
		try
		{
			SHARED_IMAGE_DIRECTORY = new File(this.getClass().getResource(SHARED_IMAGE_DIRECTORY_PATH).toURI());
			packageImageDirectory = new File(this.getClass().getResource(packagePath + "/shared").toURI());
		}
		catch(Exception e)
		{
			Throwables.propagate(e);
		}
	}




	@Override
	public BufferedImage findByFilename(String filename)
	{
		Logger logger = Logger.getLogger(this.getClass());

		try
		{
			logger.info("Looking for image: " + filename);
			logger.info("Searching package directory...");

			File imageFile = searchPackageDirectory(filename);

			if (imageFile == null)
			{
				logger.info("Not found.  Trying shared directory...");
				imageFile = searchSharedDirectory(filename);
			}

			if (imageFile != null)
			{
				logger.info("Found!");
				return ImageIO.read(imageFile);
			}
		}
		catch(Exception e)
		{
			logger.error("Error!  Caught exception", e);
			Throwables.propagate(e);

		}
		logger.warn("Not found on filesystem...");
		return null;
	}

	private File searchPackageDirectory(final String filename)
	{
		File[] matchingFiles = packageImageDirectory.listFiles(getFilenameFilter(filename));

		if(matchingFiles.length == 1)
		{
			return matchingFiles[0];
		}
		else return null;
	}

	private File searchSharedDirectory(final String filename)
	{
		File[] matchingFiles = SHARED_IMAGE_DIRECTORY.listFiles(getFilenameFilter(filename));

		if(matchingFiles.length == 1)
		{
			return matchingFiles[0];
		}
		else return null;
	}

	private FilenameFilter getFilenameFilter(final String filename)
	{
		return new FilenameFilter()
		{
			@Override
			public boolean accept(File dir, String name)
			{
				return name.equalsIgnoreCase(filename);
			}
		};
	}
}
