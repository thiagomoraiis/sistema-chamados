package com.ifrn.chamados.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "setores")
@Getter
@Setter
@NoArgsConstructor
public class Setor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String sigla;

    @OneToMany(mappedBy = "setor")
    private List<Categoria> categorias = new ArrayList<>();

    @OneToMany(mappedBy = "setor")
    private List<Usuario> tecnicos = new ArrayList<>();
}
