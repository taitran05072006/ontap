package com.example.demo.dto.category;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CategoryResponse {
    private long id;
    private String name;
    private String description;
}
