package ru.yandex.yashop.dao.service;

import org.springframework.stereotype.Repository;
import ru.yandex.yashop.dao.entity.Products;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ProductDaoService {
    void deleteIdDelete(UUID uuid);

    Products nodesIdGet(UUID uuid);

    List<Products> salesGet(@NotNull @Valid OffsetDateTime offsetDateTime);

    boolean isNotRemoteById(UUID uuid);

    boolean isRemoteById(UUID uuid);

    List<Products> saveAll(@Valid List<Products> productsList);


}
