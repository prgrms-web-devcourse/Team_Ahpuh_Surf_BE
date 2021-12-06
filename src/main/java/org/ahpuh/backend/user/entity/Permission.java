package org.ahpuh.backend.user.entity;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "permission")
@Getter
public class Permission {

    @Id
    @Column(name = "permission_id")
    private Long permissionId;

    @Column(name = "permission_name")
    private String permissionName;

    public List<GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(permissionName));
    }

}
