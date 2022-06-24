package ru.yandex.yashop.mapper;


import org.springframework.util.CollectionUtils;
import ru.yandex.temp.model.*;
import ru.yandex.yashop.dao.entity.Products;
import ru.yandex.yashop.dao.entity.ProductsUpdateHistory;
import ru.yandex.yashop.exсeption.ValidException;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ProductMapper {

    public static Products shopUnitImportToEntity(ShopUnitImport shopUnitImport, OffsetDateTime updateTime) {
        return Products.builder().id(shopUnitImport.getId())
                .name(shopUnitImport.getName())
                .date(updateTime.toLocalDateTime())
                .type(shopUnitImport.getType())
                .price(shopUnitImport.getPrice())
                .remote(false)
                .build();
    }

    public static ProductsUpdateHistory shopUnitImportToEntityUpdateHistory(ShopUnitImport shopUnitImport, OffsetDateTime updateTime) {
        if (shopUnitImport.getType().equals(ShopUnitType.OFFER) && shopUnitImport.getPrice() == null) {
            throw new ValidException("У товара необходимо ввести цену");
        }
        return ProductsUpdateHistory.builder().uuid(shopUnitImport.getId())
                .name(shopUnitImport.getName())
                .date(updateTime.toLocalDateTime())
                .parentId(shopUnitImport.getParentId())
                .type(shopUnitImport.getType())
                .price(shopUnitImport.getPrice())
                .remote(false)
                .build();
    }

    public static ShopUnit entityToShopUnit(Products prd) {
        List<Long> longs = new ArrayList<>();
        ShopUnit shopUnit = new ShopUnit().id(prd.getId())
                .name(prd.getName())
                .date(prd.getDate().atOffset(ZoneOffset.UTC))
                .parentId(prd.getParent() != null ? prd.getParent().getId() : null)
                .type(prd.getType())
                .price(prd.getPrice());
        if (!CollectionUtils.isEmpty(prd.getChildren())) {
            shopUnit.setChildren(prd.getChildren().stream().map(ProductMapper::entityToShopUnit).collect(Collectors.toList()));
            long l = shopUnit.getChildren().stream().map(ShopUnit::getPrice)
                    .filter(Objects::nonNull)
                    .reduce(Long::sum).orElse(0L);
            shopUnit.setPrice(l / shopUnit.getChildren().size());
        }

        return shopUnit;
    }

    public static ShopUnitStatisticResponse listOfEntityToShopUnit(List<Products> prd) {

        ShopUnitStatisticResponse response = new ShopUnitStatisticResponse();
        prd.forEach(s -> {
            response.addItemsItem(entityProdToShopUnitStatisticUnit(s));
        });
        return response;
    }

    public static ShopUnitStatisticUnit entityProdToShopUnitStatisticUnit(Products product) {
        return new ShopUnitStatisticUnit().id(product.getId())
                .name(product.getName())
                .parentId(product.getParent() != null ? product.getParent().getId() : null)
                .type(product.getType())
                .price(product.getPrice())
                .date(product.getDate().atOffset(ZoneOffset.UTC));
    }

    public static ShopUnitStatisticResponse getShopUnitStatisticResponse(List<ProductsUpdateHistory> productsUpdateHistoryList) {
        ShopUnitStatisticResponse response = new ShopUnitStatisticResponse();
        productsUpdateHistoryList.forEach(s -> {
            response.addItemsItem(entityToShopUnitStatisticUnit(s));
        });
        return response;
    }

    public static ShopUnitStatisticUnit entityToShopUnitStatisticUnit(ProductsUpdateHistory productsUpdateHistory) {
        return new ShopUnitStatisticUnit().id(productsUpdateHistory.getUuid())
                .name(productsUpdateHistory.getName())
                .parentId(productsUpdateHistory.getParentId())
                .type(productsUpdateHistory.getType())
                .price(productsUpdateHistory.getPrice())
                .date(productsUpdateHistory.getDate().atOffset(ZoneOffset.UTC));
    }
}

