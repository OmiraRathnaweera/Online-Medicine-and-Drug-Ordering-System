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
@RequestMapping(value = "api/v1/product/admin")
public class AdminProductController {
    @Autowired
    private ProductService productService;

    @PostMapping(value = "/saveProduct")
    public ResponseEntity<ResponseDTO> saveProduct(@RequestBody ProductDTO productDTO) {
        ResponseDTO responseDTO = new ResponseDTO();
        try {
            String response = productService.saveProduct(productDTO);
            if (StringList.RSP_SUCCESS.equals(response)) {
                responseDTO.setMessage(StringList.RSP_SUCCESS);
                responseDTO.setContent(productDTO);
                return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
            } else if (StringList.RSP_DUPLICATED.equals(response)) {
                responseDTO.setMessage(StringList.RSP_DUPLICATED);
                responseDTO.setContent(productDTO);
                return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
            } else {
                responseDTO.setMessage(StringList.RSP_FAIL);
                responseDTO.setContent(null);
                return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            responseDTO.setMessage(e.getMessage());
            responseDTO.setContent(null);
            return new ResponseEntity<>(responseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(value = "/updateProduct")
    public ResponseEntity<ResponseDTO> updateProduct(@RequestBody ProductDTO productDTO) {
        ResponseDTO responseDTO = new ResponseDTO();
        try {
            String response = productService.updateProduct(productDTO);
            if (StringList.RSP_SUCCESS.equals(response)) {
                responseDTO.setMessage(StringList.RSP_SUCCESS);
                responseDTO.setContent(productDTO);
                return new ResponseEntity<>(responseDTO, HttpStatus.ACCEPTED);
            } else if (StringList.RSP_NO_DATA_FOUND.equals(response)) {
                responseDTO.setMessage(StringList.RSP_NO_DATA_FOUND);
                responseDTO.setContent(null);
                return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
            } else {
                responseDTO.setMessage(StringList.RSP_FAIL);
                responseDTO.setContent(null);
                return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            responseDTO.setMessage(e.getMessage());
            responseDTO.setContent(null);
            return new ResponseEntity<>(responseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getAllProducts")
    public ResponseEntity<ResponseDTO> getAllProducts() {
        ResponseDTO responseDTO = new ResponseDTO();
        try {
            List<ProductDTO> productDTOList = productService.getProductList();
            responseDTO.setMessage(StringList.RSP_SUCCESS);
            responseDTO.setContent(productDTOList);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (Exception e) {
            responseDTO.setMessage(e.getMessage());
            responseDTO.setContent(null);
            return new ResponseEntity<>(responseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/deleteProduct/{productId}")
    public ResponseEntity<ResponseDTO> deleteProduct(@PathVariable int productId) {
        ResponseDTO responseDTO = new ResponseDTO();
        try {
            String response = productService.deleteProduct(productId);
            if (StringList.RSP_SUCCESS.equals(response)) {
                responseDTO.setMessage(StringList.RSP_SUCCESS);
                responseDTO.setContent("Product with ID " + productId + " deleted.");
                return new ResponseEntity<>(responseDTO, HttpStatus.OK);
            } else {
                responseDTO.setMessage(StringList.RSP_NO_DATA_FOUND);
                responseDTO.setContent(null);
                return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            responseDTO.setMessage(e.getMessage());
            responseDTO.setContent(null);
            return new ResponseEntity<>(responseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/searchProduct")
    public ResponseEntity<ResponseDTO> searchProduct(@RequestParam("name") String productName) {
        ResponseDTO responseDTO = new ResponseDTO();
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

    @PutMapping("/updateStock/{id}")
    public ResponseEntity<ResponseDTO> updateStock(@PathVariable int id, @RequestParam int quantity) {
        ResponseDTO responseDTO = new ResponseDTO();
        try {
            ProductDTO updatedProduct = productService.updateStock(id, quantity);
            responseDTO.setMessage("Stock updated successfully");
            responseDTO.setContent(updatedProduct);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (RuntimeException e) {
            responseDTO.setMessage(e.getMessage());
            responseDTO.setContent(null);
            return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            responseDTO.setMessage(StringList.RSP_ERROR);
            responseDTO.setContent(null);
            return new ResponseEntity<>(responseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
