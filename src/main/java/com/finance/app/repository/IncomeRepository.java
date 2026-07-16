package com.finance.app.repository;

import com.finance.app.entity.Income;
import com.finance.app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IncomeRepository extends JpaRepository<Income, Long> {
    List<Income> findByUserOrderByDateDesc(User user);
    List<Income> findByUser(User user);
}
