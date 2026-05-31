package com.fhk.security.models.account.domain;

import com.fhk.core.entity.BaseEntity;
import com.fhk.security.core.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="account_tbl")
@Getter @Setter
@ToString
public class Account extends BaseEntity {

    @Id
    @Column(name="account_id")
    @SequenceGenerator(
            name = "account_seq",
            sequenceName = "account_seq_tbl",
            allocationSize = 1 // sequence 캐싱 처리, 배포시 50정도 -> 병목시 늘리기
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_seq")
    private Long id;

    @Column(name = "login_id",
            unique = true)
    private String loginId;

    @Column(name = "login_pw")
    private String password;

    @Column(name = "account_nickname",
            unique = true)
    private String nickname;

    @Column(name = "account_role")
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "account_deleted",
            nullable = false)
    private String deletedYn;

    @Column(name = "token_version", nullable = false)
    private Long tokenVersion = 0L;

    @PrePersist
    public void prePersist() {
        if (this.deletedYn == null)
            this.deletedYn = "N";

        if (this.role == null)
            this.role = Role.USER;
    }
}
