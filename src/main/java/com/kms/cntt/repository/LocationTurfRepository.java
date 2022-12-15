package com.kms.cntt.repository;

import com.kms.cntt.model.LocationTurf;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LocationTurfRepository extends JpaRepository<LocationTurf, UUID> {
  Optional<LocationTurf> findByIdAndDeletedAtIsNull(UUID id);
  Page<LocationTurf> findAllByDeletedAtIsNull(Pageable pageable);
}
