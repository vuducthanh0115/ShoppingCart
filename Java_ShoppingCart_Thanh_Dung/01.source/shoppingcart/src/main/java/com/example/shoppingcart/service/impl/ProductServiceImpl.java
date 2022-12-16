package com.example.shoppingcart.service.impl;

import com.example.shoppingcart.dto.ProductListDTO;
import com.example.shoppingcart.entity.AuditLog;
import com.example.shoppingcart.entity.Product;
import com.example.shoppingcart.entity.ProductMedia;
import com.example.shoppingcart.entity.Views;
import com.example.shoppingcart.exception.NotFoundException;
import com.example.shoppingcart.repository.AuditLogRepository;
import com.example.shoppingcart.repository.ProductMediaRepository;
import com.example.shoppingcart.repository.ProductRepository;
import com.example.shoppingcart.repository.ViewsRepository;
import com.example.shoppingcart.service.FilesStorageService;
import com.example.shoppingcart.service.ProductService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private ProductRepository productRepository;

    @Autowired
    private HttpServletRequest request;
    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Autowired
    private FilesStorageService filesStorageService;

    @Autowired
    private ProductMediaRepository productMediaRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private ViewsRepository viewsRepository;

    @Override
    public List<ProductListDTO> getAllProduct(Integer pageNo, Integer pageSize, String sortBy) {
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
        List<ProductListDTO> pageResult = productRepository.findAll(paging)
                .stream()
                .map(data -> {
                    ProductListDTO employeeListDto = new ProductListDTO();
                    BeanUtils.copyProperties(data, employeeListDto);
                    return employeeListDto;
                }).collect(Collectors.toList());
        if (!pageResult.isEmpty()) {
            return pageResult;
        }
        throw new NotFoundException("Product not found");
    }

    public String getUsername(HttpServletRequest request) {
        String encode = request.getHeader("Authorization").substring(6);
        byte[] decodedBytes = Base64.getDecoder().decode(encode);
        String decodedString = new String(decodedBytes);
        int i = decodedString.indexOf(":");
        return decodedString.substring(0, i);
    }

    @Override
    public boolean createProduct(ProductListDTO productListDTO) {
        Product product = new Product();
        BeanUtils.copyProperties(productListDTO, product);
        productRepository.save(product);
        return true;
    }

    @Override
    public boolean deleteProductById(Long product_id) {
        if (productRepository.countId(product_id) > 0) {
            productRepository.deleteByProductId(product_id);
            AuditLog auditLog = new AuditLog();
            LocalDate now = LocalDate.now();
            auditLog.setProductId(product_id);
            auditLog.setCreatedAt(now);
            auditLog.setDml("Delete");
            auditLog.setUsername(getUsername(request));
            auditLogRepository.save(auditLog);
            return true;
        }
        throw new NotFoundException("Product + " + product_id + " not found!");
    }

    @Override
    public void editProduct(ProductListDTO productListDTO, Long product_id) {
        Product product = productRepository.findById(product_id).orElseThrow(() -> new NotFoundException("Product not found!"));
        product.setProductName(productListDTO.getProductName());
        product.setProductPrice(productListDTO.getProductPrice());
        product.setProductQuantity(productListDTO.getProductQuantity());
        product.setProductRating(productListDTO.getProductRating());
        product.setSoldNumber(productListDTO.getSoldNumber());
        product.setCategoryId(productListDTO.getCategoryId());
        productRepository.save(product);
        AuditLog auditLog = new AuditLog();
        LocalDate now = LocalDate.now();
        auditLog.setProductId(product.getProductId());
        auditLog.setCreatedAt(now);
        auditLog.setDml("Update");
        auditLog.setUsername(getUsername(request));
        auditLogRepository.save(auditLog);
    }

    @Override
    public List<ProductListDTO> getProductByName(String product_name, Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        List<ProductListDTO> productListDTOS = this.productRepository.getProductByName(product_name, pageable)
                .stream()
                .map(data -> {
                    ProductListDTO productListDTO = new ProductListDTO();
                    BeanUtils.copyProperties(data, productListDTO);
                    return productListDTO;
                }).collect(Collectors.toList());
        if (!productListDTOS.isEmpty()) {
            return productListDTOS;
        } else throw new NotFoundException("No product found with name: " + product_name);
    }

    @Override
    public ProductListDTO addAProduct(ProductListDTO productDTO, MultipartFile[] multipartFile) {
        MultipartFile[] multipartFiles = multipartFile;
        Product product = new Product();
        BeanUtils.copyProperties(productDTO, product);
        Product prod = productRepository.save(product);
        productDTO.setProductId(prod.getProductId());
        productDTO.setProductMediaSet(uploadProductMedia(productDTO, multipartFiles));
        AuditLog auditLog = new AuditLog();
        LocalDate now = LocalDate.now();
        auditLog.setProductId(prod.getProductId());
        auditLog.setCreatedAt(now);
        auditLog.setDml("Insert");
        auditLog.setUsername(getUsername(request));
        auditLogRepository.save(auditLog);
        return productDTO;
    }

    @Override
    public List<ProductListDTO> getById(Long productId, HttpSession session) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new NotFoundException("Product not " +
                "found!"));
        product.setViews(product.getViews() + 1);
        productRepository.save(product);
        List<ProductListDTO> productListDTOS = this.productRepository.findById(productId)
                .stream()
                .map(data -> {
                    ProductListDTO productListDTO = new ProductListDTO();
                    BeanUtils.copyProperties(data, productListDTO);
                    return productListDTO;
                }).collect(Collectors.toList());
        if (!productListDTOS.isEmpty()) {
            Long userId = 0L;
            if ((Long) session.getAttribute("user") != null) {
                userId = (Long) session.getAttribute("user");
                if (viewsRepository.findByProductIdAndUserId(productId, userId) != null) {
                    Views views = viewsRepository.findByProductIdAndUserId(productId, userId);
                    views.setViews(views.getViews() + 1);
                    viewsRepository.save(views);
                } else {
                    Views views = new Views();
                    views.setUserId(userId);
                    views.setProductId(productId);
                    views.setViews(1L);
                    viewsRepository.save(views);
                }
            }
            return productListDTOS;
        } else throw new NotFoundException("No product found with id: " + productId);
    }

    @Override
    public Set<ProductMedia> uploadProductMedia(ProductListDTO productDTO, MultipartFile[] multipartFile) {
        if (multipartFile == null) return null;
        Set<ProductMedia> productMediaSet = new HashSet<>();
        Arrays.asList(multipartFile).stream().forEach(file -> {
            String path = filesStorageService.save(file, productDTO.getProductId() + "");
            String type = filesStorageService.getFileTypeByProbeContentType(file.getOriginalFilename());
            ProductMedia productMedia = new ProductMedia();
            productMedia.setPath(path);
            productMedia.setProductId(productDTO.getProductId());

            if (type == null) {
                type = "Unknown";
            }
            productMedia.setType(type);

            productMediaSet.add(productMediaRepository.save(productMedia));

        });
        return productMediaSet;
    }
}
