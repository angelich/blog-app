package ru.angelich.posts.models;

import lombok.Data;

import java.util.List;

@Data
public class PostRequest {
    private String title;
    private String text;
    private List<String> tags;
}
