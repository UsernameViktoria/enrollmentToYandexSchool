package ru.yandex.yashop.dao.service;

import org.springframework.stereotype.Repository;
import ru.yandex.yashop.dao.entity.ProductsUpdateHistory;

import javax.validation.Valid;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface HistoryDaoService {

    void saveAll(@Valid List<ProductsUpdateHistory> productsUpdateHistoryList);

    List<ProductsUpdateHistory> nodeIdStatisticGet(UUID uuid, @Valid OffsetDateTime dataS, @Valid OffsetDateTime dataPo);

    boolean isNotRemoteById(UUID uuid);
}
