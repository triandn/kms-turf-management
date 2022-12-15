package com.kms.cntt.repository;

import com.kms.cntt.model.Turf;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TurfRepository extends JpaRepository<Turf, UUID> {
  Optional<Turf> findByIdAndDeletedAtIsNull(UUID id);

  Page<Turf> findAllByLocationTurfIdAndDeletedAtIsNull(Pageable pageable, UUID locationTurfId);
}
