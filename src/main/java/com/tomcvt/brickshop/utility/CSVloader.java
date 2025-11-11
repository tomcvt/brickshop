package com.tomcvt.brickshop.utility;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.tomcvt.brickshop.dto.ProductInput;
import com.tomcvt.brickshop.service.ProductService;

@Component
@Profile({"dev", "demo"})
public class CSVloader {
    private final ProductService productService;
    private final String filePath;

    public CSVloader(ProductService productService,
            @Value("${csvloader.filepath}") String filePath) {
        this.productService = productService;
        this.filePath = filePath;
    }

    public void loadProductsFromCSV() {
        try {
            Files.lines(Paths.get(filePath)).forEach(line -> {
                String[] values = line.split(",");
                if (values.length == 4) {
                    String name = values[0].trim();
                    String description = values[1].trim();
                    BigDecimal price = new BigDecimal(values[2].trim());
                    int stock = Integer.parseInt(values[3].trim());
                    var prod = new ProductInput(name, description, price, stock);
                    productService.addProduct(prod);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
