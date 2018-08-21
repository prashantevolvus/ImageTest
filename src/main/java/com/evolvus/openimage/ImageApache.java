/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.evolvus.openimage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.imaging.ImageFormat;
import org.apache.commons.imaging.ImageFormats;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.ImagingConstants;
import org.apache.commons.imaging.formats.tiff.constants.TiffConstants;

/**
 *
 * @author Prashant Maroli @ Evolvus
 */
public class ImageApache {

    public static void main(String[] args) {
        try {
            final BufferedImage image = Imaging.getBufferedImage(new File("photo.jpg"));

            final ImageFormat format = ImageFormats.TIFF;
            final Map<String, Object> params = new HashMap<>();
            Object put = params.put(ImagingConstants.PARAM_KEY_COMPRESSION, TiffConstants.TIFF_COMPRESSION_CCITT_1D);

            Imaging.writeImage(image, new File("outputuc21.tiff"), format, params);

        } catch (ImageReadException | IOException | ImageWriteException ex) {
            Logger.getLogger(ImageApache.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
