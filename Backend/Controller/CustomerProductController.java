package com.example.Medicine.Drug_OS.Controller;

import com.example.Medicine.Drug_OS.DTO.ProductDTO;
import com.example.Medicine.Drug_OS.DTO.ResponseDTO;
import com.example.Medicine.Drug_OS.Service.ProductService;
import com.example.Medicine.Drug_OS.Util.StringList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping(value = "api/v1/product/customer")
public class CustomerProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ResponseDTO responseDTO;

    @GetMapping("/getAllProducts")
    public ResponseEntity<ResponseDTO> getAllProductsForCustomer() {
        try {
            List<ProductDTO> productList = productService.getProductList();
            responseDTO.setMessage(StringList.RSP_SUCCESS);
            responseDTO.setContent(productList);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (Exception e) {
            responseDTO.setMessage(StringList.RSP_ERROR);
            responseDTO.setContent(null);
            return new ResponseEntity<>(responseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/viewAvailability")
    public ResponseEntity<ResponseDTO> getAvailableProducts() {
        try {
            List<ProductDTO> availableProducts = productService.getAvailableProducts();
            responseDTO.setMessage(StringList.RSP_SUCCESS);
            responseDTO.setContent(availableProducts);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (Exception e) {
            responseDTO.setMessage(StringList.RSP_ERROR);
            responseDTO.setContent(null);
            return new ResponseEntity<>(responseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/searchProduct")
    public ResponseEntity<ResponseDTO> searchProductByName(@RequestParam("name") String productName) {
        try {
            ProductDTO productDTO = productService.searchProduct(productName);
            if (productDTO != null) {
                responseDTO.setMessage(StringList.RSP_SUCCESS);
                responseDTO.setContent(productDTO);
                return new ResponseEntity<>(responseDTO, HttpStatus.OK);
            } else {
                responseDTO.setMessage("Product not found");
                responseDTO.setContent(null);
                return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            responseDTO.setMessage(StringList.RSP_ERROR);
            responseDTO.setContent(null);
            return new ResponseEntity<>(responseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/filterCategory/{category}")
    public ResponseEntity<ResponseDTO> filterProductsByCategory(@PathVariable String category) {
        try {
            List<ProductDTO> products = productService.filterProductsByCategory(category);
            responseDTO.setMessage(StringList.RSP_SUCCESS);
            responseDTO.setContent(products);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (Exception e) {
            responseDTO.setMessage(StringList.RSP_ERROR);
            responseDTO.setContent(null);
            return new ResponseEntity<>(responseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}