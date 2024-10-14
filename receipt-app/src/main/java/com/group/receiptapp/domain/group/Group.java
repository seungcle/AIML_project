package com.group.receiptapp.domain.group;

import com.group.receiptapp.domain.member.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public Group() {}

    public Group(String name) {
        this.name = name;
    }
}
