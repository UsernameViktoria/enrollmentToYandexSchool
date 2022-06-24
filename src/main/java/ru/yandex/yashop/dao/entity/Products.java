package ru.yandex.yashop.dao.entity;

import lombok.*;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;
import ru.yandex.temp.model.ShopUnitType;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
@Table(name = "products")
@EntityListeners(AuditingEntityListener.class)
public class Products {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    @NotNull
    @NotBlank
    private String name;

    @Column(name = "date", nullable = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @CreatedDate
    private LocalDateTime date;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ShopUnitType type;

    @Column(name = "price")
    private Long price;

    @Column(name = "remote")
    private boolean remote;

    @OneToMany(mappedBy = "parent")
    @Where(clause = "remote = false")
    @ToString.Exclude
    private List<Products> children;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "parent_id")
    @ToString.Exclude
    private Products parent;
}


