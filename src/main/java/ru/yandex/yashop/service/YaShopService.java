package ru.yandex.yashop.service;

import ru.yandex.temp.model.ShopUnit;
import ru.yandex.temp.model.ShopUnitImportRequest;
import ru.yandex.temp.model.ShopUnitStatisticResponse;

import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;

import java.util.UUID;

public interface YaShopService {
    void deleteIdDelete(UUID uuid);

    void importsPost(@Valid ShopUnitImportRequest shopUnitImportRequest) throws ValidationException;

    ShopUnitStatisticResponse nodeIdStatisticGet
            (UUID uuid, @Valid OffsetDateTime offsetDateTime, @Valid OffsetDateTime offsetDateTime1);

    ShopUnit nodesIdGet(UUID uuid);

    ShopUnitStatisticResponse salesGet(@NotNull @Valid OffsetDateTime offsetDateTime);
}
