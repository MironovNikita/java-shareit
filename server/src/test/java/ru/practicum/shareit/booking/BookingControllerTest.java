package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";
    @MockBean
    BookingService bookingService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    private BookingController bookingController;

    @Test
    @DisplayName("Проверка метода создания бронирования")
    void checkCreateShouldCreateBooking() throws Exception {
        long bookingId = 1L;
        long userId = 1L;

        LocalDateTime start = LocalDateTime.of(2123, 1, 1, 9, 0);
        LocalDateTime end = LocalDateTime.of(2123, 2, 1, 9, 0);
        BookingDto correctBookingDto = new BookingDto(1L, start, end, null);
        Booking booking = new Booking(bookingId, start, end, null, null, BookingStatus.WAITING);

        when(bookingService.create(any(), anyLong())).thenReturn(booking);

        mockMvc.perform(post("/bookings").header(HEADER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(correctBookingDto)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(booking)));
        verify(bookingService, times(1)).create(any(), anyLong());
    }

    @Test
    @DisplayName("Проверка метода обновления бронирования")
    void checkUpdateShouldUpdateBooking() throws Exception {
        long bookingId = 1L;
        long bookerId = 1L;

        LocalDateTime start = LocalDateTime.of(2123, 1, 1, 9, 0);
        LocalDateTime end = LocalDateTime.of(2123, 2, 1, 9, 0);
        Booking booking = new Booking(bookingId, start, end, null, null, BookingStatus.WAITING);

        when(bookingService.update(anyLong(), anyLong(), anyBoolean())).thenReturn(booking);

        mockMvc.perform(patch(String.format("/bookings/%d", bookingId))
                .contentType(MediaType.APPLICATION_JSON)
                .header(HEADER_USER_ID, bookerId)
                .queryParam("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(booking)));
        verify(bookingService, times(1)).update(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    @DisplayName("Проверка метода получения бронирования по ID")
    void checkGetByIdShouldGetBookingById() throws Exception {
        long bookingId = 1L;
        long bookerId = 1L;

        LocalDateTime start = LocalDateTime.of(2123, 1, 1, 9, 0);
        LocalDateTime end = LocalDateTime.of(2123, 2, 1, 9, 0);
        Booking booking = new Booking(bookingId, start, end, null, null, BookingStatus.WAITING);

        when(bookingService.getById(anyLong(), anyLong())).thenReturn(booking);

        mockMvc.perform(get(String.format("/bookings/%d", bookingId)).header(HEADER_USER_ID, bookerId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(booking)));
        verify(bookingService, times(1)).getById(anyLong(), anyLong());
    }

    @Test
    @DisplayName("Проверка метода получения бронирований бронирующего пользователя")
    void checkGetBookingsByBookerIdShouldReturnBookingListOfBookerByHisId() throws Exception {
        long bookerId = 1L;
        long bookingId1 = 1L;
        long bookingId2 = 2L;

        LocalDateTime start = LocalDateTime.of(2123, 1, 1, 9, 0);
        LocalDateTime end = LocalDateTime.of(2123, 2, 1, 9, 0);
        Booking booking1 = new Booking(bookingId1, start, end, null, null, BookingStatus.WAITING);
        Booking booking2 = new Booking(bookingId2, start, end, null, null, BookingStatus.WAITING);

        List<Booking> expectedList = List.of(booking1, booking2);

        when(bookingService.getBookingsByBookerId(anyLong(), any(), any())).thenReturn(expectedList);

        mockMvc.perform(get("/bookings").header(HEADER_USER_ID, bookerId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedList)));
        verify(bookingService, times(1)).getBookingsByBookerId(anyLong(), any(), any());
    }

    @Test
    @DisplayName("Проверка метода получения бронирований собственника вещей")
    void checkGetItemBookingsByOwnerIdShouldReturnBookingListOfOwnerByHisId() throws Exception {
        long ownerId = 1L;
        long bookingId1 = 1L;
        long bookingId2 = 2L;

        LocalDateTime start = LocalDateTime.of(2123, 1, 1, 9, 0);
        LocalDateTime end = LocalDateTime.of(2123, 2, 1, 9, 0);
        Booking booking1 = new Booking(bookingId1, start, end, null, null, BookingStatus.WAITING);
        Booking booking2 = new Booking(bookingId2, start, end, null, null, BookingStatus.WAITING);

        List<Booking> expectedList = List.of(booking1, booking2);

        when(bookingService.getItemBookingsByOwnerId(anyLong(), any(), any())).thenReturn(expectedList);

        mockMvc.perform(get("/bookings/owner").header(HEADER_USER_ID, ownerId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedList)));
        verify(bookingService,times(1)).getItemBookingsByOwnerId(anyLong(), any(), any());
    }
}