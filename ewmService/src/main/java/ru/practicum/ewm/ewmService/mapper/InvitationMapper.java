package ru.practicum.ewm.ewmService.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.ewmService.model.event.Event;
import ru.practicum.ewm.ewmService.model.invitation.Invitation;
import ru.practicum.ewm.ewmService.model.invitation.InvitationDto;
import ru.practicum.ewm.ewmService.model.user.User;

@UtilityClass
public class InvitationMapper {

    public static Invitation toInvitation(User sender, Event event, User recipient) {
        return Invitation.builder()
                .sender(sender)
                .event(event)
                .recipient(recipient)
                .build();
    }

    public static InvitationDto toInvitationDto(Invitation invitation) {
        return InvitationDto.builder()
                .id(invitation.getId())
                .sender(invitation.getSender().getId())
                .recipient(invitation.getRecipient().getId())
                .event(invitation.getEvent().getId())
                .stateInvitation(invitation.getStatus())
                .created(invitation.getCreated()).build();
    }
}
