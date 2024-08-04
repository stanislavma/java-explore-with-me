package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exception.EntityNotFoundException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.repository.CategoryRepository;
import ru.practicum.ewm.service.CategoryService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private static final String CATEGORY_NOT_FOUND = "Категория не найдена - %d";
    private static final String CATEGORY_ALREADY_EXISTS = "Категория уже существует - %s";

    private final CategoryRepository categoryRepository;

    @Transactional
    public Category add(Category category) {
        validateIsNotExistByName(category);
        return categoryRepository.save(category);
    }

    @Transactional
    public void delete(Long catId) {
        validateIsExistById(catId);
        categoryRepository.deleteById(catId);
    }

    @Transactional
    public Category update(Long catId, Category updatedCategory) {
        Category category = getById(catId);
        validateIsNewNameNotExist(updatedCategory, category);
        category.setName(updatedCategory.getName());
        return categoryRepository.save(category);
    }

    public List<Category> getAll(int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        return categoryRepository.findAll(pageRequest).getContent();
    }

    public Category getById(Long catId) {
        return categoryRepository.findById(catId)
                .orElseThrow(() -> {
                    log.error(String.format(CATEGORY_NOT_FOUND, catId));
                    return new EntityNotFoundException(String.format(CATEGORY_NOT_FOUND, catId));
                });
    }

    private void validateIsNotExistByName(Category category) {
        if (isExistsByName(category)) {
            createCategoryAlreadyExistException(category);
        }
    }

    private void validateIsNewNameNotExist(Category updatedCategory, Category category) {
        if (!category.getName().equals(updatedCategory.getName()) && isExistsByName(updatedCategory)) {
            createCategoryAlreadyExistException(updatedCategory);
        }
    }

    private boolean isExistsByName(Category updatedCategory) {
        return categoryRepository.existsByName(updatedCategory.getName());
    }

    private void validateIsExistById(Long catId) {
        if (!categoryRepository.existsById(catId)) {
            log.error(String.format(CATEGORY_NOT_FOUND, catId));
            throw new EntityNotFoundException(String.format(CATEGORY_NOT_FOUND, catId));
        }
    }

    private static void createCategoryAlreadyExistException(Category category) {
        throw new ValidationException(String.format(CATEGORY_ALREADY_EXISTS, category.getName()), HttpStatus.CONFLICT);
    }

}