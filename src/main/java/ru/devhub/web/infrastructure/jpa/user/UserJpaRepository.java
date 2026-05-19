package ru.devhub.web.infrastructure.jpa.user;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, UUID> {
    Optional<UserJpaEntity> findByEmail(String email);


    @Query("""
       select u from UserJpaEntity u
       where lower(u.name)  like lower(concat('%', :q, '%'))
          or lower(u.email) like lower(concat('%', :q, '%'))
       order by
         case when lower(u.name) like lower(concat(:q, '%')) then 0 else 1 end,
         u.name asc
       """)
    List<UserJpaEntity> searchAllByNameOrEmail(@Param("q") String query, Pageable pageable);
}
