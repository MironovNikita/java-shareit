package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findAllByUserIdOrderByCreatedDesc(long userId);

    List<Request> findAllByUserIdIsNotOrderByCreatedDesc(long userId, Pageable pageable);
}
