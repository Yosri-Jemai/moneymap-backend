package com.yosri.moneymap.repository;

import com.yosri.moneymap.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category,Long> {
    List<Category> findByProfileId(Long id);
    Optional<Category> findByIdAndProfileId(Long id,Long profileId);
    List<Category> findByTypeAndProfileId(String type, Long profileId);
    boolean existsByNameAndProfileId(String name, Long profileOd);
}
