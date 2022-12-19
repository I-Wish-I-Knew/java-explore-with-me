package ru.practicum.ewm.ewmService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.ewmService.model.invitation.Invitation;

import java.util.Optional;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {

    @Query("select i from Invitation i " +
            "where i.id = :id " +
            "and i.recipient.id = :userId or i.sender.id = :userId")
    Optional<Invitation> findByIdAndSenderOrRecipientId(@Param("id") Long id,
                                                        @Param("userId") Long userId);

    Optional<Invitation> findByIdAndRecipientId(Long id, Long recipientId);

    Boolean existsBySenderIdAndEventIdAndRecipientId(Long senderId, Long eventId, Long recipientId);

}
