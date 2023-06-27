package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "requests")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Описание запроса не может быть пустым!")
    @Size(min = 5, max = 500, message = "Описание запроса должно содержать от 5 до 500 символов")
    private String description;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private LocalDateTime created;
}
