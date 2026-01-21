package ru.sellerbot.persistence.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.sellerbot.persistence.entity.PhonePriceEntity;

@Repository
public interface PhonePriceRepository extends JpaRepository<PhonePriceEntity, Long> {
    @Query("SELECT p FROM PhonePriceEntity p WHERE p.brand = :brand AND p.model = :model AND p.storageGb = :storageGb")
    Optional<PhonePriceEntity> findByBrandAndModelAndStorageGb(@Param("brand") String brand, @Param("model") String model, @Param("storageGb") Integer storageGb);
}
