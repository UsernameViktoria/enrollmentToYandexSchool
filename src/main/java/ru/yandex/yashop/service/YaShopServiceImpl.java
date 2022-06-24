package ru.yandex.yashop.service;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.yandex.temp.model.ShopUnit;
import ru.yandex.temp.model.ShopUnitImportRequest;
import ru.yandex.temp.model.ShopUnitStatisticResponse;
import ru.yandex.temp.model.ShopUnitType;
import ru.yandex.yashop.dao.entity.Products;
import ru.yandex.yashop.dao.service.HistoryDaoServiceImpl;
import ru.yandex.yashop.dao.service.ProductDaoServiceImpl;
import ru.yandex.yashop.dao.entity.ProductsUpdateHistory;
import ru.yandex.yashop.exсeption.NoFoundException;
import ru.yandex.yashop.mapper.ProductMapper;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.*;

@Service
public class YaShopServiceImpl implements YaShopService {


    private final ProductDaoServiceImpl productDaoService;
    private final HistoryDaoServiceImpl historyDaoService;

    public YaShopServiceImpl(ProductDaoServiceImpl yaShopDAOimpl, HistoryDaoServiceImpl historyDaoService) {
        this.productDaoService = yaShopDAOimpl;
        this.historyDaoService = historyDaoService;
    }

    /**
     * Метод, который удаляет элемент из базы данных по его uuid
     *
     * @param uuid
     */
    @Override
    @Transactional
    public void deleteIdDelete(UUID uuid) {
        productDaoService.deleteIdDelete(uuid);
    }

    /**
     * Метод, который добавляет или обновляет продукт/категорию*
     * В таблицу product_history всегда происходит добавление
     * В таблицу products происходит добавление или обновление
     *
     * @param shopUnitImportRequest продукт/категория которую необходимо добавить*
     */
    @Override
    @Transactional
    public void importsPost(@Valid ShopUnitImportRequest shopUnitImportRequest) {
        List<Products> productsList = new ArrayList<>();
        List<ProductsUpdateHistory> productsUpdateHistoryList = new ArrayList<>();
        Map<UUID, Products> categoryMap = new HashMap<>();
        Map<UUID, Products> productMap = new HashMap<>();
        shopUnitImportRequest.getItems().forEach(i -> {
            if (productDaoService.isRemoteById(i.getId())) {
                throw new NoFoundException();
            }
            Products p = ProductMapper.shopUnitImportToEntity(i, shopUnitImportRequest.getUpdateDate());
            productsList.add(p);
            productsUpdateHistoryList.add(ProductMapper
                    .shopUnitImportToEntityUpdateHistory(i, shopUnitImportRequest.getUpdateDate()));
            if (i.getType().equals(ShopUnitType.CATEGORY)) {
                categoryMap.put(p.getId(), p);
            }
            productMap.put(p.getId(), p);
        });

        shopUnitImportRequest.getItems().forEach(i -> {
            if (i.getParentId() != null) {
                Products parent = categoryMap.get(i.getParentId());
                productMap.get(i.getId()).setParent(
                        parent != null ?
                                parent : productDaoService.nodesIdGet(i.getParentId())
                );
            }
        });
        productDaoService.saveAll(productsList);
        setCotigorysPriceAndSave(productsUpdateHistoryList, productsList);
    }

    /**
     * Метод, используемый при добавлении/обновлении товара/категории, в котором происходит сохранение в базу и расчет цены категории
     *
     * @param productsUpdateHistoryList
     * @param productsList
     */
    private void setCotigorysPriceAndSave(List<ProductsUpdateHistory> productsUpdateHistoryList,
                                          List<Products> productsList) {

        productsList.forEach(p -> {
            if (p.getType().equals(ShopUnitType.CATEGORY) || !CollectionUtils.isEmpty(p.getChildren())) {
                productsUpdateHistoryList.stream().filter(h -> h.getUuid().equals(p.getId()))
                        .findFirst().orElseThrow().setPrice(setCotigorysPrice(p));
            }
        });
        historyDaoService.saveAll(productsUpdateHistoryList);
    }

    /**
     * Метод, используемый при добавлении/обновлении товара/категории, в котором происходит расчет средней категории,
     * если она имеет дочерние элементы
     *
     * @param products
     * @return
     */
    private Long setCotigorysPrice(Products products) {
        if (CollectionUtils.isEmpty(products.getChildren())) {
            return products.getPrice();
        } else {
            return products.getChildren().stream().mapToLong(s -> {
                if (s.getType().equals(ShopUnitType.CATEGORY) || !CollectionUtils.isEmpty(s.getChildren())) {
                    return setCotigorysPrice(s);
                } else {
                    return s.getPrice();
                }
            }).sum() / products.getChildren().size();
        }
    }

    /**
     * Метод, который возвращает информацию по товару/категории по его uuid
     *
     * @param uuid
     * @return ShopUnit
     */
    @Override
    public ShopUnit nodesIdGet(UUID uuid) {
        if (productDaoService.isNotRemoteById(uuid)) {
            return ProductMapper.entityToShopUnit(productDaoService.nodesIdGet(uuid));
        } else {
            throw new NoFoundException();
        }
    }


//----------------------------------------------------------------------

    /**
     * Метод, который показывает историю изменений по отпределенному товару/категории.
     * Временной период можно задать двумя датами, одной начальной, одной конечной либо не задать,
     * в таком случае будет выведена история изменений за все время
     *
     * @param uuid
     * @param offsetDateTime
     * @param offsetDateTime1
     * @return ShopUnitStatisticResponse
     */
    @Override
    public ShopUnitStatisticResponse nodeIdStatisticGet(UUID uuid, @Valid OffsetDateTime offsetDateTime,
                                                        @Valid OffsetDateTime offsetDateTime1) {
        return ProductMapper.getShopUnitStatisticResponse(historyDaoService
                .nodeIdStatisticGet(uuid, offsetDateTime, offsetDateTime1));
    }

    /**
     * Метод, который выводит  список товаров/категорий по которым происходили изменения/обновления за последние сутки
     *
     * @param offsetDateTime
     * @return ShopUnitStatisticResponse
     */
    @Override
    public ShopUnitStatisticResponse salesGet(@NotNull @Valid OffsetDateTime offsetDateTime) {
        return ProductMapper.listOfEntityToShopUnit(productDaoService.salesGet(offsetDateTime));
    }
}
