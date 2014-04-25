package org.cru.godtools.api.packages;

import com.google.common.base.Throwables;
import org.cru.godtools.api.packages.utils.XmlDocumentStreamConverter;
import org.w3c.dom.Document;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;

/**
 * Created by ryancarlson on 4/25/14.
 */
public class FileSystemPageLookup implements PageLookup
{
	private File packagePageDirectory;

	public FileSystemPageLookup(String packagePath)
	{
		try
		{
			packagePageDirectory = new File(this.getClass().getResource(packagePath ).toURI());
		}
		catch(Exception e)
		{
			Throwables.propagate(e);
		}
	}

	@Override
	public Document findByFilename(String filename)
	{
		File foundFile = searchPackageDirectory(filename);

		if(foundFile == null) return null;

		try
		{
			return XmlDocumentStreamConverter.streamToXml(new FileInputStream(foundFile));
		}
		catch(Exception e)
		{
			Throwables.propagate(e);
			return null;
		}
	}

	private File searchPackageDirectory(final String filename)
	{
		File[] matchingFiles = packagePageDirectory.listFiles(getFilenameFilter(filename));

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
