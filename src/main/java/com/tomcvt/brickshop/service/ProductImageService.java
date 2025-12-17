package com.tomcvt.brickshop.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.tomcvt.brickshop.dto.ImageOrderDto;
import com.tomcvt.brickshop.exception.FileUploadException;
import com.tomcvt.brickshop.model.Product;
import com.tomcvt.brickshop.model.ProductImage;
import com.tomcvt.brickshop.repository.ProductImageRepository;
import com.tomcvt.brickshop.repository.ProductRepository;
import com.tomcvt.brickshop.utility.ImageConverter;


@Service
public class ProductImageService {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ProductImageService.class);
    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;
    private final ImageConverter imageConverter;

    @Value("${product.images.upload.dir}")
    String uploadDir;

    public ProductImageService(ProductImageRepository productImageRepository, ProductRepository productRepository,
            ImageConverter imageConverter) {
        this.productImageRepository = productImageRepository;
        this.productRepository = productRepository;
        this.imageConverter = imageConverter;
    }

    public void addProductImageDataToDb(Product product, Integer imgOrder, String imageUuid) {
        ProductImage productImage = new ProductImage(product, imgOrder, imageUuid);
        Product productUpdated = productRepository.findById(product.getId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + product.getId()));
        if (imgOrder == 1) {
            productUpdated.setThumbnailUuid(productImage.getImageUuid());
            productRepository.save(productUpdated);
        }
        productImageRepository.save(productImage);
    }
    @Transactional
    public boolean saveImageOrderForProductId(ImageOrderDto ioDto) {
        Product product = productRepository.findById(ioDto.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + ioDto.getProductId()));
        Map<UUID, Integer> uuids = new HashMap<>();
        List<ProductImage> images = productImageRepository.findAllByProductId(product.getId());
        for (int i = 0; i < ioDto.getImageUrls().size(); i++) {
            //log.info("Processing image URL: " + ioDto.getImageUrls().get(i) + " at position " + (i + 1));
            if (i == 0) {
                product.setThumbnailUuid(getUUIDFromFilename(ioDto.getImageUrls().get(i)));
                productRepository.save(product);
            }
            uuids.put(getUUIDFromFilename(ioDto.getImageUrls().get(i)), Integer.valueOf(i + 1));
        }
        for (int i = 0; i < ioDto.getImageUrls().size(); i++) {
            ProductImage pi = images.get(i);
            pi.setImgOrder(uuids.get(pi.getImageUuid()));
            productImageRepository.save(pi);
        }
        return true;
    }
    @Transactional
    public String saveProductImage(MultipartFile file, UUID productPublicId, Integer imageOrder) {
        Product product = productRepository.findByPublicId(productPublicId)
                .orElseThrow(() -> new FileUploadException("Product not found with id: " + productPublicId));
        return saveProductImage(file, product, imageOrder);
    }
    @Transactional
    public void saveProductImages(List<MultipartFile> files, Product product) {
        //int imageOrder = 1;
        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            try {
                ImageIO.read(file.getInputStream());
                log.warn("Available formats: " + String.join(", ", ImageIO.getReaderFormatNames())); // Check
            } catch (IOException e) {
                throw new FileUploadException("Invalid image file: " + file.getOriginalFilename());
            }
            Integer imageOrder = i + 1;
            Optional<ProductImage> existingImage = productImageRepository.findByProductIdAndImgOrder(product.getId(), imageOrder);
            String uuidData = null;
            if (existingImage.isPresent()) {
                uuidData = existingImage.get().getImageUuid().toString();
            } else {
                ProductImage newProductImage = new ProductImage();
                newProductImage.setProduct(product);
                newProductImage.setImgOrder(imageOrder);
                product.addProductImage(newProductImage);
                newProductImage = productImageRepository.save(newProductImage);
                uuidData = newProductImage.getImageUuid().toString();
            }
            if (imageOrder == 1) {
                product.setThumbnailUuid(UUID.fromString(uuidData));
                productRepository.save(product);
            }
            uuidData = uuidData + ".jpg";
            try {
                saveImage(file, uuidData);
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
    }


    @Transactional
    public String saveProductImage(MultipartFile file, Product product, Integer imageOrder) {
        Optional<ProductImage> existingImage = productImageRepository.findByProductIdAndImgOrder(product.getId(), imageOrder);
        String uuidData = null;
        //String ext = getFileExtension(file.getOriginalFilename());
        if (existingImage.isPresent()) {
            uuidData = existingImage.get().getImageUuid().toString();
            //System.out.println("Image exists with " + uuidData + ", overwriting");
        } else {
            ProductImage newProductImage = new ProductImage();
            newProductImage.setProduct(product);
            newProductImage.setImgOrder(imageOrder);
            product.addProductImage(newProductImage);
            newProductImage = productImageRepository.save(newProductImage);
            uuidData = newProductImage.getImageUuid().toString();
            //System.out.println("New image created with " + uuidData);
        }
        if (imageOrder == 1) {
            product.setThumbnailUuid(UUID.fromString(uuidData));
            productRepository.save(product);
        }
        // uuidData = uuidData + "." + ext;
        uuidData = uuidData + ".jpg";
        String filenameUuid = saveImage(file, uuidData);
        return filenameUuid;
    }


    //Method for saving image from file directly
    @Transactional
    public String saveProductImageFromFile(File file) {
        String[] parts = file.getName().split("_");
        if (parts.length != 2) {
            throw new FileUploadException("Filename format is incorrect. Expected format: <productId>_<imageOrder>");
        }
        Long productId = Long.parseLong(parts[0]);
        Integer imageOrder = Integer.parseInt(parts[1].split("\\.")[0]); //removing extension
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new FileUploadException("Product not found with id: " + productId));
        Optional<ProductImage> existingImage = productImageRepository.findByProductIdAndImgOrder(product.getId(), imageOrder);
        String uuidData = null;
        if (existingImage.isPresent()) {
            uuidData = existingImage.get().getImageUuid().toString();

            System.out.println("Image exists with " + uuidData + ", overwriting");
        } else {
            ProductImage newProductImage = new ProductImage();
            newProductImage.setProduct(product);
            newProductImage.setImgOrder(imageOrder);
            product.addProductImage(newProductImage);
            newProductImage = productImageRepository.save(newProductImage);
            uuidData = newProductImage.getImageUuid().toString();
            System.out.println("New image created with " + uuidData);
        }
        if (imageOrder == 1) {
            product.setThumbnailUuid(UUID.fromString(uuidData));
            productRepository.save(product);
        }
        uuidData = uuidData + ".jpg";
        String filenameUuid = saveImage(file, uuidData);
        return filenameUuid;
    }
    //TODO refactor to storageservice and saveon disk method

    private String saveImage(MultipartFile file, String filename) {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            try {
                Files.createDirectories(uploadPath);
            } catch (IOException e) {
                log.error("Could not create directory", e);
                throw new FileUploadException("Could not save file", e);
            }
        }
        try {
            //saving in configuration provided directory with provided filename
            File outputFile = new File(uploadPath.resolve(filename).toString());
            if (outputFile.exists()) {
                Files.delete(outputFile.toPath());
            }
            imageConverter.convertToJpg(file, outputFile, 0.8f);
            return filename;
        } catch (IOException e) {
            log.error("Could not save file", e);
            throw new FileUploadException("Could not save file", e);
        }
    }
    private String saveImage(File file, String filename) {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            try {
                Files.createDirectories(uploadPath);
            } catch (IOException e) {
                throw new FileUploadException("Could not save file", e);
            }
        }
        try {
            //saving in configuration provided directory with provided filename
            File outputFile = new File(uploadPath.resolve(filename).toString());
            if (outputFile.exists()) {
                Files.delete(outputFile.toPath());
            }
            imageConverter.convertToJpg(file, outputFile, 0.8f);
            return filename;
        } catch (IOException e) {
            throw new FileUploadException("Could not save file", e);
        }
    }

    public static String getFileExtension(String filename) {
        if (filename == null) {
            return null;
        }
        int dotIndex = filename.lastIndexOf(".");
        if (dotIndex >= 0) {
            return filename.substring(dotIndex + 1);
        }
        return "";
    }

    public static UUID getUUIDFromFilename(String filename) {
        if (filename == null) {
            return null;
        }
        int dotIndex = filename.lastIndexOf(".");
        String name = filename.substring(0, dotIndex);
        return UUID.fromString(name);
    }

}
