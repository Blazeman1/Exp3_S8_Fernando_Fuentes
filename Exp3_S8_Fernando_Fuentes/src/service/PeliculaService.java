package service;

import dao.PeliculaDAO;
import java.time.Year;
import java.util.List;
import model.Pelicula;

/**
 * Servicio de aplicación para la gestión de películas
 * 
 * Responsabilidades:
 * - Implementar la lógica de negocio
 * - Validar datos antes de persistir
 * - Coordinar operaciones con el DAO
 * - Manejar excepciones de negocio
 * 
 */

public class PeliculaService {

    private final PeliculaDAO dao; // DAO para operaciones de persistencia

    /**
     * Constructor que inyecta el DAO
     * 
     * @param dao Implementación de PeliculaDAO
     */
    public PeliculaService(PeliculaDAO dao) {
        this.dao = dao; 
    }

    /**
     * Crea una nueva película en la base de datos con validaciones de negocio
     * 
     * Validaciones implementadas:
     * -Título no nulo y no vacío
     * - Directo no nulo y no vacío
     * - Año dentro de rango válido (1900 - año actual +1)
     * - Duración dentro de rango válido (1-999 minutos)
     * - Prevención de duplicados (título + año)
     * 
     * @param p Película a crear
     * @return int ID generado por la base de datos
     * @throws Exception Si falla validación o persistencia
     */
    public int add(Pelicula p) throws Exception {
        // Validaciones de negocio (no en la vista)
        if (p.getTitulo() == null || p.getTitulo().isBlank()) {
            throw new IllegalArgumentException("El título es obligatorio.");
        }
        if (p.getTitulo().length() > 100){
            throw new IllegalArgumentException("El título no puede exceder 100 caracteres");
        }
        if (p.getDirector() == null || p.getDirector().isBlank()) {
            throw new IllegalArgumentException("El director es obligatorio.");
        }
        int currentMax = Year.now().getValue() + 1;
        if (p.getAnio() < 1900 || p.getAnio() > currentMax) {
            throw new IllegalArgumentException("El año debe estar entre 1900 y " + currentMax + ".");
        }
        if (p.getDuracion() < 1 || p.getDuracion() > 999) {
            throw new IllegalArgumentException("La duración debe estar entre 1 y 999.");
        }

        try {
            // Delegar la persistencia al DAO
            int id = dao.create(p);
            p.setId(id);
            return id;
        } catch (java.sql.SQLIntegrityConstraintViolationException dup) {
            // Manejar violación del constraint única (título + año)
            throw new IllegalArgumentException("Ya existe una película con el mismo TÍTULO y AÑO.");
        }
    }
    
    public Pelicula findById(int id) throws Exception{
        if(id <= 0){
            throw new IllegalArgumentException("ID inválido.");
        }
        
        Pelicula pelicula = dao.findById(id);
        if(pelicula == null){
            throw new IllegalArgumentException("No se encontró la pelicula con ID: " + id);
        }
        return pelicula;
    }
    
    public List<Pelicula> findAll() throws Exception{
        return dao.findAll();
    }
    
    public List<Pelicula> findByTitle(String query) throws Exception{
        if(query == null || query.trim().isEmpty()){
            throw new IllegalArgumentException("El término de búsqueda no puede estar vacío.");
        }
        return dao.findByTitleLike(query.trim());
    }
    
    public void update(Pelicula p) throws Exception{
        // Validaciones similares a add()
        if(p.getId() == null || p.getId() <= 0){
            throw new IllegalArgumentException("ID de película inválido.");
        }
        if(p.getTitulo() == null || p.getTitulo().isBlank()){
            throw new IllegalArgumentException("El titulo es obligatorio.");
        }
        if(p.getDirector() == null || p.getDirector().isBlank()){
            throw new IllegalArgumentException("El director es obligatorio.");
        }
        int currentMax = Year.now().getValue() + 1;
        if(p.getAnio() < 1900 || p.getAnio() > currentMax){
            throw new IllegalArgumentException("El año debe estar entre 1900 y " + currentMax + ".");
        }
        if(p.getDuracion() < 1 || p.getDuracion() > 999){
            throw new IllegalArgumentException("La duración debe estar entre 1 y 999.");
        }
        
        try {
            dao.update(p);
        } catch (java.sql.SQLIntegrityConstraintViolationException dup) {
            throw new IllegalArgumentException("Ya existe otra película con el mismo Título y Año.");
        }
    }
    
    public void delete(int id) throws Exception{
        if(id <= 0){
            throw new IllegalArgumentException("ID inválido.");
        }
        
        // Verificar que la película existe antes de eliminar
        Pelicula pelicula = dao.findById(id);
        if(pelicula == null){
            throw new IllegalArgumentException("No se encontró la película con ID: " + id);
        }
        
        dao.delete(id);
    }
    
    public List<Pelicula> findByGenero(String genero) throws Exception{
        if(genero == null || genero.trim().isEmpty()){
            throw new IllegalArgumentException("El género no puede estar vacío");
        }
        return dao.findByGenero(genero);
    }
    
    public List<Pelicula> findByRangoAnios(int anioDesde, int anioHasta) throws Exception{
        int currentMax = Year.now().getValue();
        if(anioDesde < 1900 || anioHasta > currentMax || anioDesde > anioHasta){
            throw new IllegalArgumentException("Rango de años inválido. Debe estar entre 1900 y " + currentMax);
        }
        return dao.findByRangoAnios(anioDesde, anioHasta);
    }
    
    public List<Pelicula> findByGeneroAndRangoAnios(String genero, int anioDesde, int anioHasta) throws Exception{
        if(genero == null || genero.trim().isEmpty()){
            throw new IllegalArgumentException("El género no puede estar vacío");
        }
        
        int currentMax = Year.now().getValue();
        if(anioDesde < 1900 || anioHasta > currentMax || anioDesde > anioHasta){
            throw new IllegalArgumentException("Rango de años inválido. Debe estar entre 1900 y " + currentMax);
        }
        
        return dao.findByGeneroAndRangoAnios(genero, anioDesde, anioHasta);
    }
    
    // Método General para filtrado flexible
    public List<Pelicula> findWithFilters(String genero, Integer anioDesde, Integer anioHasta) throws Exception{
        // Validar parámetros
        if(anioDesde != null && anioHasta != null && anioDesde > anioHasta){
            throw new IllegalArgumentException("El año 'desde' no puede ser mayor al año 'hasta'.");
        }
        
        // Casi 1: Sin filtros - devolver todo
        if((genero == null || genero.isEmpty()) && anioDesde == null && anioHasta == null){
            return dao.findAll();
        }
        
        // Caso 2: Solo filtro por género
        if(genero != null && !genero.isEmpty() && anioDesde == null && anioHasta == null){
            return dao.findByGenero(genero);
        }
        
        // Caso 3: Solo filtro por rango de años
        if((genero == null || genero.isEmpty()) && anioDesde != null && anioHasta != null){
            return dao.findByRangoAnios(anioDesde, anioHasta);
        }
        
        // Caso 4: Ambos filtros
        if(genero != null && !genero.isEmpty() && anioDesde != null && anioHasta != null){
            return dao.findByGeneroAndRangoAnios(genero, anioDesde, anioHasta);
        }
        
        // Caso por defecto. devolver todo
        return dao.findAll();
    }
}
