package org.example.app.entity;

import org.hibernate.annotations.Audited.Table;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;

@Entity
@Table(name="authorities")
public class Authorities {
    @Id @GeneratedValue
    @SequenceGenerator
    private Long id;
    private String authority;
    Authorities(Long id,String authority){
        this.id = id;
        this.authority = authority;
    }
    public Long getId(){
        return this.id;
    }
    public String getAuthorities(){
        return this.authority;
    }
    public void setId(Long id){
        this.id = id;
    }
    public void setAuthorities(String authority){
        this.authority = authority;
    }
}