package org.assistantAPI.controllers;

import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.assistantAPI.domain.Product;
import org.assistantAPI.domain.User;
import org.assistantAPI.dto.ProductDto;
import org.assistantAPI.dto.UpdateProductRequest;
import org.assistantAPI.mapper.ProductMapper;
import org.assistantAPI.repository.ProductRepo;
import org.assistantAPI.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductRepo products;
    private final UserService userService;

    private Long currentUserId(Authentication auth) {
        String email = auth.getName();
        return userService.findByEmail(email).getId();
    }

    @GetMapping
    public List<ProductDto> myProducts(Authentication auth) {
        Long uid = currentUserId(auth);
        return products.findBySellerIdOrderByUpdatedAtDesc(uid)
                .stream().map(ProductMapper::toDto).toList();
    }

    @PatchMapping("/{id}")
    @Transactional
    public ProductDto updateMine(@PathVariable UUID id,
                                 @RequestBody UpdateProductRequest req,
                                 Authentication auth) {
        Long uid = currentUserId(auth);
        Product p = products.findByIdAndSellerId(id, uid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found"));
        if (req.price()!=null && req.price() < 0)  throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "price>=0");
        if (req.quantity()!=null && req.quantity() < 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "qty>=0");

        ProductMapper.apply(p, req);
        p.setUpdatedAt(OffsetDateTime.now());
        products.save(p);
        return ProductMapper.toDto(p);
    }

    @PostMapping(consumes = {"multipart/form-data"})
    @Transactional
    public ResponseEntity<Map<String, Object>> importCsv(
            @RequestPart("file") MultipartFile file,
            Authentication authentication) throws IOException {

        int ok = 0, failed = 0;
        String userEmail = authentication.getName();
        User user = userService.findByEmail(userEmail);

        try (var reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            var csv = CSVFormat.DEFAULT.builder()
                    .setDelimiter(';')
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .setTrim(true)
                    .build()
                    .parse(reader);

            final List<String> headers = csv.getHeaderNames() != null ? csv.getHeaderNames() : List.of();

            for (CSVRecord r : csv) {
                try {
                    String sku = r.get(0);
                    Double price = parseDoubleSafe(r.get(1));
                    String currency = r.get(2);
                    Integer quantity = Integer.parseInt(r.get(3));

                    Map<String,Object> attrs = new LinkedHashMap<>();
                    for (int i = 4; i < r.size(); i++) {
                        String key = (i < headers.size() && headers.get(i) != null && !headers.get(i).isBlank())
                                ? headers.get(i).trim()
                                : ("col" + (i+1));
                        String raw = r.get(i);
                        if (raw != null && !raw.isBlank())
                            attrs.put(key, inferSimple(raw));
                    }

                    var existing = products.findBySellerIdAndSku(user.getId(), sku).orElse(null);
                    if (existing == null) {
                        products.save(Product.builder()
                                .id(UUID.randomUUID())
                                .sellerId(user.getId())
                                .sku(sku)
                                .price(price)
                                .currency(currency != null ? currency : "UAH")
                                .quantity(quantity)
                                .updatedAt(OffsetDateTime.now())
                                .attributes(attrs)
                                .build());
                    } else {
                        if (price != null)
                            existing.setPrice(price);
                        if (currency != null && !currency.isBlank())
                            existing.setCurrency(currency);
                        existing.setUpdatedAt(OffsetDateTime.now());
                        existing.setQuantity(quantity);
                        if (existing.getAttributes() == null)
                            existing.setAttributes(new LinkedHashMap<>());
                        existing.getAttributes().putAll(attrs);
                        products.save(existing);
                    }
                    ok++;
                } catch (Exception e) {
                    failed++;
                }
            }
        }
        return ResponseEntity.ok(Map.of("ok", ok, "failed", failed));
    }

    private static Double parseDoubleSafe(String s) {
        if (s == null || s.isBlank())
            return null;
        try {
            return Double.valueOf(s.replace(",", "."));
        } catch (Exception e) {
            return null;
        }
    }
    private static Object inferSimple(String raw) {
        String s = raw.trim();
        if (s.equalsIgnoreCase("true") || s.equalsIgnoreCase("false"))
            return Boolean.valueOf(s);
        if (s.matches("[-+]?\\d+"))
            try {
                return Integer.valueOf(s);
            } catch (Exception e) {
                e.printStackTrace();
            }
        if (s.matches("[-+]?\\d*([.,]\\d+)?"))
            try {
                return Double.valueOf(s.replace(",", "."));
            } catch (Exception e) {
                e.printStackTrace();
            }
        return s;
    }
}