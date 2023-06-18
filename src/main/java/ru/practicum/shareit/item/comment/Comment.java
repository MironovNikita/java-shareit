package ru.practicum.shareit.item.comment;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.common.validation.Create;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(groups = Create.class, message = "Текст не должен быть пустым!")
    @Size(groups = Create.class, message = "Текст не должен быть менее 1 символа и более 1000 символов!",
            max = 1000, min = 1)
    private String text;
    @ManyToOne
    @JoinColumn(name = "item_id")
    @NotNull(groups = Create.class, message = "Необходима вещь, на которую оставляют комментарий!")
    private Item item;
    @ManyToOne
    @JoinColumn(name = "author_id")
    @NotNull(message = "Необходим автор комментария!")
    private User author;
    @NotNull(message = "Необходимо указать время и дату создания комментария!")
    private LocalDateTime created;
}
