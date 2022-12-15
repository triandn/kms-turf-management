package com.kms.cntt.model;

import com.kms.cntt.enums.Role;
import com.kms.cntt.enums.TurfType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "turfs")
public class Turf extends AbstractEntity{
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private TurfType type;

    @Column(nullable = false)
    private BigDecimal hourlyFee;

    private float rating;

    @Column(nullable = false)
    private String imageLink;

    @ManyToOne
    @JoinColumn(name = "location_turf_id")
    private LocationTurf locationTurf;

}
