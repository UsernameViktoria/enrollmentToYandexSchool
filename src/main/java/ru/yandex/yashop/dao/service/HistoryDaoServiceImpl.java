package ru.yandex.yashop.dao.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.yandex.yashop.dao.entity.ProductsUpdateHistory;
import ru.yandex.yashop.dao.repository.ProductRepository;
import ru.yandex.yashop.dao.repository.ProductsUpdateHistoryRepository;
import ru.yandex.yashop.exсeption.NoFoundException;

import javax.validation.Valid;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;


@Repository
public class HistoryDaoServiceImpl implements HistoryDaoService {

    private final ProductsUpdateHistoryRepository productsUpdateHistoryRepository;
    private final ProductRepository productRepository;

    @Autowired
    public HistoryDaoServiceImpl(ProductsUpdateHistoryRepository productsUpdateHistoryRepository, ProductRepository productRepository) {
        this.productsUpdateHistoryRepository = productsUpdateHistoryRepository;
        this.productRepository = productRepository;
    }

    /**
     * Метод, сохраняющий в базу список товаров/категорий (таблица products_update_history)
     *
     * @param productsUpdateHistoryList
     * @return
     */
    @Override
    public void saveAll(List<ProductsUpdateHistory> productsUpdateHistoryList) {
        productsUpdateHistoryRepository.saveAll(productsUpdateHistoryList);
    }

    /**
     * Метод, который возвращает true, если товар существует в базе и не удален (таблица products)
     *
     * @param uuid
     * @return
     */
    @Override
    public boolean isNotRemoteById(UUID uuid) {
        return productRepository.findByIdAndRemote(uuid, false).isPresent();
    }

    /**
     * Метод, который показывает историю изменений по отпределенному товару/категории.
     * Временной период можно задать двумя датами, одной начальной, одной конечной либо не задать,
     * в таком случае будет выведена история изменений за все время (таблица products_update_history)
     *
     * @param uuid
     * @param dataS
     * @param dataPo
     * @return List<ProductsUpdateHistory>
     */
    @Override
    public List<ProductsUpdateHistory> nodeIdStatisticGet(UUID uuid, @Valid OffsetDateTime dataS, @Valid OffsetDateTime dataPo) {
        if (isNotRemoteById(uuid)) {
            List<ProductsUpdateHistory> result;
            if (dataS == null) {
                if (dataPo == null) {
                    return productsUpdateHistoryRepository.findAllByUuid(uuid);
                } else {
                    result = productsUpdateHistoryRepository.findAllByIdAndDateBefore(uuid, dataPo);
                }
            } else {
                if (dataPo == null) {
                    result = productsUpdateHistoryRepository.findAllByIdAndDateAfter(uuid, dataS);
                } else {
                    result = productsUpdateHistoryRepository.findAllByIdAndDateBetween(uuid, dataS, dataPo);
                }
            }
            return result;
        } else {
            throw new NoFoundException();
        }
    }
}
