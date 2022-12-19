package ru.practicum.ewm.ewmService.service.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.ewmService.exception.AlreadyExistsException;
import ru.practicum.ewm.ewmService.exception.ForbiddenException;
import ru.practicum.ewm.ewmService.exception.NotFoundException;
import ru.practicum.ewm.ewmService.mapper.InvitationMapper;
import ru.practicum.ewm.ewmService.model.category.Category;
import ru.practicum.ewm.ewmService.model.event.Event;
import ru.practicum.ewm.ewmService.model.event.Location;
import ru.practicum.ewm.ewmService.model.event.State;
import ru.practicum.ewm.ewmService.model.invitation.Invitation;
import ru.practicum.ewm.ewmService.model.invitation.InvitationDto;
import ru.practicum.ewm.ewmService.model.invitation.StateInvitation;
import ru.practicum.ewm.ewmService.model.request.Request;
import ru.practicum.ewm.ewmService.model.request.StateRequest;
import ru.practicum.ewm.ewmService.model.user.User;
import ru.practicum.ewm.ewmService.repository.*;
import ru.practicum.ewm.ewmService.service.InvitationService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.practicum.ewm.ewmService.utility.Constants.INVITATIONS_NOT_FOUND;

@Transactional
@SpringBootTest(properties = "db.name=test", webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class InvitationServiceImplTest {

    private final InvitationService service;
    @Autowired
    private final InvitationRepository repository;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final EventRepository eventRepository;
    @Autowired
    private final RequestRepository requestRepository;
    @Autowired
    private final CategoryRepository categoryRepository;
    private User sender;
    private User initiator;
    private User recipient;
    private Event event1;
    private Event event2;

    @BeforeEach
    void setUp() {
        sender = userRepository.save(User.builder()
                .name("sender")
                .email("sender@email.com")
                .build());
        initiator = userRepository.save(User.builder()
                .name("initiator")
                .email("initiator@email.com")
                .build());
        recipient = userRepository.save(User.builder()
                .name("recipient")
                .email("recipient@email.com")
                .build());
        Category category = categoryRepository.save(Category.builder()
                .name("category")
                .build());
        event1 = eventRepository.save(Event.builder()
                .annotation("annotation for event1")
                .title("title of event1")
                .category(category)
                .description("description of event1")
                .eventDate(LocalDateTime.now().plusDays(1))
                .location(new Location(52.41621224781127, 13.596652621929262))
                .paid(true)
                .participantLimit(0)
                .requestModeration(false)
                .initiator(initiator)
                .state(State.PUBLISHED)
                .build());
        event2 = eventRepository.save(Event.builder()
                .annotation("annotation for event2")
                .title("title of event2")
                .category(category)
                .description("description of event2")
                .eventDate(LocalDateTime.now().plusMonths(1))
                .location(new Location(52.41621224781127, 13.596652621929262))
                .paid(true)
                .participantLimit(0)
                .requestModeration(false)
                .initiator(initiator)
                .state(State.PUBLISHED)
                .build());
    }

    @Test
    void getAllSent() {
        Invitation invitation1 = repository.save(Invitation.builder()
                .sender(sender)
                .recipient(recipient)
                .event(event1)
                .status(StateInvitation.NEW)
                .build());

        Invitation invitation2 = repository.save(Invitation.builder()
                .sender(sender)
                .recipient(recipient)
                .event(event2)
                .status(StateInvitation.NEW)
                .build());

        List<InvitationDto> invitations = service.getAllSent(sender.getId(), null, null, null);

        assertThat(invitations).hasSize(2)
                .contains(InvitationMapper.toInvitationDto(invitation1))
                .contains(InvitationMapper.toInvitationDto(invitation2));
    }

    @Test
    void getAllSentStatus() {
        Invitation invitation1 = repository.save(Invitation.builder()
                .sender(sender)
                .recipient(recipient)
                .event(event1)
                .status(StateInvitation.NEW)
                .build());

        Invitation invitation2 = repository.save(Invitation.builder()
                .sender(sender)
                .recipient(recipient)
                .event(event2)
                .status(StateInvitation.NEW)
                .build());

        invitation2.setStatus(StateInvitation.REJECTED);
        repository.save(invitation2);

        List<InvitationDto> invitations = service.getAllSent(sender.getId(), null, null,
                StateInvitation.REJECTED);

        assertThat(invitations).hasSize(1)
                .doesNotContain(InvitationMapper.toInvitationDto(invitation1))
                .contains(InvitationMapper.toInvitationDto(invitation2));
    }

    @Test
    void getAllSentInRange() {
        Invitation invitation1 = repository.save(Invitation.builder()
                .sender(sender)
                .recipient(recipient)
                .event(event1)
                .status(StateInvitation.NEW)
                .build());

        Invitation invitation2 = repository.save(Invitation.builder()
                .sender(sender)
                .recipient(recipient)
                .event(event2)
                .status(StateInvitation.NEW)
                .build());

        List<InvitationDto> invitations = service.getAllSent(sender.getId(), LocalDateTime.now(),
                LocalDateTime.now().plusDays(5), null);

        assertThat(invitations).hasSize(1)
                .contains(InvitationMapper.toInvitationDto(invitation1))
                .doesNotContain(InvitationMapper.toInvitationDto(invitation2));
    }

    @Test
    void getAllReceived() {
        Invitation invitation1 = repository.save(Invitation.builder()
                .sender(sender)
                .recipient(recipient)
                .event(event1)
                .status(StateInvitation.NEW)
                .build());

        Invitation invitation2 = repository.save(Invitation.builder()
                .sender(sender)
                .recipient(recipient)
                .event(event2)
                .status(StateInvitation.NEW)
                .build());

        List<InvitationDto> invitations = service.getAllReceived(recipient.getId(), null, null, null);

        assertThat(invitations).hasSize(2)
                .contains(InvitationMapper.toInvitationDto(invitation1))
                .contains(InvitationMapper.toInvitationDto(invitation2));
    }

    @Test
    void getAllReceivedStatus() {
        Invitation invitation1 = repository.save(Invitation.builder()
                .sender(sender)
                .recipient(recipient)
                .event(event1)
                .status(StateInvitation.NEW)
                .build());

        Invitation invitation2 = repository.save(Invitation.builder()
                .sender(sender)
                .recipient(recipient)
                .event(event2)
                .status(StateInvitation.NEW)
                .build());

        invitation1.setStatus(StateInvitation.ACCEPTED);
        repository.save(invitation1);

        List<InvitationDto> invitations = service.getAllReceived(recipient.getId(), null, null,
                StateInvitation.ACCEPTED);

        assertThat(invitations).hasSize(1)
                .contains(InvitationMapper.toInvitationDto(invitation1))
                .doesNotContain(InvitationMapper.toInvitationDto(invitation2));
    }

    @Test
    void getAllReceivedInRange() {
        Invitation invitation1 = repository.save(Invitation.builder()
                .sender(sender)
                .recipient(recipient)
                .event(event1)
                .status(StateInvitation.NEW)
                .build());

        Invitation invitation2 = repository.save(Invitation.builder()
                .sender(sender)
                .recipient(recipient)
                .event(event2)
                .status(StateInvitation.NEW)
                .build());

        List<InvitationDto> invitations = service.getAllReceived(recipient.getId(), LocalDateTime.now().plusDays(6),
                LocalDateTime.now().plusMonths(2), null);

        assertThat(invitations).hasSize(1)
                .doesNotContain(InvitationMapper.toInvitationDto(invitation1))
                .contains(InvitationMapper.toInvitationDto(invitation2));
    }

    @Test
    void get() {
        Invitation invitation = repository.save(Invitation.builder()
                .sender(sender)
                .recipient(recipient)
                .event(event1)
                .status(StateInvitation.NEW)
                .build());

        InvitationDto invitationDto = service.get(invitation.getId(), sender.getId());

        assertThat(invitationDto).hasFieldOrPropertyWithValue("id", invitation.getId())
                .hasFieldOrPropertyWithValue("sender", invitation.getSender().getId())
                .hasFieldOrPropertyWithValue("recipient", invitation.getRecipient().getId())
                .hasFieldOrPropertyWithValue("event", invitation.getEvent().getId());
    }

    @Test
    void save() {
        InvitationDto invitationDto = service.save(sender.getId(), event1.getId(), recipient.getId());

        Optional<Invitation> savedInvitation = repository.findByIdAndSenderOrRecipientId(invitationDto.getId(), sender.getId());

        assertThat(savedInvitation).isNotEmpty();
    }

    @Test
    void saveEventIsNotPublished() {
        event1.setState(State.PENDING);

        ForbiddenException thrown = assertThrows(ForbiddenException.class, () -> {
            service.save(sender.getId(), event1.getId(), recipient.getId());
        });

        assertThat(thrown.getMessage()).isNotNull()
                .isEqualTo("Invitation cannot be sent to an unpublished event");
    }

    @Test
    void saveEventRecipientIsAnInitiator() {
        event1.setInitiator(recipient);
        ForbiddenException thrown = assertThrows(ForbiddenException.class, () -> {
            service.save(sender.getId(), event1.getId(), recipient.getId());
        });

        assertThat(thrown.getMessage()).isNotNull()
                .isEqualTo(String.format("User %d is an initiator of event %d", sender.getId(), event1.getId()));
    }

    @Test
    void saveEventRecipientEqualSender() {
        ForbiddenException thrown = assertThrows(ForbiddenException.class, () -> {
            service.save(sender.getId(), event1.getId(), sender.getId());
        });

        assertThat(thrown.getMessage()).isNotNull()
                .isEqualTo("User cannot send invitation to himself");
    }

    @Test
    void saveAlreadyExist() {
        repository.save(Invitation.builder()
                .sender(sender)
                .recipient(recipient)
                .event(event1)
                .status(StateInvitation.NEW)
                .build());

        AlreadyExistsException thrown = assertThrows(AlreadyExistsException.class, () -> {
            service.save(sender.getId(), event1.getId(), recipient.getId());
        });

        assertThat(thrown.getMessage()).isNotNull()
                .isEqualTo(String.format("Invitation from user %d for event %d to user %d was already sent",
                        sender.getId(), event1.getId(), recipient.getId()));
    }

    @Test
    void saveInvitationFromInitiator() {
        event1.setInitiator(sender);

        InvitationDto invitationDto = service.save(sender.getId(), event1.getId(), recipient.getId());
        Optional<Request> request = requestRepository.findByRequesterIdAndEventId(invitationDto.getRecipient(),
                invitationDto.getEvent());

        assertThat(request).isNotEmpty();
        assertThat(request.get()).hasFieldOrPropertyWithValue("status", StateRequest.CONFIRMED);
    }

    @Test
    void accept() {
        Invitation invitation = repository.save(Invitation.builder()
                .sender(sender)
                .recipient(recipient)
                .event(event1)
                .status(StateInvitation.NEW)
                .build());

        service.accept(invitation.getId(), recipient.getId());

        Optional<Invitation> acceptedInvitation = repository.findById(invitation.getId());

        assertThat(acceptedInvitation).isNotEmpty();
        assertThat(acceptedInvitation.get()).hasFieldOrPropertyWithValue("status", StateInvitation.ACCEPTED);
    }

    @Test
    void acceptByWrongUser() {
        Invitation invitation = repository.save(Invitation.builder()
                .sender(sender)
                .recipient(recipient)
                .event(event1)
                .status(StateInvitation.NEW)
                .build());

        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            service.accept(invitation.getId(), sender.getId());
        });

        assertThat(thrown.getMessage()).isNotNull()
                .isEqualTo(String.format(INVITATIONS_NOT_FOUND + "for recipient %d", invitation.getId(),
                        invitation.getSender().getId()));
    }

    @Test
    void reject() {
        Invitation invitation = repository.save(Invitation.builder()
                .sender(sender)
                .recipient(recipient)
                .event(event1)
                .status(StateInvitation.NEW)
                .build());

        service.reject(invitation.getId(), recipient.getId());

        Optional<Invitation> acceptedInvitation = repository.findById(invitation.getId());

        assertThat(acceptedInvitation).isNotEmpty();
        assertThat(acceptedInvitation.get()).hasFieldOrPropertyWithValue("status", StateInvitation.REJECTED);
    }

    @Test
    void rejectByWrongUser() {
        Invitation invitation = repository.save(Invitation.builder()
                .sender(sender)
                .recipient(recipient)
                .event(event1)
                .status(StateInvitation.NEW)
                .build());

        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            service.reject(invitation.getId(), sender.getId());
        });

        assertThat(thrown.getMessage()).isNotNull()
                .isEqualTo(String.format(INVITATIONS_NOT_FOUND + "for recipient %d", invitation.getId(),
                        invitation.getSender().getId()));
    }

    @Test
    void rejectReceivedFromInitiator() {
        event1.setInitiator(sender);
        InvitationDto invitationDto = service.save(sender.getId(), event1.getId(), recipient.getId());
        Optional<Request> request = requestRepository.findByRequesterIdAndEventId(invitationDto.getRecipient(),
                invitationDto.getEvent());

        assertThat(request).isNotEmpty();

        service.reject(invitationDto.getId(), invitationDto.getRecipient());

        Optional<Request> cancelledRequest = requestRepository.findByRequesterIdAndEventId(invitationDto.getRecipient(),
                invitationDto.getEvent());

        assertThat(cancelledRequest).isNotEmpty();
        assertThat(cancelledRequest.get()).hasFieldOrPropertyWithValue("status", StateRequest.CANCELED);
    }
}