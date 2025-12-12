package com.tomcvt.brickshop.utility;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.*;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Iterator;

@Component
public class ImageConverter {
    //TODO make static methods
    public void convertToJpg(InputStream inputStream, File output, float quality) throws IOException {
        // add try catch and logginf=g
        BufferedImage source = ImageIO.read(inputStream);

        BufferedImage converted = new BufferedImage(
                source.getWidth(),
                source.getHeight(),
                BufferedImage.TYPE_INT_RGB);

        Graphics2D g = converted.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, source.getWidth(), source.getHeight());
        g.drawImage(source, 0, 0, null);
        g.dispose();

        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
        ImageWriter writer = writers.next();
        ImageOutputStream ios = ImageIO.createImageOutputStream(output);
        writer.setOutput(ios);

        ImageWriteParam param = writer.getDefaultWriteParam();
        if (param.canWriteCompressed()) {
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(quality);
        }

        writer.write(null, new IIOImage(converted, null, null), param);

        ios.close();
        writer.dispose();
    }

    public void convertToJpg(MultipartFile input, File output, float quality) throws IOException {
        try (InputStream inputStream = input.getInputStream()) {
            convertToJpg(inputStream, output, quality);
        }
    }
    public void convertToJpg(File input, File output, float quality) throws IOException {
        try (InputStream inputStream = Files.newInputStream(input.toPath())) {
            convertToJpg(inputStream, output, quality);
        }
    }
}
