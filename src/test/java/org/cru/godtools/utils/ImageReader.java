package org.cru.godtools.utils;

import com.google.common.base.Throwables;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * Created by ryancarlson on 3/21/14.
 */
public class ImageReader
{

	public static byte[] read(String filePath)
	{
		try
		{
			return read(new File(ImageReader.class.getResource(filePath).toURI()));
		}
		catch(Exception e)
		{
			Throwables.propagate(e);
			return null; /*unreachable*/
		}
	}

    public static byte[] read(File imageFile)
    {
		try
		{
			BufferedImage bufferedImage = ImageIO.read(imageFile);

			ByteArrayOutputStream bos = new ByteArrayOutputStream();

			ImageIO.write(bufferedImage, "png", bos);
			bos.flush();

			return bos.toByteArray();
		}
		catch(IOException e)
		{
			Throwables.propagate(e);
			return null;
		}
    }
}
