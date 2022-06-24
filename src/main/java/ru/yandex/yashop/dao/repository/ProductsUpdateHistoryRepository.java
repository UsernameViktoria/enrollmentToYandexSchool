package ru.yandex.yashop.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.yandex.yashop.dao.entity.ProductsUpdateHistory;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ProductsUpdateHistoryRepository extends JpaRepository<ProductsUpdateHistory, Integer> {

    /**
     * Метод, который удаляет список элементов из базы данных по его uuid (таблица products_update_history) (мягкое удаление)
     *
     * @param uuidList
     */

    @Query(nativeQuery = true, value = "UPDATE products_update_history SET remote=true where uuid in :uuidList")
    @Modifying
    void deleteByIdIn(List<UUID> uuidList);

    List<ProductsUpdateHistory> findAllByUuid(UUID uuid);

    /**
     * @param id        uuid
     * @param startDate дата с
     * @return List ProductUpdateHistoryRepository
     */
    List<ProductsUpdateHistory> findAllByIdAndDateAfter(UUID id, OffsetDateTime startDate);

    /**
     * @param id      uuid
     * @param endDate дата по
     * @return List ProductUpdateHistoryRepository
     */
    List<ProductsUpdateHistory> findAllByIdAndDateBefore(UUID id, OffsetDateTime endDate);

    /**
     * @param id        uuid
     * @param startDate дата по
     * @param endDate   дата по
     * @return List ProductUpdateHistoryRepository
     */

    List<ProductsUpdateHistory> findAllByIdAndDateBetween(UUID id, OffsetDateTime startDate, OffsetDateTime endDate);


}
