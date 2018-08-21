/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.evolvus.openimage;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

/**
 *
 * @author Prashant Maroli @ Evolvus
 */
public class ImageMonkey {

    public static void main(String[] args) {
        try {
            BufferedImage image = ImageIO.read(new File("photo.jpg"));
            BufferedImage grayScalBuffImg = new BufferedImage(image.getWidth(null), image.getHeight(null),
                    BufferedImage.TYPE_BYTE_GRAY);
            final Graphics2D g = (Graphics2D) grayScalBuffImg.getGraphics();
            g.drawImage(image, 0, 0, null);
            g.dispose();

            File jpg = new File("output.jpeg");
            ImageWriter writer = ImageIO.getImageWritersByFormatName("JPEG").next();
            ImageWriteParam writeParam = writer.getDefaultWriteParam();
            writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            //writeParam.setCompressionType("JPEG"); //This line does what it's supposed to
            //writeParam.setCompressionQuality(0.3f); //This line does not seem to do anything

            final ImageOutputStream jpgOutStream
                    = ImageIO.createImageOutputStream(new FileOutputStream(jpg));
            writer.setOutput(jpgOutStream);
            writer.write(null, new IIOImage(grayScalBuffImg, null, null), writeParam);

            // ImageIO.write(grayScalBuffImg, "JPEG", new File("output.jpg"));
            // ImageIO.write(grayScalBuffImg, "TIFF", new File("output.tiff"));
        } catch (IOException ex) {
            Logger.getLogger(ImageMonkey.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
