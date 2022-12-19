package ru.practicum.ewm.ewmService.model.invitation;

import lombok.*;
import ru.practicum.ewm.ewmService.model.event.Event;
import ru.practicum.ewm.ewmService.model.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "invitations")
public class Invitation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invitation_id", nullable = false)
    private Long id;
    @Column(nullable = false)
    private final LocalDateTime created = LocalDateTime.now();
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;
    @ManyToOne
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StateInvitation status;
}
