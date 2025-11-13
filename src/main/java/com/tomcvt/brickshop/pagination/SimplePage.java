package com.tomcvt.brickshop.pagination;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;

public class SimplePage<U> {
    List<U> content;
    Long totalElements;
    int number;
    boolean last;
    boolean first;

    public SimplePage(List<U> content, Long totalElements, int number, boolean last, boolean first) {
        this.content = content;
        this.totalElements = totalElements;
        this.number = number;
        this.last = last;
        this.first = first;
    }

    public static <T, U> SimplePage<U> from(Page<T> page, Function<? super T, ? extends U> converter) {
        List<U> convertedContent = page.getContent().stream()
                .map(converter)
                .collect(Collectors.toList());
        return new SimplePage<>(
                convertedContent,
                page.getTotalElements(),
                page.getNumber(),
                page.isLast(),
                page.isFirst()
        );
    }
}
