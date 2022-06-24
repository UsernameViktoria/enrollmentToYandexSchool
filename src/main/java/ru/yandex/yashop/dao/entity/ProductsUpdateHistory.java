package ru.yandex.yashop.dao.entity;

import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;
import ru.yandex.temp.model.ShopUnitType;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity
@Table(name = "products_update_history")
@EntityListeners(AuditingEntityListener.class)
public class ProductsUpdateHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid", nullable = false)
    private UUID uuid;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "date", nullable = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime date;

    @Column(name = "parentId")
    private UUID parentId;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ShopUnitType type;

    @Column(name = "price")
    private Long price;

    @Column(name = "remote")
    private boolean remote;

}


