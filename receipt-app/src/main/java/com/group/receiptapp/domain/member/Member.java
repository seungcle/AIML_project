package com.group.receiptapp.domain.member;

import com.group.receiptapp.domain.group.Group;
import com.group.receiptapp.domain.receipt.Receipt;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "member")
@Getter @Setter
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="member_id")
    private Long id;

    @NotEmpty(message = "Name cannot be empty")
    @Size(min = 2, max = 255, message = "Name must be between 2 and 255 characters")
    @Column(nullable = false, length = 255)
    private String name;

    @Email(message = "Email should be valid")
    @NotEmpty(message = "Email cannot be empty")
    @Column(nullable = false, length = 255, unique = true)
    private String email;

    @NotEmpty
    @Size(min = 6)
    @Column(nullable = false, length = 255)
    private String password;

    @Column(name = "is_admin", nullable = false)
    private boolean isAdmin;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @NotNull(message = "그룹을 선택해 주세요.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Receipt> receipt = new ArrayList<>();

    public void deactivate() {
        this.isActive = false;
    }

    // boolean 필드에 대한 getter 메서드
    public boolean isAdmin() {
        return isAdmin;
    }

    // boolean 필드에 대한 setter 메서드
    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }
}
