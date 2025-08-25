package org.assistantAPI.controllers;

import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.assistantAPI.domain.Product;
import org.assistantAPI.domain.User;
import org.assistantAPI.repository.ProductRepo;
import org.assistantAPI.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/import")
@RequiredArgsConstructor
public class ImportController {
    private final ProductRepo products;
    private final UserService userService;

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<Map<String,Object>> importCsv(@RequestPart("file") MultipartFile file, Authentication authentication) throws IOException {
        int ok=0, failed=0;
        try (var reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String usernameOrEmail = authentication.getName();
            User user = userService.findByEmail(usernameOrEmail);

            var csv = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .setTrim(true)
                    .build()
                    .parse(reader);

            for (CSVRecord r : csv) {
                try {
                    String[] data = r.values()[0].split(";");
                    var sku   = data[0];
                    var title = data[1];
                    var desc  = data[2];
                    var price = Double.parseDouble(data[3]);
                    var currency = data[4];

                    var existing = products.findBySellerIdAndSku(user.getId(), sku).orElse(null);
                    if (existing == null) {
                        products.save(Product.builder()
                                .sellerId(user.getId()).sku(sku).title(title)
                                .description(desc).price(price).currency(currency)
                                .updatedAt(OffsetDateTime.now())
                                .build());
                    } else {
                        existing.setTitle(title);
                        existing.setDescription(desc);
                        existing.setPrice(price);
                        existing.setCurrency(currency);
                        existing.setUpdatedAt(OffsetDateTime.now());
                        products.save(existing);
                    }
                    ok++;
                } catch (Exception ex) {
                    failed++;
                }
            }
        }
        return ResponseEntity.ok(Map.of("ok", ok, "failed", failed));
    }
}