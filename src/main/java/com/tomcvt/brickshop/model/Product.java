package com.tomcvt.brickshop.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.tomcvt.brickshop.dto.ProductDto;
import com.tomcvt.brickshop.dto.ProductSummaryDto;

import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Product {
    private static final int MAX_NAME_LENGTH = 100;
    private static final int MAX_DESCRIPTION_LENGTH = 500;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private UUID publicId = UUID.randomUUID();
    @Column(length = MAX_NAME_LENGTH, nullable = false)
    private String name;
    @Column(length = MAX_DESCRIPTION_LENGTH)
    private String description;
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("imgOrder ASC")
    private List<ProductImage> productImages = new ArrayList<>();
    private BigDecimal price;
    private int stock;
    private UUID thumbnailUuid;
    @ManyToMany
    @JoinTable(
        name = "category_products",
        joinColumns = @JoinColumn(name = "product_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @Fetch(FetchMode.SUBSELECT)
    private Set<Category> categories;

    public Product() {}

    //TODO refactor price to string in constructor
    public Product(String name, String description, double price, int stock) {
        this.name = name;
        this.description = description;
        this.price = BigDecimal.valueOf(price);
        this.stock = stock;
        this.thumbnailUuid = null;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public UUID getPublicId() {
        return publicId;
    }
    public void setPublicId(UUID publicId) {
        this.publicId = publicId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public List<ProductImage> getProductImages() {
        return productImages;
    }
    public void setProductImages(List<ProductImage> productImages) {
        this.productImages = productImages;
    }
    public void addProductImage(ProductImage image) {
        productImages.add(image);
        image.setProduct(this);
    }
    public void removeProductImage(ProductImage image) {
        productImages.remove(image);
        image.setProduct(null);
    }
    public BigDecimal getPrice() {
        return price;
    }
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    public int getStock() {
        return stock;
    }
    public void setStock(int stock) {
        this.stock = stock;
    }
    public UUID getThumbnailUuid() {
        return thumbnailUuid;
    }
    public void setThumbnailUuid(UUID thumbnailUuid) {
        this.thumbnailUuid = thumbnailUuid;
    }
    public Set<Category> getCategories() {
        return categories;
    }
    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }
    public void addCategory(Category category) {
        this.categories.add(category);
    }
    public void removeCategory(Category category) {
        this.categories.remove(category);
    }

    public ProductSummaryDto toSummaryDto() {
        return new ProductSummaryDto(this.publicId, this.name, this.price, this.stock,
                this.thumbnailUuid != null ? this.thumbnailUuid + ".jpg" : null);
    }
    public ProductDto toDto() {
        List<String> imageUrls = this.productImages.stream()
                //.sorted((img1, img2) -> img1.getImgOrder().compareTo(img2.getImgOrder()))
                .map(productimage -> productimage.getImageUuid() + ".jpg").toList();
        Set<String> categoryNames = this.categories.stream()
                .map(Category::getName)
                .collect(Collectors.toSet());
        return new ProductDto(this.publicId, this.name, this.description, imageUrls, this.price, this.stock, categoryNames);
    }
    //TODO service to set thumbnail on product add/edit and productimageservice on image add/delete
    //ProductSummarydto gets cached thumbnail DONE
    //cache thumbnail when adding image with order 1 DONE or when deleting image with order 1
    //if no image with order 1, set to null DONE

}
