package org.example.app.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name="acelist")
public class Acelist {
    @Id @GeneratedValue
    @SequenceGenerator
    private Long id;
    private Long uid;
    private Integer aid;
    Acelist(Long id,Long uid,Integer aid){
        this.id = id;
        this.uid = uid;
        this.aid = aid;
    }    
    public Long getId(){
        return this.id;
    }
    public Long getUid(){
        return this.uid;
    }
    public Integer getAid(){
        return this.aid;
    }
    public void setId(Long id){
        this.id = id;
    }
    public void setUid(Long uid){
        this.uid = uid;
    }
    public void setAid(Integer aid){
        this.aid = aid;
    }
}
