package ru.practicum.ewm.ewmService.model.request;

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
@Table(name = "participation_requests")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id", nullable = false)
    private Long id;
    private LocalDateTime created = LocalDateTime.now();
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
    @ManyToOne
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StateRequest status;

}
