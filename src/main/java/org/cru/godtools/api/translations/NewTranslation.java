package org.cru.godtools.api.translations;

import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import org.cru.godtools.api.packages.utils.NonClosingZipInputStream;
import org.cru.godtools.api.packages.utils.XmlDocumentStreamConverter;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.w3c.dom.Document;

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
public class NewTranslation implements Map<String,Document>
{
	Map<String, Document> files;

	public NewTranslation(InputPart inputPart)
	{
		files = Maps.newHashMap();

		try
		{
			extractXmlDocumentsFromZip(getZipInputStream(inputPart));
		}
		catch(IOException e)
		{
			Throwables.propagate(e);
		}
	}

	private InputStream getZipInputStream(InputPart inputPart) throws IOException
	{
		return inputPart.getBody(InputStream.class, null);
	}

	private void extractXmlDocumentsFromZip(InputStream inputStream) throws IOException
	{
		/*wrap the ZipInputStream instance b/c the call inside XmlDocumentStreamCoverter will close it before we're ready for it to be closed*/
		NonClosingZipInputStream safeZipInputStream = new NonClosingZipInputStream(new ZipInputStream(inputStream));

		ZipEntry zipEntry = safeZipInputStream.getNextEntry();

		while(zipEntry != null)
		{
			String filename = zipEntry.getName();

			files.put(filename, XmlDocumentStreamConverter.streamToXml(safeZipInputStream));

			safeZipInputStream.closeEntry();
			zipEntry = safeZipInputStream.getNextEntry();
		}

		safeZipInputStream.forceClose();
	}

	@Override
	public int size()
	{
		return files.size();
	}

	@Override
	public boolean isEmpty()
	{
		return files.isEmpty();
	}

	@Override
	public boolean containsKey(Object key)
	{
		return files.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value)
	{
		return files.containsValue(value);
	}

	@Override
	public Document get(Object key)
	{
		return files.get(key);
	}

	@Override
	public Document put(String key, Document value)
	{
		return files.put(key, value);
	}

	@Override
	public Document remove(Object key)
	{
		return files.remove(key);
	}

	@Override
	public void putAll(Map<? extends String, ? extends Document> m)
	{
		files.putAll(m);
	}

	@Override
	public void clear()
	{
		files.clear();
	}

	@Override
	public Set<String> keySet()
	{
		return files.keySet();
	}

	@Override
	public Collection<Document> values()
	{
		return files.values();
	}

	@Override
	public Set<Entry<String, Document>> entrySet()
	{
		return files.entrySet();
	}

	@Override
	public boolean equals(Object o)
	{
		return files.equals(o);
	}

	@Override
	public int hashCode()
	{
		return files.hashCode();
	}
}

