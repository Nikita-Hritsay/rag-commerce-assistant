package org.assistantAPI.controllers;

import lombok.RequiredArgsConstructor;
import org.assistantAPI.domain.Seller;
import org.assistantAPI.dto.CreateSellerRequest;
import org.assistantAPI.repository.SellerRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/sellers")
@RequiredArgsConstructor
public class SellerController {
    private final SellerRepo sellers;

    @PostMapping
    public ResponseEntity<Seller> create(@RequestBody CreateSellerRequest req) {
        var s = Seller.builder().name(req.name()).build();
        return ResponseEntity.ok(sellers.save(s));
    }

    @GetMapping("{id}")
    public ResponseEntity<Seller> get(@PathVariable UUID id) {
        return sellers.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
}
