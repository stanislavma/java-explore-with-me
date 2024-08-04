package ru.practicum.ewm.service;

import ru.practicum.ewm.model.Category;

import java.util.List;

public interface CategoryService {

    Category add(Category category);

    void delete(Long catId);

    Category update(Long catId, Category category);

    List<Category> getAll(int from, int size);

    Category getById(Long catId);

}