package ru.sellerbot.persistence.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.sellerbot.persistence.entity.PriceRequestWaiterEntity;

@Repository
public interface PriceRequestWaiterRepository extends JpaRepository<PriceRequestWaiterEntity, Long> {

    @Query("SELECT CASE WHEN COUNT(w) > 0 THEN true ELSE false END FROM PriceRequestWaiterEntity w WHERE w.request.id = :requestId AND w.waiterChatId = :waiterChatId")
    boolean existsByRequest_IdAndWaiterChatId(@Param("requestId") Long requestId, @Param("waiterChatId") Long waiterChatId);

    @Query("SELECT w FROM PriceRequestWaiterEntity w WHERE w.request.id = :requestId AND w.waiterChatId = :waiterChatId")
    Optional<PriceRequestWaiterEntity> findByRequest_IdAndWaiterChatId(@Param("requestId") Long requestId, @Param("waiterChatId") Long waiterChatId);

    @Query("SELECT w FROM PriceRequestWaiterEntity w WHERE w.request.id = :requestId")
    List<PriceRequestWaiterEntity> findAllByRequest_Id(@Param("requestId") Long requestId);
}
