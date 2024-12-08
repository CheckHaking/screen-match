package com.aluracursos.screenmatch.model;

public enum Categoria {

    //vamos a agregar una nueva clausula para que reconozca el nombre en espaniol
    ACCION("Action", "Acción"),
    ROMANCE("Romance", "Romance"),
    COMEDIA("Comedy", "Comedia"),
    DRAMA("Drama", "Drama"),
    CRIMEN("Crime", "Crimen");

    private String categoriaOmdb;

    private String categoriaEspanol;



    //CONSTRUCTOR DE MI CLASE CATEGORIA
    Categoria (String categoriaOmdb, String categoriaEspanol){

        this.categoriaOmdb = categoriaOmdb;
        this.categoriaEspanol = categoriaEspanol;

    }

    public static Categoria fromString(String text) {
        for (Categoria categoria : Categoria.values()) {
            if (categoria.categoriaOmdb.equalsIgnoreCase(text)) {
                return categoria;
            }
        }
        throw new IllegalArgumentException("Ninguna categoria encontrada: " + text);
    }

    //Este metodo verifica si el texto en el metodo es igual a algun elemento de nuestra categoria
    public static Categoria fromEspanol(String text) {
        for (Categoria categoria : Categoria.values()) {
            if (categoria.categoriaEspanol.equalsIgnoreCase(text)) {
                return categoria;
            }
        }
        throw new IllegalArgumentException("Ninguna categoria encontrada: " + text);
    }

}
