package com.aluracursos.screenmatch.principal;

import com.aluracursos.screenmatch.model.*;
import com.aluracursos.screenmatch.repository.SerieRepository;
import com.aluracursos.screenmatch.service.ConsumoAPI;
import com.aluracursos.screenmatch.service.ConvierteDatos;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoApi = new ConsumoAPI();
    private final String URL_BASE = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=eea6e0a2";
    private ConvierteDatos conversor = new ConvierteDatos();
    private List<DatosSerie> datosSeries = new ArrayList<>();
    private SerieRepository repositorio;
    private List<Serie> series;
    private Optional <Serie> serieBuscada;

    public Principal(SerieRepository repository) {
        this.repositorio = repository;
    }

    public void muestraElMenu() {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    \n
                    1 - Buscar series 
                    2 - Buscar episodios
                    3 - Mostrar series buscadas
                    4.- Buscar series por titulo
                    5.- Top 5 mejores series
                    6.- Buscar serie por categoria
                    7.- Buscar serie por numero maximo de Temporadas y numero minimo de Evaluacion
                    8.- Buscar episodios por titulo 
                    9.- Top 5 episodios por Serie 

                                  
                    0 - Salir
                    """;
            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    mostrarSeriesBuscadas();
                    break;
                case 4:
                    buscarSeriesPorTitulo();
                    break;
                case 5:
                    buscarTop5series();
                    break;
                case 6:
                    buscarSeriesPorCategoria();
                    break;
                case 7:
                    buscarSeriesPorNumeroTemporada();
                    break;
                case 8:
                    buscarEpisodioPorTitulo();
                    break;
                case 9:
                    top5EpisodiosPorSerie();
                    break;
                case 0:
                    System.out.println("Cerrando la aplicación...");
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }

    }

    private DatosSerie getDatosSerie() {
        System.out.println("Escribe el nombre de la serie que deseas buscar");
        var nombreSerie = teclado.nextLine();
        var json = consumoApi.obtenerDatos(URL_BASE + nombreSerie.replace(" ", "+") + API_KEY);
        System.out.println(json);
        DatosSerie datos = conversor.obtenerDatos(json, DatosSerie.class);
        return datos;
    }
    private void buscarEpisodioPorSerie() {
        mostrarSeriesBuscadas();
        System.out.println("Escribe el nombre de la seria de la cual quieres ver los episodios");
        var nombreSerie = teclado.nextLine();

        Optional<Serie> serie = series.stream()
                .filter(s -> s.getTitulo().toLowerCase().contains(nombreSerie.toLowerCase()))
                .findFirst();

        if(serie.isPresent()){
            var serieEncontrada = serie.get();
            List<DatosTemporadas> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumoApi.obtenerDatos(URL_BASE + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DatosTemporadas datosTemporada = conversor.obtenerDatos(json, DatosTemporadas.class);
                temporadas.add(datosTemporada);
            }
            temporadas.forEach(System.out::println);

            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d -> d.episodios().stream()
                            .map(e -> new Episodio(d.numero(), e)))
                    .collect(Collectors.toList());

            serieEncontrada.setEpisodios(episodios);
            repositorio.save(serieEncontrada);
        }



    }
    private void buscarSerieWeb() {
        DatosSerie datos = getDatosSerie();
        Serie serie = new Serie(datos);
        repositorio.save(serie);
        //datosSeries.add(datos);
        System.out.println(datos);
    }

    private void mostrarSeriesBuscadas() {
        series = repositorio.findAll();

        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }

    private void buscarSeriesPorTitulo(){
        System.out.println("Escribe el nombre de la serie que deseas buscar: ");
        var nombreSerie = teclado.nextLine();
        //Creamos una lista para almacenar estas series
        serieBuscada = repositorio.findByTituloContainsIgnoreCase(nombreSerie);

        if(serieBuscada.isPresent()){
            System.out.println("La serie buscada es: " + serieBuscada.get());
        } else {

            System.out.println("Serie no encontrada...");
        }

    }

    private void buscarTop5series(){

        List <Serie> top5series = repositorio.findTop5ByOrderByEvaluacionDesc();
        top5series.forEach( s -> System.out.println("\nSerie: " + s.getTitulo() + "\nEvaluacion: " + s.getEvaluacion() ));

    }

    private void buscarSeriesPorCategoria(){

        System.out.println("Escribe el nombre de la categoria de la serie que deseas buscar: ");
        var genero = teclado.nextLine();
        var categoria = Categoria.fromEspanol(genero);
        List <Serie> seriesPorCategoria = repositorio.findSerieByGenero(categoria);

        if (seriesPorCategoria.size() > 0){
            System.out.println("\nSeries encontradas del genero " + categoria + ": ");
            seriesPorCategoria.forEach(s -> System.out.println(s.getTitulo()));
        }else{
            System.out.println("No se encontraron series ...");
        }
    }

    private void buscarSeriesPorNumeroTemporada(){

        System.out.println("Escribe el numero maximo de temporadas de la serie: ");
        var temp = teclado.nextInt();
        System.out.println("Escribe el numero minimo de evaluacion de la serie: ");
        var eval = teclado.nextDouble();
        List <Serie> filtroSeries = repositorio.seriesPorTemporadaYEvaluaciion(temp, eval);
        System.out.println("Series encontradas con el rango de temporadas: \n");
        if (filtroSeries.size() > 0){
            System.out.println("\nSeries encontradas por temporada y evaluacion: \n ");
            filtroSeries.forEach(s -> System.out.println(s.getTitulo()));
        }else{
            System.out.println("No se encontraron series ...");
        }

    }

    private void buscarEpisodioPorTitulo(){
        System.out.println("Escribe el titulo del episodio que deseas buscar: ");
        var titulo = teclado.nextLine();
        List <Episodio> episodiosEncontrados = repositorio.episodiosPorNombre(titulo);
        episodiosEncontrados.forEach( e ->
                System.out.printf("\n Serie: %s\n Temporada: %s \n Episodio: #%s %s \n Evaluacion: %s",
                        e.getSerie().getTitulo(),
                        e.getTemporada(),
                        e.getNumeroEpisodio() ,
                        e.getTitulo(),
                        e.getEvaluacion()));
    }

    private void top5EpisodiosPorSerie(){
        buscarSeriesPorTitulo();
        if (serieBuscada.isPresent()){
            Serie serie = serieBuscada.get();
            List <Episodio> topEpisodios = repositorio.top5Episodios(serie);
            topEpisodios.forEach( e ->
                    System.out.printf("\n Serie: %s\n Temporada: %s \n Episodio: #%s %s \n Evaluacion: %s",
                            e.getSerie().getTitulo(),
                            e.getTemporada(),
                            e.getNumeroEpisodio(),
                            e.getTitulo(),
                            e.getEvaluacion()));
        }
    }
}

