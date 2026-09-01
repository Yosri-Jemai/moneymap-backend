package com.yosri.moneymap.service;

import com.yosri.moneymap.dto.ExpenseDTO;
import com.yosri.moneymap.entity.Category;
import com.yosri.moneymap.entity.Expense;
import com.yosri.moneymap.entity.Profile;
import com.yosri.moneymap.repository.CategoryRepository;
import com.yosri.moneymap.repository.ExpenseRepository;
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
public class ExpenseService {

    public final CategoryRepository categoryRepository;
    public final ExpenseRepository expenseRepository;
    public final ProfileService profileService;

    private Expense toEntity(ExpenseDTO expenseDTO,Profile profile, Category category) {
        return Expense.builder()
                .name(expenseDTO.getName())
                .icon(expenseDTO.getIcon())
                .amount(expenseDTO.getAmount())
                .date(expenseDTO.getDate())
                .profile(profile)
                .category(category)
                .build();
    }

    private ExpenseDTO toDTO(Expense expense) {
        return ExpenseDTO.builder()
                .id(expense.getId())
                .name(expense.getName())
                .icon(expense.getIcon())
                .categoryId(expense.getCategory().getId())
                .categoryName(expense.getCategory() != null ? expense.getCategory().getName() : "N/A")
                .amount(expense.getAmount())
                .date(expense.getDate())
                .createdAt(expense.getCreatedAt())
                .updatedAt(expense.getUpdatedAt())
                .build();
    }

    public ExpenseDTO addExpense(ExpenseDTO expenseDTO) {
        Profile profile = profileService.getCurrentProfile();
        Category category = categoryRepository.findById(expenseDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        Expense expense = toEntity(expenseDTO,profile,category);
        return toDTO(expenseRepository.save(expense));
    }

    public List<ExpenseDTO> getCurrentMonthExpensesForCurrentUser() {
        Profile profile = profileService.getCurrentProfile();
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());
        List<Expense> list = expenseRepository.findByProfileIdAndDateBetween(profile.getId(),
                startDate, endDate);
        return list.stream().map(this::toDTO).toList();
    }

    public void deleteExpense(Long expenseId) {
        Profile profile = profileService.getCurrentProfile();
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found"));
        if (!expense.getProfile().getId().equals(profile.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized to delete this expense");
        }
        expenseRepository.deleteById(expenseId);
    }

    public List<ExpenseDTO> getLatest5ExpensesForCurrentUser() {
        Profile profile = profileService.getCurrentProfile();
        List<Expense> expenses = expenseRepository.findTop5ByProfileIdOrderByDateDesc(profile.getId());
        return expenses.stream().map(this::toDTO).toList();
    }

    public BigDecimal getTotalExpenseForCurrentUser() {
        Profile profile = profileService.getCurrentProfile();
        BigDecimal total = expenseRepository.findTotalExpenseByProfileId(profile.getId());
        return total != null ? total : BigDecimal.ZERO;
    }

    public List<ExpenseDTO> filterExpenses(LocalDate startDate, LocalDate endDate, String keyword, Sort sort) {
        Profile profile =  profileService.getCurrentProfile();
        List<Expense> expenses = expenseRepository.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(profile.getId(), startDate, endDate, keyword, sort);
        return expenses.stream().map(this::toDTO).toList();
    }
}
