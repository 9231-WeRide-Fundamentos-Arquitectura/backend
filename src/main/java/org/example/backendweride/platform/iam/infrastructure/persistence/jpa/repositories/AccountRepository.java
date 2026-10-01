package org.example.backendweride.platform.iam.infrastructure.persistence.jpa.repositories;

import org.example.backendweride.platform.iam.domain.model.aggregates.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * AccountRepository interface for managing Account entities in the database.
 *
 * @repository annotation indicates that this interface is a Spring Data repository.
 * @summary This interface extends JpaRepository to provide CRUD operations for Account entities.
 */
@Repository
public interface AccountRepository extends JpaRepository<Account,Long> {
    boolean existsByUserName(String userName);
    Optional<Account> findByUserName(String userName);

    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select a from Account a where a.id = :id")
    Optional<Account> findLockedById(@org.springframework.data.repository.query.Param("id") Long id);

    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select a from Account a where a.userName = :username")
    Optional<Account> findLockedByUserName(@org.springframework.data.repository.query.Param("username") String username);
}
