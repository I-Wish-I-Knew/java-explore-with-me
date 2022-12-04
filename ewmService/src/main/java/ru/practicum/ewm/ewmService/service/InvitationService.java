package ru.practicum.ewm.ewmService.service;

import ru.practicum.ewm.ewmService.model.invitation.InvitationDto;
import ru.practicum.ewm.ewmService.model.invitation.StateInvitation;

import java.time.LocalDateTime;
import java.util.List;

public interface InvitationService {

    /**
     * Метод принимает в качестве параметров дату и время начала периода @param LocalDateTime start
     * и конца переода @param LocalDateTime end в котором происходит мероприятие,
     * а также идентификатор пользователя осуществившего запрос @param Long senderId
     * и статус @param StateInvitation status.
     */
    List<InvitationDto> getAllSent(Long userId, LocalDateTime start, LocalDateTime end, StateInvitation status);

    /**
     * Метод принимает в качестве параметров дату и время начала периода @param LocalDateTime start
     * и конца периода @param LocalDateTime end в котором происходит мероприятие,
     * а также идентификатор пользователя осуществившего запрос @param Long recipientId
     * и статус @param StateInvitation status.
     */
    List<InvitationDto> getAllReceived(Long userId, LocalDateTime start, LocalDateTime end, StateInvitation status);

    /**
     * Метод возвращает приглашение по идентификатору
     */
    InvitationDto get(Long id, Long userId);

    /**
     * Метод сохраняет приглашение от пользователя c идентификатором @param Long senderId,
     * на мероприятие с идентификатором @param Long eventId, пользователю с идентификатором @param Long recipientId
     */
    InvitationDto save(Long senderId, Long eventId, Long recipientId);

    /**
     * Метод для принятия приглашения с идентификатором @param Long id
     * пользователем с идентификатором @param Long recipientId
     */
    InvitationDto accept(Long id, Long recipientId);

    /**
     * Метод для отклонения приглашения с идентификатором @param Long id
     * пользователем с идентификатором @param Long recipientId
     */
    InvitationDto reject(Long id, Long recipientId);

}
