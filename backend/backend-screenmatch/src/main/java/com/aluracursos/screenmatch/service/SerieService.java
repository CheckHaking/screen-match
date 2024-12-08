package com.aluracursos.screenmatch.service;


import com.aluracursos.screenmatch.dto.EpisodioDTO;
import com.aluracursos.screenmatch.dto.SerieDTO;
import com.aluracursos.screenmatch.model.Categoria;
import com.aluracursos.screenmatch.model.Episodio;
import com.aluracursos.screenmatch.model.Serie;
import com.aluracursos.screenmatch.repository.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SerieService {

    @Autowired
    private SerieRepository repository;

    @GetMapping("/series")
    public List<SerieDTO> obtenerTodasLasSeries(){
        return convierteDato(repository.findAll());
    }

    @GetMapping("/series/top5")
    public List<SerieDTO> obtenerTop5(){
        return convierteDato(repository.findTop5ByOrderByEvaluacionDesc());

    }

    @GetMapping("/lanzamientos")
    public List<SerieDTO> obtenerLanzamientosMasRecientes(){
        return convierteDato(repository.lanzamientosMasRecientes());
    }


    @GetMapping("/{id}")
    public SerieDTO obtenerPorId( Long id){
        Optional<Serie> serie = repository.findById(id);
        if(serie.isPresent()){
            Serie s = serie.get();
            return new SerieDTO(s.getId(),s.getTitulo(),s.getTotalTemporadas(),s.getEvaluacion(),
                    s.getPoster(), s.getGenero(), s.getActores(), s.getSinopsis());
        }
        return null;
    }


    public List<EpisodioDTO> obtenerTodasLasTemporadas(Long id){
        Optional<Serie> serie = repository.findById(id);
        if(serie.isPresent()){
            Serie s = serie.get();
            return s.getEpisodios().stream().map(e-> new EpisodioDTO(e.getTemporada(),
                    e.getTitulo(),
                    e.getNumeroEpisodio())).collect(Collectors.toList());
        }
        return null;
    }

    public List<EpisodioDTO> obtenerTemporadasPorNumero( Long id,Long numeroTemporada){
        return repository.obtenerTemporadasPorNumero(id, numeroTemporada).stream().map(e->new EpisodioDTO(e.getTemporada(),
                e.getTitulo(),
                e.getNumeroEpisodio())).collect(Collectors.toList());
    }



    public List<SerieDTO> convierteDato(List <Serie> serie){
        return serie.stream()
                .map(s-> new SerieDTO(s.getId(), s.getTitulo(),s.getTotalTemporadas(),s.getEvaluacion(),
                        s.getPoster(), s.getGenero(), s.getActores(), s.getSinopsis()))
                .collect(Collectors.toList());
    }


    public List<SerieDTO> obtenerSeriesPorCategoria(String nombreGenero) {
        Categoria categoria = Categoria.fromEspanol(nombreGenero);
        return convierteDato(repository.findSerieByGenero(categoria));
    }

    public List<EpisodioDTO> obtenerTop5EpisodiosPorSerie(Long id) {
        Optional<Serie> serie = repository.findById(id);
        if(serie.isPresent()){
            Serie s = serie.get();
            return repository.top5Episodios(s).stream().map(e -> new EpisodioDTO(e.getTemporada(),
                    e.getTitulo(),
                    e.getNumeroEpisodio())).collect(Collectors.toList());
        }
        return null;

    }


}
