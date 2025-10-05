package dao;

import java.util.List;
import model.Pelicula;
import java.sql.SQLException;

/**
 * Interfaz para el Data Access Object (DAO) de Películas
 * 
 * Responsabilidades:
 * - Definir el contrato para las operaciones CRUD de películas
 * - Abstraction de la persistencia de datos
 * - Permitir diferentes implementaciones de persistencia
 * 
 * Patrón: Data Access Object (DAO)
 * 
 */

public interface IPeliculaDAO {
    
    /**
     * Crea una nueva película en la base de datos
     * 
     * @param p Objeto Pelicula a persistir
     * @return int ID generado por la base de datos
     * @throws SQLException Si ocurre error en la operación de base de datos
     */

    int create(Pelicula p) throws SQLException;
    
    // Funcionalidades a extender
    Pelicula findById(int id) throws SQLException;
    List<Pelicula> findAll() throws SQLException;
    List<Pelicula> findByTitleLike(String query) throws SQLException;
    void update(Pelicula p) throws SQLException;
    void delete(int id) throws SQLException;
    
    // Métodos para filtros
    List<Pelicula> findByGenero(String genero) throws SQLException;
    List<Pelicula> findByRangoAnios(int anioDesde, int anioHasta) throws SQLException;
    List<Pelicula> findByGeneroAndRangoAnios(String genero, int anioDesde, int anioHasta) throws SQLException;
}
