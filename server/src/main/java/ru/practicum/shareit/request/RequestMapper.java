package ru.practicum.shareit.request;

import org.mapstruct.Mapper;
import ru.practicum.shareit.request.dto.RequestDto;

@Mapper(componentModel = "spring")
public interface RequestMapper {
    Request transformRequestDtoToRequest(RequestDto requestDto);

    RequestDto transformRequestToRequestDto(Request request);
}
