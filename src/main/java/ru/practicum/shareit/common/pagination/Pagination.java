package ru.practicum.shareit.common.pagination;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Pagination {
    public static Pageable splitByPages(Integer from, Integer size) {
        if (from == null || size == null) {
            return null;
        }

        return PageRequest.of(from / size, size);
    }
}
