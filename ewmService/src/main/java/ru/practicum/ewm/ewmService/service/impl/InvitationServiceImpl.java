package ru.practicum.ewm.ewmService.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.ewmService.exception.AlreadyExistsException;
import ru.practicum.ewm.ewmService.exception.ForbiddenException;
import ru.practicum.ewm.ewmService.exception.NotFoundException;
import ru.practicum.ewm.ewmService.mapper.InvitationMapper;
import ru.practicum.ewm.ewmService.model.event.Event;
import ru.practicum.ewm.ewmService.model.event.State;
import ru.practicum.ewm.ewmService.model.invitation.Invitation;
import ru.practicum.ewm.ewmService.model.invitation.InvitationDto;
import ru.practicum.ewm.ewmService.model.invitation.StateInvitation;
import ru.practicum.ewm.ewmService.model.user.User;
import ru.practicum.ewm.ewmService.repository.EventRepository;
import ru.practicum.ewm.ewmService.repository.InvitationRepository;
import ru.practicum.ewm.ewmService.repository.UserRepository;
import ru.practicum.ewm.ewmService.service.InvitationService;
import ru.practicum.ewm.ewmService.service.RequestService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.ewmService.utility.Constants.*;

@Service
@Transactional(readOnly = true)
public class InvitationServiceImpl implements InvitationService {

    private final InvitationRepository repository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestService requestService;
    @PersistenceContext
    private EntityManager entityManager;

    public InvitationServiceImpl(InvitationRepository repository, UserRepository userRepository,
                                 EventRepository eventRepository, RequestService requestService) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.requestService = requestService;
    }

    @Override
    public List<InvitationDto> getAllSent(Long userId, LocalDateTime start,
                                          LocalDateTime end, StateInvitation status) {
        return getAll(userId, start, end, status, true);
    }

    @Override
    public List<InvitationDto> getAllReceived(Long userId, LocalDateTime start,
                                              LocalDateTime end, StateInvitation status) {
        return getAll(userId, start, end, status, false);
    }

    @Override
    public InvitationDto get(Long id, Long userId) {
        Invitation invitation = repository.findByIdAndSenderOrRecipientId(id, userId)
                .orElseThrow(() -> new NotFoundException(String.format(INVITATIONS_NOT_FOUND, id)));
        return InvitationMapper.toInvitationDto(invitation);
    }

    @Override
    @Transactional
    public InvitationDto save(Long senderId, Long eventId, Long recipientId) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new NotFoundException(String.format(USER_NOT_FOUND, senderId)));
        User recipient = userRepository.findById(senderId)
                .orElseThrow(() -> new NotFoundException(String.format(USER_NOT_FOUND, recipientId)));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format(EVENT_NOT_FOUND, eventId)));

        if (event.getState() != State.PUBLISHED) {
            throw new ForbiddenException("Invitation cannot be sent to an unpublished event");
        }
        if (recipientId.equals(event.getInitiator().getId())) {
            throw new ForbiddenException(String.format("User %d is an initiator of event %d", recipient.getId(), eventId));
        }
        if (senderId.equals(recipientId)) {
            throw new ForbiddenException("User cannot send invitation to himself");
        }
        if (Boolean.TRUE.equals(repository.existsBySenderIdAndEventIdAndRecipientId(senderId, eventId, recipientId))) {
            throw new AlreadyExistsException(String.format("Invitation from user %d for event %d to user %d was already sent",
                    senderId, eventId, recipientId));
        }

        if (senderId.equals(event.getInitiator().getId())) {
            requestService.reserveForInvitedGuest(event, recipient);
        }

        Invitation invitation = InvitationMapper.toInvitation(sender, event, recipient);
        invitation.setStatus(StateInvitation.NEW);


        return InvitationMapper.toInvitationDto(repository.save(invitation));
    }

    @Override
    @Transactional
    public InvitationDto accept(Long id, Long recipientId) {
        Invitation invitation = repository.findByIdAndRecipientId(id, recipientId)
                .orElseThrow(() -> new NotFoundException(String.format(INVITATIONS_NOT_FOUND + "for recipient %d", id,
                        recipientId)));

        requestService.save(invitation.getRecipient().getId(), invitation.getEvent().getId());
        invitation.setStatus(StateInvitation.ACCEPTED);

        return InvitationMapper.toInvitationDto(repository.save(invitation));
    }

    @Override
    @Transactional
    public InvitationDto reject(Long id, Long recipientId) {
        Invitation invitation = repository.findByIdAndRecipientId(id, recipientId)
                .orElseThrow(() -> new NotFoundException(String.format(INVITATIONS_NOT_FOUND + "for recipient %d", id,
                        recipientId)));

        if (invitation.getStatus() == StateInvitation.ACCEPTED ||
                invitation.getSender().equals(invitation.getEvent().getInitiator())) {
            requestService.cancelForInvitedGuest(invitation.getEvent().getId(), invitation.getRecipient().getId());
        }

        invitation.setStatus(StateInvitation.REJECTED);

        return InvitationMapper.toInvitationDto(repository.save(invitation));
    }

    private List<InvitationDto> getAll(Long userId, LocalDateTime start,
                                       LocalDateTime end, StateInvitation status, boolean isSender) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Invitation> cq = cb.createQuery(Invitation.class);
        Root<Invitation> invitation = cq.from(Invitation.class);
        List<Predicate> predicates = new ArrayList<>();

        if (isSender) {
            predicates.add(cb.equal(invitation.get("sender").get("id"), (userId)));
        } else {
            predicates.add(cb.equal(invitation.get("recipient").get("id"), (userId)));
        }
        if (start != null) {
            predicates.add(cb.greaterThan(invitation.get("event").get("eventDate"), start));
        }
        if (end != null) {
            predicates.add(cb.lessThan(invitation.get("event").get("eventDate"), end));
        }
        if (status != null) {
            predicates.add(cb.equal(invitation.get("status"), status));
        }

        cq.orderBy(cb.asc(invitation.get("event").get("eventDate")));
        cq.select(invitation).where(predicates.toArray(new Predicate[]{}));

        List<Invitation> invitations = entityManager.createQuery(cq).getResultList();

        return invitations.stream()
                .map(InvitationMapper::toInvitationDto)
                .collect(Collectors.toList());
    }
}
