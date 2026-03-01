package com.example.Medicine.Drug_OS.Service;

import com.example.Medicine.Drug_OS.DTO.ProductDTO;
import com.example.Medicine.Drug_OS.Entity.Product;
import com.example.Medicine.Drug_OS.Reposit.ProductRepo;
import com.example.Medicine.Drug_OS.Util.StringList;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductService {
    @Autowired
    private ProductRepo productRepo;
    @Autowired
    private ModelMapper modelMapper;

    public String saveProduct(ProductDTO productDTO) {
        if (productDTO.getProductName() != null && productRepo.existsByProductName(productDTO.getProductName())) {
            return StringList.RSP_DUPLICATED;
        }

        Product product = modelMapper.map(productDTO, Product.class);
        product.setProductId(null);
        if (product.getLowStockThreshold() == null) product.setLowStockThreshold(5);
        if (product.getExpiryThresholdDays() == null) product.setExpiryThresholdDays(60);
        if (product.getIsDiscounted() == null) product.setIsDiscounted(false);

        productRepo.save(product);
        return StringList.RSP_SUCCESS;
    }

    public String updateProduct(ProductDTO productDTO) {
        Integer id = productDTO.getProductId();
        if (id == null) {
            return StringList.RSP_NO_DATA_FOUND;
        }

        Optional<Product> optional = productRepo.findById(id);
        if (optional.isEmpty()) {
            return StringList.RSP_NO_DATA_FOUND;
        }

        Product existing = optional.get();
        // Copy allowed fields from DTO to entity
        if (productDTO.getProductName() != null) existing.setProductName(productDTO.getProductName());
        if (productDTO.getProductPrice() != null) existing.setProductPrice(productDTO.getProductPrice());
        if (productDTO.getProductCategory() != null) existing.setProductCategory(productDTO.getProductCategory());
        if (productDTO.getProductQuantity() != null) existing.setProductQuantity(productDTO.getProductQuantity());
        if (productDTO.getLowStockThreshold() != null) existing.setLowStockThreshold(productDTO.getLowStockThreshold());
        if (productDTO.getExpiryDate() != null) existing.setExpiryDate(productDTO.getExpiryDate());
        if (productDTO.getExpiryThresholdDays() != null) existing.setExpiryThresholdDays(productDTO.getExpiryThresholdDays());
        if (productDTO.getIsDiscounted() != null) existing.setIsDiscounted(productDTO.getIsDiscounted());

        productRepo.save(existing);
        return StringList.RSP_SUCCESS;
    }

    public List<ProductDTO> getProductList() {
        List<Product> productList = productRepo.findAll();
        return productList.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public String deleteProduct(int productId) {
        if (productRepo.existsById(productId)) {
            productRepo.deleteById(productId);
            return StringList.RSP_SUCCESS;
        } else {
            return StringList.RSP_NO_DATA_FOUND;
        }
    }

    public ProductDTO searchProduct(String productName) {
        Optional<Product> productOptional = productRepo.findByProductName(productName);
        return productOptional.map(this::convertToDto).orElse(null);
    }

    public List<ProductDTO> filterProductsByCategory(String category) {
        List<Product> products = productRepo.findByProductCategoryContainingIgnoreCase(category);
        return products.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public ProductDTO updateStock(int id, int newQuantity) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        product.setProductQuantity(newQuantity);
        Product savedProduct = productRepo.save(product);
        return convertToDto(savedProduct);
    }

    public List<ProductDTO> getAvailableProducts() {
        return productRepo.findAll().stream()
                .filter(p -> p.getProductQuantity() != null && p.getProductQuantity() > 0)
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private ProductDTO convertToDto(Product product) {
        ProductDTO dto = modelMapper.map(product, ProductDTO.class);
        dto.setProductStatus(product.getProductStatus());
        return dto;
    }
}
