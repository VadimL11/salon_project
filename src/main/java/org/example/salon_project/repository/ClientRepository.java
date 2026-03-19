package org.example.salon_project.repository;

import org.example.salon_project.domain.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByEmailIgnoreCase(String email);

    Optional<Client> findByExternalId(String externalId);
}
