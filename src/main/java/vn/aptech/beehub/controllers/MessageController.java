package vn.aptech.beehub.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.aptech.beehub.models.Message;
import vn.aptech.beehub.payload.request.SendMessageRequest;
import vn.aptech.beehub.services.MessageService;

import java.util.List;

@Tag(name = "Message")
@CrossOrigin(origins = "http://localhost:5173", maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("/api/messages")
public class MessageController {
    @Autowired
    MessageService messageService;

    @GetMapping("/user/{id}")
    ResponseEntity<?> getUserMessages(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(messageService.getChatMessagesByUserId(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Cannot get messages");
        }
    }

    @GetMapping("/group/{id}")
    ResponseEntity<?> getGroupMessages(@PathVariable Long id) {
        try {
        return ResponseEntity.ok(messageService.getChatMessagesByGroupId(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Cannot get messages");
        }
    }

    @PostMapping("/user")
    ResponseEntity<?> createUserMessage(@RequestBody SendMessageRequest messageRequest) {
        try {
            return ResponseEntity.ok(messageService.sendMessageForUser(messageRequest));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Cannot send message");
        }
    }

    @PostMapping("/group")
    ResponseEntity<?> createGroupMessage(@RequestBody SendMessageRequest messageRequest) {
        try {
            return ResponseEntity.ok(messageService.sendMessageForGroup(messageRequest));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Cannot send message");
        }
    }
}
