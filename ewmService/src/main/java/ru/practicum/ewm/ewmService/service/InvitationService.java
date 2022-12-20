package ru.practicum.ewm.ewmService.service;

import ru.practicum.ewm.ewmService.model.invitation.InvitationDto;
import ru.practicum.ewm.ewmService.model.invitation.StateInvitation;

import java.time.LocalDateTime;
import java.util.List;

public interface InvitationService {

    List<InvitationDto> getAllSent(Long userId, LocalDateTime start, LocalDateTime end, StateInvitation status);

    List<InvitationDto> getAllReceived(Long userId, LocalDateTime start, LocalDateTime end, StateInvitation status);

    InvitationDto get(Long id, Long userId);

    InvitationDto save(Long senderId, Long eventId, Long recipientId);

    InvitationDto accept(Long id, Long recipientId);

    InvitationDto reject(Long id, Long recipientId);

}
