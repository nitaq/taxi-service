package com.internship.amazingtaxiservice.taxiservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import static java.util.stream.Collectors.toList;


@Entity
@Table(name = "users")
@Data
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "enabled")
    private boolean enabled;

    @Column(name = "first_name")
    @NotEmpty(message = "FirstName should not be blank")
    @Size(min = 2, max = 30)
    private String firstName;

    @Column(name = "last_name")
    @NotEmpty(message = "LastName should not be blank")
    @Size(min = 2, max = 30)
    private String lastName;

    @Column(name = "email")
    @Email(message = "Email should be valid")
    private String username;

    @Column(name = "password")
    @NotEmpty(message = "Password should not be blank")
    private String password;

    @Column(name = "phone")
    @NotEmpty(message = "Phone should not be blank")
    private String phone;

    @Column(name = "expiry_date")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date expiryDate;

    @Column(name = "reset_expiry_date")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date resetExpiryDate;

    @Column(name = "token")
    private String token;

    @Column(name = "resetToken")
    private String resetToken;



    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "role_id")
    private Role role;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<String> strings = new ArrayList<>();
        strings.add(role.getTitle());
        return strings.stream().map(SimpleGrantedAuthority::new).collect(toList());
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setRoleId(int role_id) {
        this.role.setId(role_id);
    }
}