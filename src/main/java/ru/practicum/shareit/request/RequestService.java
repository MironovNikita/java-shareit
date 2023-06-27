package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.RequestDto;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RequestService {
    RequestDto create(long userId, RequestDto requestDto);

    List<RequestDto> getOwnRequests(long userId);

    List<RequestDto> getOtherUsersRequests(long userId, Pageable pageable);

    RequestDto get(long userId, long requestId);
}
