package ru.yandex.yashop.dao.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import ru.yandex.temp.model.ShopUnitType;
import ru.yandex.yashop.dao.entity.Products;
import ru.yandex.yashop.dao.repository.ProductRepository;
import ru.yandex.yashop.dao.repository.ProductsUpdateHistoryRepository;
import ru.yandex.yashop.exсeption.NoFoundException;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class ProductDaoServiceImpl implements ProductDaoService {

    private final ProductRepository productRepository;
    private final ProductsUpdateHistoryRepository productsUpdateHistoryRepository;

    @Autowired
    public ProductDaoServiceImpl(ProductRepository productRepository, ProductsUpdateHistoryRepository productsUpdateHistoryRepository) {
        this.productRepository = productRepository;
        this.productsUpdateHistoryRepository = productsUpdateHistoryRepository;
    }

    /**
     * Метод, который удаляет элемент из базы данных по его uuid (таблица products)
     *
     * @param id
     */
    @Override
    @Transactional
    public void deleteIdDelete(UUID id) {
        Products products = productRepository.findById(id).orElseThrow(NoFoundException::new);
        List<UUID> uuidList = new ArrayList<>();
        uuidList.add(delete(products, uuidList));
        productRepository.save(products);
        productsUpdateHistoryRepository.deleteByIdIn(uuidList);
    }

    /**
     * Метод каскадного удаления товара, если он имеет дочерние элементы.Используется в deleteIdDelete методе.
     *
     * @param products
     * @param uuidList
     * @return
     */
    private UUID delete(Products products, List<UUID> uuidList) {
        products.setRemote(true);
        if (!CollectionUtils.isEmpty(products.getChildren())) {
            for (Products prod : products.getChildren()) {
                uuidList.add(delete(prod, uuidList));
            }
        }
        return products.getId();
    }

    /**
     * Метод, который возвращает информацию по товару/категории по его uuid
     *
     * @param uuid
     * @return
     */
    @Override
    public Products nodesIdGet(UUID uuid) {
        return productRepository.findById(uuid).orElseThrow(NoFoundException::new);
    }

    /**
     * Метод, который выводит  список товаров/категорий по которым происходили изменения/обновления за последние сутки
     *
     * @param offsetDateTime
     * @return List<Products>
     */
    @Override
    public List<Products> salesGet(@NotNull @Valid OffsetDateTime offsetDateTime) {
        LocalDateTime data2 = offsetDateTime.toLocalDateTime();
        System.out.println(data2);
        LocalDateTime data1 = data2.minusHours(24L);
        System.out.println(data1);
        return productRepository.findAllByDateBetweenAndRemoteAndType(data1, data2, false, ShopUnitType.OFFER);
    }

    /**
     * Метод, сохраняющий в базу список товаров/категорий (таблица products)
     *
     * @param productsList
     * @return
     */
    @Override
    public List<Products> saveAll(@Valid List<Products> productsList) {
        return productRepository.saveAll(productsList);
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
     * Метод, который возвращает true, если товар был добавлен в базу и удален (таблица products)
     *
     * @param uuid
     * @return
     */
    @Override
    public boolean isRemoteById(UUID uuid) {
        return productRepository.findByIdAndRemote(uuid, true).isPresent();
    }

}
