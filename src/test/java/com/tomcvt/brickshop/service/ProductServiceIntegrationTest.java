package com.tomcvt.brickshop.service;

import java.io.InputStream;
import java.util.List;
import java.util.Set;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.tomcvt.brickshop.dto.NewProductInput;

// Removed incorrect import for @Container annotation
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.containers.PostgreSQLContainer;

//@Disabled("Temporarily disabled for troubleshooting")
@TestPropertySource(properties = {
        "product.images.upload.dir=target/test-uploads"
})
@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
public class ProductServiceIntegrationTest {

    @Container
    @SuppressWarnings("resource")
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:17")
            .withDatabaseName("brickshop-test-db")
            .withUsername("brickshop-test-user")
            .withPassword("brickshop-test-pass");

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Autowired
    private Flyway flyway;

    @BeforeEach
    public void setUp() {
        flyway.clean();
        flyway.migrate();
    }

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Test
    void debugDockerEnv() {
        System.getenv().forEach((k, v) -> {
            if (k.contains("DOCKER")) {
                System.out.println(k + "=" + v);
            }
        });
    }

    @Test
    @Transactional
    public void testCreateAndRetrieveProduct() {
        NewProductInput newProduct = new NewProductInput("Test Product", "A product for testing",
                java.math.BigDecimal.valueOf(19.99), 100, Set.of("TestCategory1", "TestCategory2"));

        var category1 = categoryService.addCategory("TestCategory1");
        var category2 = categoryService.addCategory("TestCategory2");
        InputStream is = getClass().getResourceAsStream("/dummy.jpg");
        InputStream is2 = getClass().getResourceAsStream("/dummy.jpg");
        MockMultipartFile imageFile1;
        MockMultipartFile imageFile2;
        try {
            imageFile1 = new MockMultipartFile(
                "image",
                "test-image.jpg",
                "image/jpeg",
                is);
            imageFile2 = new MockMultipartFile(
                "image",
                "test-image2.jpg",
                "image/jpeg",
                is2);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load test image", e);
        }
        
        List<MultipartFile> imageFiles = List.of(imageFile1, imageFile2);

        var createdProduct = productService.addProductWithImages(newProduct, imageFiles);

        var retrievedProduct = productService.getProductById(createdProduct.getId());

        assert retrievedProduct.getName().equals(newProduct.name());
        assert retrievedProduct.getDescription().equals(newProduct.description());
        assert retrievedProduct.getPrice().equals(newProduct.price());
        assert retrievedProduct.getStock() == newProduct.stock();
        assert retrievedProduct.getCategories().size() == 2;
        assert retrievedProduct.getProductImages().size() == 2;

    }

    @AfterEach
    public void cleanUpUploads() throws Exception {
        java.nio.file.Path uploadDir = java.nio.file.Paths.get("target/test-uploads");
        if (java.nio.file.Files.exists(uploadDir)) {
            try (var files = java.nio.file.Files.walk(uploadDir)) {
                files.sorted(java.util.Comparator.reverseOrder())
                        .map(java.nio.file.Path::toFile)
                        .forEach(java.io.File::delete);
            }
        }
    }

}
