package com.example.demo.controller.admin;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.domain.Product;
import com.example.demo.domain.User;
import com.example.demo.service.ProductService;
import com.example.demo.service.UploadService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ProductController {
    ProductService productService;
    UploadService uploadService;

    public ProductController(ProductService productService, UploadService uploadService) {
        this.productService = productService;
        this.uploadService = uploadService;
    }

    @GetMapping("/admin/product")
    public String getDashBoard(Model model) {
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        return "admin/product/show";
    }

    @GetMapping("/admin/product/create")
    public String getCreateProductPage(Model model) {
        model.addAttribute("newProduct", new Product());
        return "admin/product/create";
    }

    @PostMapping("/admin/product/create")
    public String createProductPage(Model model, @ModelAttribute("newProduct") @Valid Product newProduct,
            BindingResult newUserBindingResult,
            @RequestParam("avatarFile") MultipartFile file) {

        List<FieldError> errors = newUserBindingResult.getFieldErrors();
        for (FieldError error : errors) {
            System.out.println(error.getField() + " - " + error.getDefaultMessage());
        }
        if (newUserBindingResult.hasErrors()) {
            return "admin/product/create";
        }
        String image = uploadService.handleSaveUploadFile(file, "product");
        newProduct.setImage(image);
        productService.handleSaveProduct(newProduct);
        return "redirect:/admin/product";

    }

    @GetMapping("/admin/product/{id}")
    public String getProductDetailPage(Model model, @PathVariable long id) {
        model.addAttribute("id", id);
        Product product = this.productService.getProductById(id);
        model.addAttribute("product", product);
        return "admin/product/view";
    }

    @GetMapping("/admin/product/update/{id}")
    public String getProductUpdatePage(Model model, @PathVariable long id) {
        model.addAttribute("id", id);
        Product product = this.productService.getProductById(id);
        model.addAttribute("newProduct", product);
        return "admin/product/update";
    }

    @PostMapping("/admin/product/update")
    public String getProductUpdate(Model model,
            @ModelAttribute("newProduct") @Valid Product updateProduct, BindingResult newUserBindingResult,
            @RequestParam("avatarFile") MultipartFile file) {
        if (newUserBindingResult.hasErrors()) {
            return "admin/product/update";
        }
        if (!file.isEmpty()) {
            String image = uploadService.handleSaveUploadFile(file, "product");
            updateProduct.setImage(image);
        } else {
            Product oldProduct = productService.getProductById(updateProduct.getId());
            updateProduct.setImage(oldProduct.getImage());
        }
        productService.handleSaveProduct(updateProduct);
        return "redirect:/admin/product";
    }

    @GetMapping("/admin/product/delete/{id}")
    public String getProductDeletePage(Model model, @PathVariable long id) {
        model.addAttribute("id", id);
        model.addAttribute("deleteProduct", new Product());
        return "admin/product/delete";
    }

    @PostMapping("/admin/product/delete")
    public String getProductDelete(Model model, @ModelAttribute("deleteProduct") Product product) {
        productService.deleteProduct(product.getId());
        return "redirect:/admin/product";
    }
}
