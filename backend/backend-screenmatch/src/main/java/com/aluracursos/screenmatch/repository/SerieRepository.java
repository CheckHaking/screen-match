package com.aluracursos.screenmatch.repository;

import com.aluracursos.screenmatch.dto.EpisodioDTO;
import com.aluracursos.screenmatch.model.Categoria;
import com.aluracursos.screenmatch.model.Episodio;
import com.aluracursos.screenmatch.model.Serie;
import jakarta.persistence.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SerieRepository extends JpaRepository<Serie,Long> {

     //Declaraciones de metodos para QUERY en nuestra base de datos
     Optional<Serie> findByTituloContainsIgnoreCase(String nombreSerie);

     List <Serie> findTop5ByOrderByEvaluacionDesc();

     //Busqueda por categoria, como en nuestra clase serie la categoria esta mapeada como "genero" no podemos usar
     List <Serie> findSerieByGenero(Categoria categoria);

     //Busqueda por numero maximo de temporadas y un numero minimo de temporadas
     //List <Serie> findByTotalTemporadasLessThanEqualAndEvaluacionGreaterThanEqual(Integer temporadas, Integer eval);

     //@Query(value = "SELECT s FROM Serie s WHERE s.totalTemporadas <= :totalTemporadas AND s.evaluacion >= :evaluacion")

     //JPQL lenguaje de queries nativos de Java
     String jpql = "SELECT s FROM Serie s WHERE s.totalTemporadas <= :totalTemporadas AND s.evaluacion >= :evaluacion";
     @Query(jpql)
     List <Serie> seriesPorTemporadaYEvaluaciion(int totalTemporadas, Double evaluacion);

     String jpqlEpisodiosPorNombre = "SELECT e FROM Serie s JOIN s.episodios e WHERE e.titulo ILIKE %:nombreEpisodio%";

     @Query(jpqlEpisodiosPorNombre)
     List <Episodio> episodiosPorNombre(String nombreEpisodio);

     //METODO TOP 5 MEJORES EPISODIOS

     String buesquedaTop5Episodios = "SELECT e FROM Serie s JOIN s.episodios e WHERE s = :serie ORDER BY e.evaluacion DESC LIMIT 5 ";
     @Query(buesquedaTop5Episodios)
     List <Episodio> top5Episodios (Serie serie);




     String busquedaDeLanzamientosMasRecientes = "SELECT s FROM Serie s " + "JOIN s.episodios e " + "GROUP BY s " + "ORDER BY MAX(e.fechaDeLanzamiento) DESC LIMIT 5";
     @Query(busquedaDeLanzamientosMasRecientes)
     List <Serie> lanzamientosMasRecientes();

     String obtenerTemporadasPorNumero = "SELECT e FROM Serie s JOIN s.episodios e WHERE s.id = :id AND e.temporada = :numeroTemporada";
     @Query(obtenerTemporadasPorNumero)
     List<Episodio> obtenerTemporadasPorNumero(Long id, Long numeroTemporada);
}
