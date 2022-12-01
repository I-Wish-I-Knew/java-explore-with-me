package ru.practicum.ewm.ewmService.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.ewmService.model.invitation.InvitationDto;
import ru.practicum.ewm.ewmService.service.InvitationService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users/{userId}/invitations")
public class InvitationController {

    private final InvitationService service;

    public InvitationController(InvitationService service) {
        this.service = service;
    }

    @GetMapping("/sent")
    public List<InvitationDto> getAllSent(@PathVariable(name = "userId") Long senderId,
                                          @RequestParam(required = false) LocalDateTime start,
                                          @RequestParam(required = false) LocalDateTime end) {
        log.info("Get all sent invitations for user {} for events from {} to {}", senderId, start, end);
        return service.getAllSent(senderId, start, end);
    }

    @GetMapping("/received")
    public List<InvitationDto> getAllReceived(@PathVariable(name = "userId") Long recipientId,
                                              @RequestParam(required = false) LocalDateTime start,
                                              @RequestParam(required = false) LocalDateTime end) {
        log.info("Get all received invitations for user {} for events from {} to {}", recipientId, start, end);
        return service.getAllReceived(recipientId, start, end);
    }

    @GetMapping("/{id}")
    public InvitationDto get(@PathVariable Long id,
                             @PathVariable Long userId) {
        log.info("Get invitation {} for user {}", id, userId);
        return service.get(id, userId);
    }

    @PostMapping
    public InvitationDto save(@PathVariable(name = "userId") Long senderId,
                              @RequestParam Long eventId,
                              @RequestParam Long recipientId) {
        log.info("Save invitation from user {} from event {} to user {}", senderId, eventId, recipientId);
        return service.save(senderId, eventId, recipientId);
    }

    @PatchMapping("/{id}/accept")
    public InvitationDto accept(@PathVariable Long id,
                                @PathVariable(name = "userId") Long recipientId) {
        log.info("Accept invitation {} by user {}", id, recipientId);
        return service.accept(id, recipientId);
    }

    @PatchMapping("/{id}/reject")
    public InvitationDto reject(@PathVariable Long id,
                                @PathVariable(name = "userId") Long recipientId) {
        log.info("Reject invitation {} by user {}", id, recipientId);
        return service.reject(id, recipientId);
    }
}
