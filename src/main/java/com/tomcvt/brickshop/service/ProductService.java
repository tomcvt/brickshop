package com.tomcvt.brickshop.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.tomcvt.brickshop.dto.ImageOrderDto;
import com.tomcvt.brickshop.dto.NewProductInput;
import com.tomcvt.brickshop.dto.NewProductWithHtmlInput;
import com.tomcvt.brickshop.dto.ProductDto;
import com.tomcvt.brickshop.dto.ProductHtmlDto;
import com.tomcvt.brickshop.dto.ProductInput;
import com.tomcvt.brickshop.dto.ProductSummaryDto;
import com.tomcvt.brickshop.exception.EntityAlreadyExists;
import com.tomcvt.brickshop.exception.ProductNotFoundException;
import com.tomcvt.brickshop.model.Category;
import com.tomcvt.brickshop.model.Product;
import com.tomcvt.brickshop.pagination.SimplePage;
import com.tomcvt.brickshop.repository.CategoryRepository;
import com.tomcvt.brickshop.repository.ProductRepository;
import com.tomcvt.brickshop.utility.CategoryReferenceMap;
import com.tomcvt.brickshop.utility.HtmlPolicies;



@Service
public class ProductService {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ProductService.class);
    private final ProductRepository productRepository;
    private final ProductImageService productImageService;
    private final CategoryRepository categoryRepository;
    private final CategoryReferenceMap categoryReferenceMap;

    public ProductService(ProductRepository productRepository, ProductImageService productImageService,
            CategoryReferenceMap categoryReferenceMap, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.productImageService = productImageService;
        this.categoryReferenceMap = categoryReferenceMap;
        this.categoryRepository = categoryRepository;
    }
    //TODO refactor to dto
    public Product addProduct(ProductInput input) {
        Optional<Product> existing = productRepository.findByName(input.name());
        if (existing.isPresent())
            throw new EntityAlreadyExists("Product with that name exists");
        Product product = new Product();
        product.setName(input.name());
        product.setDescription(input.description());
        product.setPrice(input.price());
        product.setStock(input.stock());
        return productRepository.save(product);
    }
    @Transactional
    public Product addProductWithImages(NewProductInput input, List<MultipartFile> images) {
        Optional<Product> existing = productRepository.findByName(input.name());
        if (existing.isPresent())
            log.warn("Product with name {} already exists", input.name());
        Product product = new Product();
        product.setName(input.name());
        product.setDescription(input.description());
        product.setPrice(input.price());
        product.setStock(input.stock());
        Set<Category> categories = categoryReferenceMap.getIds(input.categories()).stream()
                .map(id -> categoryRepository.getReferenceById(id)).collect(Collectors.toSet());
        product.setCategories(categories);
        product = productRepository.save(product);
        productImageService.saveProductImages(images, product);
        return productRepository.save(product);
    }

    @Transactional
    public Product addProductWithHtmlDescriptionAndImages(NewProductWithHtmlInput input, List<MultipartFile> images) {
        Optional<Product> existing = productRepository.findByName(input.name());
        if (existing.isPresent())
            log.warn("Product with name {} already exists", input.name());
        Product product = new Product();
        String safeHtml = HtmlPolicies.sanitizeHtmlV1(input.htmlDescription());
        product.setHtmlDescription(safeHtml);
        product.setName(input.name());
        product.setDescription(input.description());
        product.setPrice(input.price());
        product.setStock(input.stock());
        Set<Category> categories = categoryReferenceMap.getIds(input.categories()).stream()
                .map(id -> categoryRepository.getReferenceById(id)).collect(Collectors.toSet());
        product.setCategories(categories);
        product = productRepository.save(product);
        productImageService.saveProductImages(images, product);
        return productRepository.save(product);
    }

    public List<ProductSummaryDto> getProductSummariesNoPicByKeyword(String keyword) {
        return productRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword)
                .stream().map(Product::toSummaryDto).toList();
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product not found"));
    }

    public Product getProductByPublicId(UUID publicId) {
        return productRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));
    }

    @Transactional(readOnly = true)
    public ProductDto getProductDtoByPublicId(UUID publicId) {
        Product product = getProductByPublicId(publicId);
        return product.toDto();
    }

    public List<ProductSummaryDto> getAllProductSummaries() {
        return productRepository.findAll().stream()
                .map(Product::toSummaryDto).toList();
    }
    //TODO add sorting options (for starters by price)
    public SimplePage<ProductSummaryDto> getProductSummariesByPage(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        return SimplePage.fromPage(productRepository.findAll(pageable).map(Product::toSummaryDto));
    }

    public SimplePage<ProductSummaryDto> getProductSummariesByCategoriesAndPage(List<String> categories, Integer page,
            Integer size) {
        List<Long> categoryIds = categoryReferenceMap.getIdsList(categories);
        Pageable pageable = PageRequest.of(page, size);
        if (categoryIds.isEmpty()) {
            Page<Product> productPage = productRepository.findAll(pageable);
            return SimplePage.fromPage(productPage.map(Product::toSummaryDto));
        }
        Page<Long> productIds = productRepository.findIdsByCategoryIds(categoryIds, pageable);
        List<Product> products = productRepository.findByIds(productIds.getContent());
        return SimplePage.of(products, productIds).map(Product::toSummaryDto);
    }

    public SimplePage<ProductSummaryDto> getProductSummariesByKeywordAndCategoriesAndPage(String keyword,
            List<String> categories, Integer page, Integer size) {
        if (categories == null || categories.isEmpty()) {
            return getProductSummariesByKeywordAndPage(keyword, page, size);
        }
        List<Long> categoryIds = categoryReferenceMap.getIdsList(categories);
        Pageable pageable = PageRequest.of(page, size);
        Page<Long> productIds = productRepository.findIdsByCategoryIdsAndKeyword(categoryIds, keyword, pageable);
        List<Product> products = productRepository.findByIds(productIds.getContent());
        return SimplePage.of(products, productIds).map(Product::toSummaryDto);
    }

    public SimplePage<ProductSummaryDto> getProductSummariesByKeywordAndPage(String keyword, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productRepository.findByNameOrDescriptionContaining(keyword, pageable);
        return SimplePage.fromPage(productPage.map(Product::toSummaryDto));

    }
    public Product getProductHydratedByPublicId(UUID publicId) {
        return productRepository.findByPublicIdHydrated(publicId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));
    }

    @Transactional
    public void editProductFromDto(ProductHtmlDto productDto) {
        Product product = productRepository.findByPublicId(productDto.publicId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        if (productDto.htmlDescription() != null && !productDto.htmlDescription().isEmpty()) {
            String safeHtml = HtmlPolicies.sanitizeHtmlV1(productDto.htmlDescription());
            product.setHtmlDescription(safeHtml);
        } else {
            product.setHtmlDescription(null);
        }
        product.setName(productDto.name());
        product.setDescription(productDto.description());
        product.setPrice(productDto.price());
        product.setStock(productDto.stock());
        Set<Category> oldCategories = product.getCategories();
        Set<Category> newCategories = categoryReferenceMap.getIds(productDto.categoriesNames()).stream()
                .map(id -> categoryRepository.getReferenceById(id)).collect(Collectors.toSet());
        if (!oldCategories.equals(newCategories)) {
            product.setCategories(newCategories);
        }
        productRepository.save(product);
        //TODO refactor to not use dto here
        productImageService.saveImageOrderForProductId(
                new ImageOrderDto(product.getId(), productDto.imageUrls()));
    }
    //TODO validate categories
    public long getTotalProductCount() {
        return productRepository.count();
    }
}
