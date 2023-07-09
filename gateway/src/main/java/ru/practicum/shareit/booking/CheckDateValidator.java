package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingGatewayDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class CheckDateValidator implements ConstraintValidator<StartBeforeEndDateValid, BookingGatewayDto> {
    @Override
    public void initialize(StartBeforeEndDateValid constraintAnnotation) {
    }

    @Override
    public boolean isValid(BookingGatewayDto bookingGatewayDto, ConstraintValidatorContext constraintValidatorContext) {
        LocalDateTime start = bookingGatewayDto.getStart();
        LocalDateTime end = bookingGatewayDto.getEnd();

        if (start == null || end == null) {
            return false;
        }

        return start.isBefore(end);
    }
}
