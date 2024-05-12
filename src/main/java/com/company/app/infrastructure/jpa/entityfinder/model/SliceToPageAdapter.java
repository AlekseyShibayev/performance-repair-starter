package com.company.app.infrastructure.jpa.entityfinder.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;
import java.util.function.Function;

/**
 * Pattern adapter.
 * If desired method return Page, and you need optimise it, because nobody used int total - use this class.
 */
public class SliceToPageAdapter<E> extends SliceImpl<E> implements Page<E> {

    private static final String EXCEPTION_MESSAGE = "All work do Slice. Page methods with total amount not supported. If you need 'total' - use original PageImpl.";

    /**
     * Constructors
     */
    private SliceToPageAdapter(List<E> content, Pageable pageable, boolean hasNext) {
        super(content, pageable, hasNext);
    }

    public static <E> Page<E> of (Slice<E> slice) {
        return new SliceToPageAdapter<>(slice.getContent(), slice.getPageable(), slice.hasNext());
    }

    /**
     * Page methods
     */
    @Override
    public int getTotalPages() {
        throw new UnsupportedOperationException(EXCEPTION_MESSAGE);
    }

    @Override
    public long getTotalElements() {
        throw new UnsupportedOperationException(EXCEPTION_MESSAGE);
    }

    @Override
    public <U> Page<U> map(Function<? super E, ? extends U> converter) {
        return SliceToPageAdapter.of(super.map(converter));
    }

    /**
     * for infinite scroll
     * if hasNext = true, return -1
     * if hasNext = false, return -2
     */
    @JsonGetter("totalPages")
    public int prepareTotalPagesForDispatcherServletAndJackson() {
        return hasNext() ? -1 : -2;
    }

    @JsonGetter("totalElements")
    public int prepareTotalElementsForDispatcherServletAndJackson() {
        return hasNext() ? -1 : -2;
    }

}