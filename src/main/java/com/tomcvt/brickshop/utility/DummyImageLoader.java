package com.tomcvt.brickshop.utility;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.tomcvt.brickshop.service.ProductImageService;

@Component
@Profile({"dev", "demo"})
public class DummyImageLoader {
    private final ProductImageService productImageService;
    private final String dummyImagesPath;
    //TODO configure path with variable

    public DummyImageLoader(ProductImageService productImageService, 
            @Value("${dummyimageloader.path}") String dummyImagesPath) {
        this.productImageService = productImageService;
        this.dummyImagesPath = dummyImagesPath;
    }

    public void loadDummyImages() {
        File dir = new File(dummyImagesPath);
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                try {
                    productImageService.saveProductImageFromFile(file);
                } catch (Exception e) {
                    System.err.println("Failed to load image: " + file.getName());
                    e.printStackTrace();
                }
            }
        }
    }
}
