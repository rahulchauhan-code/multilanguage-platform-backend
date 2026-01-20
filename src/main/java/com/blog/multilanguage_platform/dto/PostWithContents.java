package com.blog.multilanguage_platform.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostWithContents {
    private Post post;
    private List<Post_content> contents;
}
