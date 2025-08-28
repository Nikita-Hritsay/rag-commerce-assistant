package org.assistantAPI.controllers;

import lombok.RequiredArgsConstructor;
import org.assistantAPI.domain.Product;
import org.assistantAPI.dto.ChatUserMessage;
import org.assistantAPI.dto.IntentDTO;
import org.assistantAPI.dto.MessageResponse;
import org.assistantAPI.mapper.IntentExtractor;
import org.assistantAPI.services.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ProductService productService;

    @GetMapping("/message")
    public ResponseEntity<MessageResponse> create(@RequestBody ChatUserMessage message) {
        IntentDTO intentDTO = IntentExtractor.map(message.text());

        List<Product> byUserMessage = productService.findByUserMessage(intentDTO);
        MessageResponse messageResponse = new MessageResponse(byUserMessage.stream().map(Product::toString).collect(Collectors.joining("\n")));

        return ResponseEntity.ok(messageResponse);
    }

}
