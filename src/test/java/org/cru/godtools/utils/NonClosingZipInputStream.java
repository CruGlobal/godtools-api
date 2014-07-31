package org.cru.godtools.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * A ZipInputStream which doesn't close when the default close method is called.  This is to
 * get around the fact that javax.xml.parsers.DocumentBuilder closes the stream when reading
 * a portion of it (one XML entry) and there are more entries to be read.
 *
 * Calling forceClose() will actually close the stream.
 *
 * Created by ryancarlson on 7/31/14.
 */
public class NonClosingZipInputStream extends InputStream
{
	ZipInputStream zipInputStream;

	public NonClosingZipInputStream(ZipInputStream zipInputStream)
	{
		this.zipInputStream = zipInputStream;
	}

	public void forceClose() throws IOException
	{
		zipInputStream.close();
	}

	@Override
	public int read(byte[] b) throws IOException
	{
		return zipInputStream.read(b);
	}

	public ZipEntry getNextEntry() throws IOException
	{
		return zipInputStream.getNextEntry();
	}

	public void closeEntry() throws IOException
	{
		zipInputStream.closeEntry();
	}

	@Override
	public int available() throws IOException
	{
		return zipInputStream.available();
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException
	{
		return zipInputStream.read(b, off, len);
	}

	@Override
	public long skip(long n) throws IOException
	{
		return zipInputStream.skip(n);
	}

	@Override
	public void close() throws IOException
	{
		// intentionally don't close
	}

	@Override
	public int read() throws IOException
	{
		return zipInputStream.read();
	}

	@Override
	public boolean markSupported()
	{
		return zipInputStream.markSupported();
	}

	@Override
	public void mark(int readlimit)
	{
		zipInputStream.mark(readlimit);
	}

	@Override
	public void reset() throws IOException
	{
		zipInputStream.reset();
	}
}
