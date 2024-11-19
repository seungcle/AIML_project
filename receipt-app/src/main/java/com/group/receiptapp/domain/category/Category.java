package com.group.receiptapp.domain.category;

import com.group.receiptapp.domain.receipt.Receipt;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name= "category")
@Getter
@Setter
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id; // 카테고리 고유 ID

    @Column(name = "category_name", nullable = false, unique = true)
    private String name; // 카테고리 이름

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Receipt> receipts; // 이 카테고리에 속한 영수증들

    public Category() {}

    public Category(String name) {
        this.name = name;
    }

}
