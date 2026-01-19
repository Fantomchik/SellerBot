package ru.sellerbot.persistence.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sellerbot.persistence.entity.PhonePriceEntity;

@Repository
public interface PhonePriceRepository extends JpaRepository<PhonePriceEntity, Long> {
    Optional<PhonePriceEntity> findByModelIgnoreCase(String model);
}
