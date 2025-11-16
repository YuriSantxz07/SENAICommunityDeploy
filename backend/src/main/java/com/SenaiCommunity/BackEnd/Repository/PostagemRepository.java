package com.SenaiCommunity.BackEnd.Repository;

import com.SenaiCommunity.BackEnd.Entity.Postagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostagemRepository extends JpaRepository<Postagem, Long> {
    @Query("SELECT p FROM Postagem p " +
       "LEFT JOIN FETCH p.arquivos " +
       "LEFT JOIN FETCH p.curtidas " +
       "LEFT JOIN FETCH p.comentarios c " +
       "LEFT JOIN FETCH c.curtidas " +
       "ORDER BY p.dataPostagem DESC LIMIT 50")
List<Postagem> findTop50ComRelacionamentos();
}