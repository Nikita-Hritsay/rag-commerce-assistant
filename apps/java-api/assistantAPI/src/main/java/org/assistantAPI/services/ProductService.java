package org.assistantAPI.services;

import org.assistantAPI.domain.Product;
import org.assistantAPI.dto.IntentDTO;

import java.util.List;

public interface ProductService {
    public List<Product> findByUserMessage(IntentDTO intentDTO);
}
