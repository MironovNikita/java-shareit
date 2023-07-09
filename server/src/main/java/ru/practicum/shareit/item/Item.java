package ru.practicum.shareit.item;

import lombok.*;
import ru.practicum.shareit.booking.dto.BookingDatesDto;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
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
    private String name;
    private String description;
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
