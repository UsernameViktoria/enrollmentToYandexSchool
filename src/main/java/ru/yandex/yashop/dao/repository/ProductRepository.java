package ru.yandex.yashop.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.temp.model.ShopUnitType;
import ru.yandex.yashop.dao.entity.Products;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Products, UUID> {

    /**
     * Метод, который удаляет элемент из базы данных по его uuid (таблица products)
     *
     * @param uuid
     */
    @Override
    void deleteById(UUID uuid);

    /**
     * @param uuid   uuid
     * @param remote флаг,указывающий удален ли обьект
     * @return Optional<Products>
     */
    Optional<Products> findByIdAndRemote(UUID uuid, boolean remote);

    /**
     * @param data1  дата, переданная в запросе
     * @param data2  data1 - 24 hours
     * @param remote флаг,указывающий удален ли обьект
     * @return List<Products>
     */
    List<Products> findAllByDateBetweenAndRemoteAndType(LocalDateTime data1,
                                                        LocalDateTime data2, boolean remote, ShopUnitType shopUnitType);

}
