package com.tomcvt.brickshop.zdemo;

import java.io.File;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.tomcvt.brickshop.repository.ProductRepository;

@Component
@Profile({ "dev", "demo" })
public class DemoHtmlDescriptions {
    private final ProductRepository productRepository;
    private final String pathOfHtmlDescriptions;
    private final Pattern pattern = Pattern.compile("^html-desc-[.*]\\.txt$");

    public DemoHtmlDescriptions(ProductRepository productRepository,
            @Value("${html-description-loader.path}") String pathOfHtmlDescriptions) {
        this.productRepository = productRepository;
        this.pathOfHtmlDescriptions = pathOfHtmlDescriptions;
    }

    public void uploadHtmlDescriptions() {
        File dir = new File(pathOfHtmlDescriptions);
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    String fileName = file.getName();
                    if (pattern.matcher(fileName).matches()) {
                        String number = pattern.matcher(fileName).group(1);
                        Long productId = Long.parseLong(number);
                        productRepository.findById(productId).ifPresent(product -> {
                            try {
                                String htmlDescription = new String(java.nio.file.Files.readAllBytes(file.toPath()));
                                if (htmlDescription == null || htmlDescription.trim().isEmpty()) {
                                    product.setHtmlDescription(htmlDescription);
                                    productRepository.save(product);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    }
                }
            }
        }
    }
}
