package org.cru.godtools.migration;

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

    public static byte[] read(File imageFile) throws IOException
    {
        BufferedImage bufferedImage = ImageIO.read(imageFile);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        ImageIO.write(bufferedImage, "png", bos);
        bos.flush();

        return bos.toByteArray();
    }
}
