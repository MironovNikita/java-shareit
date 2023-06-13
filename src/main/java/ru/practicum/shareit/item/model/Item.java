package ru.practicum.shareit.item.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.dto.BookingDatesDto;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
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
    String name;
    String description;
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
