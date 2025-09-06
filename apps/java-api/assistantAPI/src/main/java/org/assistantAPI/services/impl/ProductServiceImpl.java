package org.assistantAPI.services.impl;

import lombok.AllArgsConstructor;
import org.assistantAPI.domain.Product;
import org.assistantAPI.dto.IntentDTO;
import org.assistantAPI.repository.ProductRepo;
import org.assistantAPI.services.ProductService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {
    private ProductRepo productRepo;

    @Override
    public List<Product> findByUserMessage(IntentDTO intentDTO) {
        return productRepo.search(null,null,null,null,false,null,null,null,0, 0);
    }
}
