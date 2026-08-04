package org.example.app.entity;

import java.time.OffsetDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("user")
public class User {
    @Id
    private Long id;
    private String username;
    private String password;
    @Column("create_at")
    private OffsetDateTime createAt;
    @Column("update_at")
    private OffsetDateTime updateAt; //或ZonedDateTime ，无时区可以使用LocalDateTime
    private Boolean enabled;
    public User(Long id, String username, String password,OffsetDateTime createAt, OffsetDateTime updateAt,Boolean enabled){
        this.id = id;
        this.username = username;
        this.createAt = createAt;
        this.updateAt = updateAt;
        this.enabled = enabled;
    }
    public Long getId(){
        return this.id;
    }
    public String getUsername(){
        return this.username;
    }
    public String getPassword(){
        return "************";
    }
    public OffsetDateTime getCreateAt(){
        return this.createAt;
    }
    public OffsetDateTime getUpdateAt(){
        return this.updateAt;
    }
    public Boolean getEnabled(){
        return this.enabled;
    }
    public void setId(Long id){
        this.id = id;
    }
    public void setUsername(String username){
        this.username = username;
    }
    public void setPassword(String password){
        this.password = password;
    }
    public void setCreateAt(OffsetDateTime createAt){
        this.createAt = createAt;
    }
    public void setUpdateAt(OffsetDateTime updateAt){
        this.updateAt = updateAt;
    }
    public void setEnabled(Boolean enabled){
        this.enabled = enabled;
    }
}
