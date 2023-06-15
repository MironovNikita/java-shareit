package ru.practicum.shareit.item.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.dto.BookingDatesDto;
import ru.practicum.shareit.common.validation.Create;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Builder
@Table(name = "items")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @NotBlank(groups = {Create.class}, message = "Необходимо название вещи!")
    @Size(groups = Create.class, min = 1, max = 255, message = "Название вещи должно быть от 1 до 255 символов!")
    String name;
    @NotBlank(groups = {Create.class}, message = "Необходимо описание вещи!")
    @Size(groups = Create.class, min = 1, max = 500, message = "Описание вещи должно быть от 1 до 500 символов!")
    String description;
    @NotNull(groups = {Create.class}, message = "Необходим статус вещи!")
    Boolean available;
    @ManyToOne
    @JoinColumn(name = "owner_id")
    User owner;

    @Transient
    BookingDatesDto lastBooking;
    @Transient
    BookingDatesDto nextBooking;
    @Transient
    List<CommentDto> comments;
}
