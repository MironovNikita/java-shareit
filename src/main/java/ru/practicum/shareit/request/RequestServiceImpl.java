package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.common.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {
    private final RequestMapper requestMapper;
    private final UserService userService;
    private final RequestRepository requestRepository;
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    @Override
    @Transactional
    public RequestDto create(long userId, RequestDto requestDto) {
        User user = userService.get(userId);

        Request request = requestMapper.transformRequestDtoToRequest(requestDto);
        request.setCreated(LocalDateTime.now());
        request.setUser(user);
        requestRepository.save(request);

        return requestMapper.transformRequestToRequestDto(request);
    }

    @Override
    public List<RequestDto> getOwnRequests(long userId) {
        userService.get(userId);

        return requestRepository.findAllByUserIdOrderByCreatedDesc(userId)
                .stream()
                .map(this::creatingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RequestDto> getOtherUsersRequests(long userId, Pageable pageable) {
        userService.get(userId);

        return requestRepository.findAllByUserIdIsNotOrderByCreatedDesc(userId, pageable)
                .stream()
                .map(this::creatingDto)
                .collect(Collectors.toList());
    }

    @Override
    public RequestDto get(long userId, long requestId) {
        userService.get(userId);

        Request request = requestRepository.findById(requestId).orElseThrow(() -> {
            log.error("Запрос по ID {} на вещь не найден!", requestId);
            throw new ObjectNotFoundException("Запрос", requestId);
        });

        return creatingDto(request);
    }

    private RequestDto creatingDto(Request request) {
        RequestDto requestDto = requestMapper.transformRequestToRequestDto(request);
        List<ItemDto> items = itemRepository.findAllByRequestId(request.getId())
                .stream()
                .map(itemMapper::transformItemToItemDto)
                .collect(Collectors.toList());
        requestDto.setItems(items);

        return requestDto;
    }
}
