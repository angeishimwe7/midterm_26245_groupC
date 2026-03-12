package com.example.portal.poratlmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Generic DTO for paginated responses
 * Contains both the data and pagination metadata
 * 
 * PAGINATION EXPLANATION:
 * - content: The actual data for current page
 * - pageNumber: Current page index (0-based)
 * - pageSize: Number of items per page
 * - totalElements: Total count of all items
 * - totalPages: Total number of pages
 * - isLast: Whether this is the last page
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagedResponseDTO<T> {
    private List<T> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean isLast;
}
