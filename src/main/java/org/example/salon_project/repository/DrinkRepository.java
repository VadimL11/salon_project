package org.example.salon_project.repository;

import org.example.salon_project.domain.Drink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DrinkRepository extends JpaRepository<Drink, Long> {
    List<Drink> findByAvailable(boolean available);
}
