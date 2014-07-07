package org.cru.godtools.api.packages.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by ryancarlson on 4/3/14.
 */
public class NonClosingZipInputStream extends InputStream
{
	ZipInputStream zipInputStream;

	public NonClosingZipInputStream(ZipInputStream zipInputStream)
	{
		this.zipInputStream = zipInputStream;
	}


	/**
	 * Forceably close the input stream
	 */
	public void forceClose() throws IOException
	{
		zipInputStream.close();
	}

	/**
	 * Don't actually close the input stream in this implementation
	 * @throws IOException
	 */
	public void close() throws IOException
	{
		/*do nothing*/
	}




	/* Standard delegate methods are below: */


	public ZipEntry getNextEntry() throws IOException
	{
		return zipInputStream.getNextEntry();
	}

	public void mark(int readlimit)
	{
		zipInputStream.mark(readlimit);
	}

	public void reset() throws IOException
	{
		zipInputStream.reset();
	}

	public int available() throws IOException
	{
		return zipInputStream.available();
	}

	public int read(byte[] b) throws IOException
	{
		return zipInputStream.read(b);
	}

	public int read() throws IOException
	{
		return zipInputStream.read();
	}

	public long skip(long n) throws IOException
	{
		return zipInputStream.skip(n);
	}

	public void closeEntry() throws IOException
	{
		zipInputStream.closeEntry();
	}

	public boolean markSupported()
	{
		return zipInputStream.markSupported();
	}

	public int read(byte[] b, int off, int len) throws IOException
	{
		return zipInputStream.read(b, off, len);
	}
}
