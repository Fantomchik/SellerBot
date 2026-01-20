package ru.sellerbot.persistence.repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.sellerbot.persistence.entity.PriceRequestEntity;
import ru.sellerbot.persistence.entity.PriceRequestStatus;

@Repository
public interface PriceRequestRepository extends JpaRepository<PriceRequestEntity, Long> {

    Optional<PriceRequestEntity> findByManagerChatIdAndManagerRequestMessageIdAndStatus(
            Long managerChatId,
            Integer managerRequestMessageId,
            PriceRequestStatus status
    );

    Optional<PriceRequestEntity> findByModelKeyAndStatus(String modelKey, PriceRequestStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from PriceRequestEntity r where r.modelKey = :modelKey and r.status = :status")
    Optional<PriceRequestEntity> findByModelKeyAndStatusForUpdate(
            @Param("modelKey") String modelKey,
            @Param("status") PriceRequestStatus status
    );
}

