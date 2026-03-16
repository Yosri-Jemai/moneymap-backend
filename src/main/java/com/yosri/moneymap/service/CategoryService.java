package com.yosri.moneymap.service;

import com.yosri.moneymap.dto.CategoryDTO;
import com.yosri.moneymap.entity.Category;
import com.yosri.moneymap.entity.Profile;
import com.yosri.moneymap.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final ProfileService profileService;
    private final CategoryRepository categoryRepository;

    private Category toEntity(CategoryDTO categoryDTO, Profile profile) {
        return Category.builder()
                .name(categoryDTO.getName())
                .type(categoryDTO.getType())
                .icon(categoryDTO.getIcon())
                .profile(profile)
                .build();
    }

    private CategoryDTO toDTO(Category category) {
        return CategoryDTO.builder()
                .name(category.getName())
                .type(category.getType())
                .icon(category.getIcon())
                .profile_id(category.getProfile()!=null ? category.getProfile().getId():null)
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }

    public CategoryDTO saveCategory(CategoryDTO categoryDTO) {
        Profile profile = profileService.getCurrentProfile();
        if (categoryRepository.existsByNameAndProfileId(categoryDTO.getName(), profile.getId())){
            throw new RuntimeException("Category already exists");
        }
        return toDTO(categoryRepository.save(toEntity(categoryDTO, profile)));
    }

    public List<CategoryDTO> getCategoriesCurrentUser(){
        List<Category> categories = categoryRepository.findByProfileId(profileService.getCurrentProfile().getId());
        return categories.stream().map(this::toDTO).toList();
    }

    public List<CategoryDTO> getCategoriesCurrentUserByType(String type){
        List<Category> categories = categoryRepository.findByTypeAndProfileId(type, profileService.getCurrentProfile().getId());
        return categories.stream().map(this::toDTO).toList();
    }

    public CategoryDTO updateCategory(Long categoryId, CategoryDTO categoryDTO) {
        Category category = categoryRepository.findByIdAndProfileId(categoryId, profileService.getCurrentProfile().getId()).orElseThrow(
                () -> new RuntimeException("Category not found"));
        category.setName(categoryDTO.getName());
        category.setType(categoryDTO.getType());
        category.setIcon(categoryDTO.getIcon());
        categoryRepository.save(category);
        return toDTO(category);
    }

    public void deleteCategory(Long categoryId) {
        categoryRepository.deleteById(categoryId);
    }
}
