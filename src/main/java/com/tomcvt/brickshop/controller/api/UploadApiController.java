package com.tomcvt.brickshop.controller.api;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.tomcvt.brickshop.dto.ErrorResponse;
import com.tomcvt.brickshop.service.ProductImageService;

@RestController
@PreAuthorize("hasAnyRole('ADMIN','SUPERUSER')")
@RequestMapping("/api/upload")
public class UploadApiController {

    private final ProductImageService productImageService;
    public UploadApiController(ProductImageService productImageService) {
        this.productImageService = productImageService;
    }

    @Value("${product.images.upload.dir}")
    String uploadDir;

    @PostMapping("/product")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file,
            @RequestParam("publicId") UUID publicId,
            @RequestParam("imageOrder") Integer imageOrder) {
        if (file.isEmpty()) {
            return ResponseEntity.status(400).body(new ErrorResponse("BAD_REQUEST", "Uploaded file is empty"));
        }
        String filename = productImageService.saveProductImage(file, publicId, imageOrder);
        return ResponseEntity.ok(filename);
    }

}
