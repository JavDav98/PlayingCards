package com.umg.regesc.core.entities;

import javax.persistence.*;

@Entity
@Table(name = "student", schema = "regesc_simp2", indexes = {
        @Index(name = "fk_student_persona1_idx", columnList = "persona_cui")
})
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "carnetstudent", nullable = false)
    private Integer id;

    @Column(name = "usuario", nullable = false, length = 45)
    private String usuario;

    @Column(name = "password", nullable = false, length = 45)
    private String password;

    @Column(name = "persona_cui", nullable = false)
    private Long personaCui;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getPersonaCui() {
        return personaCui;
    }

    public void setPersonaCui(Long personaCui) {
        this.personaCui = personaCui;
    }

}