package ru.yandex.yashop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.temp.api.DefaultApi;
import ru.yandex.temp.model.ShopUnit;
import ru.yandex.temp.model.ShopUnitImportRequest;
import ru.yandex.temp.model.ShopUnitStatisticResponse;
import ru.yandex.yashop.service.YaShopService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class Controller implements DefaultApi {

    @Autowired
    private YaShopService yaShopService;

    //-----------------Основные задачи------------------------------------------

    /**
     * Метод, который добавляет или обновляет продукт/категорию*
     * В таблицу product_history всегда происходит добавление
     * В таблицу products происходит добавление или обновление
     *
     * @param shopUnitImportRequest продукт/категория которую необходимо добавить*
     */
    @Override
    @PostMapping("/imports")
    public ResponseEntity<String> importsPost(@Valid @RequestBody(required = false) ShopUnitImportRequest shopUnitImportRequest) {
        yaShopService.importsPost(shopUnitImportRequest);
        return ResponseEntity.ok("Вставка или обновление прошли успешно.");
    }

    /**
     * Метод, который удаляет элемент из базы данных по его uuid
     *
     * @param id
     */
    @Override
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteIdDelete(@PathVariable @Valid UUID id) {
        yaShopService.deleteIdDelete(id);
        return ResponseEntity.ok("Удаление прошло успешно.");
    }

    /**
     * Метод, который возвращает информацию по товару/категории по его uuid
     *
     * @param id
     * @return ShopUnit
     */
    @Override
    @GetMapping("/nodes/{id}")
    public ResponseEntity<ShopUnit> nodesIdGet(@PathVariable @Valid UUID id) {

        return ResponseEntity.ok(yaShopService.nodesIdGet(id));
    }

    //-----------------Дополнительные задачи------------------------------------------

    /**
     * Метод, который показывает историю изменений по отпределенному товару/категории.
     * Временной период можно задать двумя датами, одной начальной, одной конечной либо не задать,
     * в таком случае будет выведена история изменений за все время
     *
     * @param uuid
     * @param offsetDateTime
     * @param offsetDateTime1
     * @return ResponseEntity<ShopUnitStatisticResponse>
     */
    @Override
    @GetMapping("/node/{id}/statistic")
    public ResponseEntity<ShopUnitStatisticResponse> nodeIdStatisticGet(@Valid UUID uuid,
                                                                        @Valid OffsetDateTime offsetDateTime, @Valid OffsetDateTime offsetDateTime1) {
        return ResponseEntity.ok(yaShopService.nodeIdStatisticGet(uuid, offsetDateTime, offsetDateTime1));
    }

    /**
     * Метод, который выводит  список товаров/категорий по которым происходили изменения/обновления за последние сутки
     *
     * @param offsetDateTime
     * @return ResponseEntity<ShopUnitStatisticResponse>
     */
    @Override
    @GetMapping("/sales")
    public ResponseEntity<ShopUnitStatisticResponse> salesGet(@NotNull @Valid OffsetDateTime offsetDateTime) {
        return ResponseEntity.ok(yaShopService.salesGet(offsetDateTime));
    }
}
