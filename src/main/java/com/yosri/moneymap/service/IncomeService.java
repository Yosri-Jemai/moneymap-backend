package com.yosri.moneymap.service;

import com.yosri.moneymap.dto.IncomeDTO;
import com.yosri.moneymap.entity.Category;
import com.yosri.moneymap.entity.Income;
import com.yosri.moneymap.entity.Profile;
import com.yosri.moneymap.repository.CategoryRepository;
import com.yosri.moneymap.repository.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncomeService {

    public final CategoryRepository categoryRepository;
    public final IncomeRepository incomeRepository;
    public final ProfileService profileService;

    private Income toEntity(IncomeDTO incomeDTO, Profile profile, Category category) {
        return Income.builder()
                .name(incomeDTO.getName())
                .icon(incomeDTO.getIcon())
                .amount(incomeDTO.getAmount())
                .date(incomeDTO.getDate())
                .profile(profile)
                .category(category)
                .build();
    }

    private IncomeDTO toDTO(Income income) {
        return IncomeDTO.builder()
                .id(income.getId())
                .name(income.getName())
                .icon(income.getIcon())
                .categoryId(income.getCategory().getId())
                .categoryName(income.getCategory() != null ? income.getCategory().getName() : "N/A")
                .amount(income.getAmount())
                .date(income.getDate())
                .createdAt(income.getCreatedAt())
                .updatedAt(income.getUpdatedAt())
                .build();
    }

    public IncomeDTO addIncome(IncomeDTO incomeDTO) {
        Profile profile = profileService.getCurrentProfile();
        Category category = categoryRepository.findById(incomeDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        Income income = toEntity(incomeDTO,profile,category);
        return toDTO(incomeRepository.save(income));
    }

    public List<IncomeDTO> getCurrentMonthIncomesForCurrentUser() {
        Profile profile = profileService.getCurrentProfile();
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());
        List<Income> list = incomeRepository.findByProfileIdAndDateBetween(profile.getId(),
                startDate, endDate);
        return list.stream().map(this::toDTO).toList();
    }

    public void deleteIncome(Long incomeId) {
        Profile profile = profileService.getCurrentProfile();
        Income income = incomeRepository.findById(incomeId)
                .orElseThrow(() -> new RuntimeException("Income not found"));
        if (!income.getProfile().getId().equals(profile.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized to delete this income");
        }
        incomeRepository.deleteById(incomeId);
    }

    public List<IncomeDTO> getLatest5IncomesForCurrentUser() {
        Profile profile = profileService.getCurrentProfile();
        List<Income> incomes = incomeRepository.findTop5ByProfileIdOrderByDateDesc(profile.getId());
        return incomes.stream().map(this::toDTO).toList();
    }

    public BigDecimal getTotalIncomeForCurrentUser() {
        Profile profile = profileService.getCurrentProfile();
        BigDecimal total = incomeRepository.findTotalIncomeByProfileId(profile.getId());
        return total != null ? total : BigDecimal.ZERO;
    }

    public List<IncomeDTO> filterIncomes(LocalDate startDate, LocalDate endDate, String keyword, Sort sort) {
        Profile profile =  profileService.getCurrentProfile();
        List<Income> incomes = incomeRepository.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(profile.getId(), startDate, endDate, keyword, sort);
        return incomes.stream().map(this::toDTO).toList();
    }
}
