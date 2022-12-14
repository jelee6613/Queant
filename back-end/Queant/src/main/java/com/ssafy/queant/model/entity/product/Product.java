package com.ssafy.queant.model.entity.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int productId;

    private String productCode;

    @Column(nullable = false)
    private int bankId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String scodeId;

    @Column(nullable = false)
    private boolean isDeposit;

    private Integer ageMin;
    private Integer ageMax;
    private Long budgetMin;
    private Long budgetMax;
    private Integer termMin;
    private Integer termMax;
    private String etc;

    @Column(nullable = false)
    private boolean isEnabled;
    private String picture;
}
