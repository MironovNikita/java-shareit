package ru.practicum.shareit.item;

import lombok.*;
import ru.practicum.shareit.booking.dto.BookingDatesDto;
import ru.practicum.shareit.common.validation.Create;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.user.User;

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
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(groups = {Create.class}, message = "Необходимо название вещи!")
    @Size(groups = Create.class, min = 1, max = 255, message = "Название вещи должно быть от 1 до 255 символов!")
    private String name;
    @NotBlank(groups = {Create.class}, message = "Необходимо описание вещи!")
    @Size(groups = Create.class, min = 1, max = 500, message = "Описание вещи должно быть от 1 до 500 символов!")
    private String description;
    @NotNull(groups = {Create.class}, message = "Необходим статус вещи!")
    private Boolean available;
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
    @ManyToOne
    @JoinColumn(name = "request_id")
    private Request request;

    @Transient
    private BookingDatesDto lastBooking;
    @Transient
    private BookingDatesDto nextBooking;
    @Transient
    private List<CommentDto> comments;
}
