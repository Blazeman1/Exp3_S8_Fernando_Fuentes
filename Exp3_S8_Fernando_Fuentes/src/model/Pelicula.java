package model;

/**
 * Entidad que representa una Película en el sistema
 * 
 * Responsabilidades:
 * - Modelar los datos de una película
 * - Proporcionar acceso controlado a los atributos
 * - Validar la consistencia interna de los datos
 * 
 * Características:
 * - Java Bean con getters y setters
 * - Múltiples constructores para diferentes escenarios
 * - Atributos validados contra la base de datos
 * 
 */

public class Pelicula {
    //Atributos de la entidad
    private Integer id;         // ID autoincremental de la base de datos
    private String titulo;      // Título de la película
    private String director;    // Director de la película
    private int anio;           // Año de estreno
    private int duracion;       // Duración en minutos
    private Genero genero;      // Género cinematográfico
    
    /**
     * Constructor completo con todos los atributos
     * 
     * @param id ID de la película (puede ser null para nuevas películas)
     * @param titulo Título de la película
     * @param director Director de la película
     * @param anio  Año de estreno
     * @param duracion  Duración en minutos
     * @param genero Género cinematográfico
     */
    public Pelicula(Integer id, String titulo, String director, int anio, int duracion, Genero genero){
        this.id = id;
        this.titulo = titulo;
        this.director = director;
        this.anio = anio;
        this.duracion = duracion;
        this.genero = genero;
    }
    
    /**
     * Constructor para nueva películas (sin ID)
     * 
     * @param titulo Título de la película
     * @param director Director de la película
     * @param anio Año de estreno
     * @param duracion Duración en minutos
     * @param genero Género cinematográfico
     */
    
    public Pelicula(String titulo, String director, int anio, int duracion, Genero genero) {
        this(null, titulo, director, anio, duracion, genero);
    }

    /**
     * Getters
     * 
     * @return Integer ID de la película
     */
    public Integer getId() { return id; }
    
    /**
     * @return String Título de la película
     */
    public String getTitulo() { return titulo; }
    
    /**
     * @return String Director de la película
     */
    public String getDirector() { return director; }

    /**
     * @return int Año de estreno
     */
    public int getAnio() { return anio; }
    
    /**
     * @return int Duración en minutos
     */
    public int getDuracion() { return duracion; }

    /**
     * @return Genero Género cinematográfico
     */
    public Genero getGenero() { return genero; }

        
    /**
     * Setters
     * 
     * @param id ID de la película
     */
    public void setId(Integer id) { this.id = id; }

    /**
     * @param titulo Título de la película
     */
    public void setTitulo(String titulo) { this.titulo = titulo; }

    /**
     * @param director Director de la película
     */
    public void setDirector(String director) { this.director = director; }

    /**
     * @param anio Año de estreno
     */
    public void setAnio(int anio) { this.anio = anio; }

    /**
     * @param duracion Duración en minutos
     */
    public void setDuracion(int duracion) { this.duracion = duracion; }

    /**
     * @param genero Género cinematográfico
     */
    public void setGenero(Genero genero) { this.genero = genero; }
}
