package com.tomcvt.brickshop.pagination;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;

public class SimplePage<T> {
    List<T> content;
    Long totalElements;
    int number;
    boolean last;
    boolean first;

    public SimplePage(List<T> content, Long totalElements, int number, boolean last, boolean first) {
        this.content = content;
        this.totalElements = totalElements;
        this.number = number;
        this.last = last;
        this.first = first;
    }
    public <U> SimplePage<U> map(Function<? super T, ? extends U> mapper) {
        List<U> mappedContent = this.content.stream()
                .map(mapper)
                .collect(Collectors.toList());
        return new SimplePage<>(
                mappedContent,
                this.totalElements,
                this.number,
                this.last,
                this.first
        );
    }

    public static <T> SimplePage<T> of(List<T> content, Page<?> page) {
        return new SimplePage<>(
                content,
                page.getTotalElements(),
                page.getNumber(),
                page.isLast(),
                page.isFirst()
        );
    }
    public static <T> SimplePage<T> fromPage(Page<T> page) {
        return new SimplePage<>(
                page.getContent(),
                page.getTotalElements(),
                page.getNumber(),
                page.isLast(),
                page.isFirst()
        );
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
    public List<T> getContent() {
        return content;
    }
    public Long getTotalElements() {
        return totalElements;
    }
    public int getNumber() {
        return number;
    }
    public boolean isLast() {
        return last;
    }
    public boolean isFirst() {
        return first;
    }
}
