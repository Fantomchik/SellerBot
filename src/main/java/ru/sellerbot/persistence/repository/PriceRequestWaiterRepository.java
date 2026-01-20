package ru.sellerbot.persistence.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sellerbot.persistence.entity.PriceRequestWaiterEntity;

@Repository
public interface PriceRequestWaiterRepository extends JpaRepository<PriceRequestWaiterEntity, Long> {

    boolean existsByRequest_IdAndWaiterChatId(Long requestId, Long waiterChatId);

    Optional<PriceRequestWaiterEntity> findByRequest_IdAndWaiterChatId(Long requestId, Long waiterChatId);

    List<PriceRequestWaiterEntity> findAllByRequest_Id(Long requestId);
}

