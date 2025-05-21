package com.group.receiptapp.domain.group;

import com.group.receiptapp.domain.member.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "member_group")
@Getter
@Setter
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private Long id; // 그룹 고유 ID

    @Column(name = "group_name", nullable = false, unique = true)
    private String name; // 그룹 이름

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Member> members = new ArrayList<>(); // 그룹에 속한 사용자들

    @Column(name = "prevent_duplicate_receipt", nullable = false)
    private boolean preventDuplicateReceipt = false;

    public Group() {}

    public Group(Long id) {
        this.id = id;
    }

    public Group(String name) {
        this.name = name;
    }


}
