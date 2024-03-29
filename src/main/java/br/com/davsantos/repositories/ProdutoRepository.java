package br.com.davsantos.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.davsantos.entities.Categoria;
import br.com.davsantos.entities.Produto;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Integer>{

	@Query("SELECT DISTINCT p FROM Produto p INNER JOIN p.categorias c WHERE p.nome LIKE %:nome% AND c IN :categorias")
	 Page<Produto> search(@Param("nome") String nome, @Param("categorias") List<Categoria> categorias, Pageable request);
}
