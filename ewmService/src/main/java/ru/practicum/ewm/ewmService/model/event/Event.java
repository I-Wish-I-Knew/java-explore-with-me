package ru.practicum.ewm.ewmService.model.event;

import lombok.*;
import ru.practicum.ewm.ewmService.model.category.Category;
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
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id", nullable = false)
    private Long id;
    @Column(length = 2000, nullable = false)
    private String annotation;
    @Column(length = 120, nullable = false)
    private String title;
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    @Column(length = 7000, nullable = false)
    private String description;
    @Column(nullable = false)
    private LocalDateTime eventDate;
    @Embedded
    private Location location;
    @Column(nullable = false)
    private Boolean paid;
    @Column(nullable = false)
    @Builder.Default
    private Integer participantLimit = 0;
    @Column(nullable = false)
    @Builder.Default
    private Boolean requestModeration = false;
    @ManyToOne
    @JoinColumn(name = "initiator_id", nullable = false)
    private User initiator;
    @Column(nullable = false)
    private final LocalDateTime createdOn = LocalDateTime.now();
    private LocalDateTime publishedOn;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private State state;
    @Column(nullable = false)
    @Builder.Default
    private Boolean onlyInvited = false;
}
