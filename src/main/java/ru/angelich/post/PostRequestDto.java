package ru.angelich.post;

import lombok.Data;

import java.util.List;

@Data
public class PostRequestDto {
    String title;
    String text;
    List<String> tags;
}
