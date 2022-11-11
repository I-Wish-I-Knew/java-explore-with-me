package ru.practicum.ewm.ewmService.model.compilation;

import lombok.*;
import ru.practicum.ewm.ewmService.model.event.Event;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "compilations")
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "compilation_id", nullable = false)
    private Long id;
    @OneToMany
    @JoinTable(
            name = "compiled_events",
            joinColumns = @JoinColumn(name = "compilation_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    @ToString.Exclude
    private Set<Event> events;
    @Column(nullable = false)
    private Boolean pinned;
    @Column(nullable = false)
    private String title;
}
