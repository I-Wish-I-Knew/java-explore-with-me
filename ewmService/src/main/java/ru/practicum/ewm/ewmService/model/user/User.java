package ru.practicum.ewm.ewmService.model.user;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long id;
    @Column(name = "user_name", length = 150, nullable = false)
    private String name;
    @Column(length = 150, nullable = false)
    private String email;
}
