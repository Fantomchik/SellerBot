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

    @Query("SELECT p FROM PriceRequestEntity p WHERE p.managerChatId = :managerChatId AND p.managerRequestMessageId = :managerRequestMessageId AND p.status = :status")
    Optional<PriceRequestEntity> findByManagerChatIdAndManagerRequestMessageIdAndStatus(
            @Param("managerChatId") Long managerChatId,
            @Param("managerRequestMessageId") Integer managerRequestMessageId,
            @Param("status") PriceRequestStatus status
    );

    @Query("SELECT p FROM PriceRequestEntity p WHERE p.modelKey = :modelKey AND p.status = :status")
    Optional<PriceRequestEntity> findByModelKeyAndStatus(
            @Param("modelKey") String modelKey,
            @Param("status") PriceRequestStatus status
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM PriceRequestEntity r WHERE r.modelKey = :modelKey AND r.status = :status")
    Optional<PriceRequestEntity> findByModelKeyAndStatusForUpdate(
            @Param("modelKey") String modelKey,
            @Param("status") PriceRequestStatus status
    );
}
