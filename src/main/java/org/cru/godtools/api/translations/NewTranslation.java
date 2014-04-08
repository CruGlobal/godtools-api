package org.cru.godtools.api.translations;

import com.google.common.base.Throwables;
import com.google.common.collect.ForwardingMap;
import com.google.common.collect.Maps;
import org.cru.godtools.api.packages.utils.NonClosingZipInputStream;
import org.cru.godtools.api.packages.utils.XmlDocumentStreamConverter;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.w3c.dom.Document;
import sun.awt.X11.XDragSourceContextPeer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


/**
 * Created by ryancarlson on 4/3/14.
 */
public class NewTranslation extends ForwardingMap<String, Document>
{
	Document packageFile;
	Map<String, Document> pageXmlFiles = Maps.newHashMap();

	public NewTranslation(InputPart inputPart)
	{
		try
		{
			extractXmlDocumentsFromZip(getZipInputStream(inputPart));
		}
		catch(IOException e)
		{
			Throwables.propagate(e);
		}
	}

	@Override
	protected Map<String, Document> delegate()
	{
		return pageXmlFiles;
	}

	public Document getPackageFile()
	{
		return packageFile;
	}

	private InputStream getZipInputStream(InputPart inputPart) throws IOException
	{
		return inputPart.getBody(InputStream.class, null);
	}

	private void extractXmlDocumentsFromZip(InputStream inputStream) throws IOException
	{
		/*wrap the ZipInputStream instance b/c the call inside XmlDocumentStreamConverter will close it before we're ready for it to be closed*/
		NonClosingZipInputStream safeZipInputStream = new NonClosingZipInputStream(new ZipInputStream(inputStream));

		ZipEntry zipEntry = safeZipInputStream.getNextEntry();

		while(zipEntry != null)
		{
			String filename = zipEntry.getName();

			if(filename.contains("/"))
			{
				pageXmlFiles.put(filename, XmlDocumentStreamConverter.streamToXml(safeZipInputStream));
			}
			else
			{
				packageFile = XmlDocumentStreamConverter.streamToXml(safeZipInputStream);
			}

			safeZipInputStream.closeEntry();
			zipEntry = safeZipInputStream.getNextEntry();
		}

		safeZipInputStream.forceClose();
	}
}

