package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Pelicula;
import model.Genero;

/**
 * Implementación concreta del DAO para la entidad Película
 * 
 * Responsabilidades:
 * - Implementar las operaciones CRUD para películas
 * - Manejar la persistencia en base de datos MySQL
 * - Gestionar transacciones y recursos JDBC
 * - Recuperar IDs auto-generados
 * 
 * Implementa: IPeliculaDAO
 * Utiliza: DatabaseConnection para obtener conexiones
 * 
 */

public class PeliculaDAO implements IPeliculaDAO {
    
    /**
     * Crea una nueva película en la base de datos
     * 
     * Carácteristicas:
     * - Usa PreparedStatement para prevenir SQL injection
     * - Recupera el ID auto-generado
     * - Incluye fallback para compatibilidad con MariaDB
     * - Usa try-with-resources para manejo automático de recursos
     * 
     * @param p Película a persistir
     * @return int ID generado por la base de datos
     * @throws SQLException Si ocurre error en la operación de base de datos
     */

    @Override
    public int create(Pelicula p) throws SQLException {
    final String sql =
        "INSERT INTO Cartelera (titulo, director, anio, duracion, genero) VALUES (?,?,?,?,?)";

    try (Connection cn = DatabaseConnection.get();
         // Opción A: pedir las keys por nombre de columna (más fiable en MariaDB)
         PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

        // Establece parámetros del PreparedStatement
        ps.setString(1, p.getTitulo());
        ps.setString(2, p.getDirector());
        ps.setInt(3, p.getAnio());
        ps.setInt(4, p.getDuracion());
        ps.setString(5, p.getGenero().name());
        
        // Ejecuta la inserción
        ps.executeUpdate();

        // Intenta recuperar el ID auto-generado
        Integer newId = null;
        try (ResultSet keys = ps.getGeneratedKeys()) {
            if (keys.next()) newId = keys.getInt(1);
        }
        
        // Fallback compatible con MariaDB
        if (newId == null) { // Fallback compatible con MariaDB
            try (Statement st = cn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT LAST_INSERT_ID()")) {
                if (rs.next()) newId = rs.getInt(1);
            }
        }
        
        // Verifica que se obtuvo un ID válido
        if (newId == null) {
            throw new SQLException("No se pudo recuperar el ID generado.");
        }
        return newId;
        }
    }
    
    @Override
    public Pelicula findById(int id) throws SQLException{
        final String sql = "SELECT * FROM Cartelera WHERE id = ?";
        
        try(Connection cn = DatabaseConnection.get();
                PreparedStatement ps = cn.prepareStatement(sql)){
            
            ps.setInt(1, id);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    return resultSetToPelicula(rs);
                }
            }
        }
        return null;
    }
    
    @Override
    public List<Pelicula> findAll() throws SQLException{
        final String sql = "SELECT * FROM Cartelera ORDER BY titulo";
        List<Pelicula> peliculas = new ArrayList<>();
        
        try(Connection cn = DatabaseConnection.get();
                Statement st = cn.createStatement();
                ResultSet rs = st.executeQuery(sql)){
            
            while(rs.next()){
                peliculas.add(resultSetToPelicula(rs));
            }
        }
        return peliculas;
    }
    
    @Override
    public List<Pelicula> findByTitleLike(String query) throws SQLException{
        final String sql = "SELECT * FROM Cartelera WHERE titulo LIKE ? ORDER BY titulo";
        
        List<Pelicula> peliculas = new ArrayList<>();
        
        try(Connection cn = DatabaseConnection.get();
                PreparedStatement ps = cn.prepareStatement(sql)){
            
            ps.setString(1, "%" + query + "%");
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    peliculas.add(resultSetToPelicula(rs));
                }
            }
        }
        return peliculas;
    }
    
    @Override
    public void update(Pelicula p) throws SQLException{
        final String sql ="UPDATE Cartelera SET titulo = ?, director = ?, anio = ?, duracion = ?, genero = ? WHERE id = ?";
        
        try(Connection cn = DatabaseConnection.get();
                PreparedStatement ps = cn.prepareStatement(sql)){
            
            ps.setString(1, p.getTitulo());
            ps.setString(2, p.getDirector());
            ps.setInt(3, p.getAnio());
            ps.setInt(4, p.getDuracion());
            ps.setString(5,p.getGenero().name());
            ps.setInt(6, p.getId());
            
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0){
                throw new SQLException("No se encontró la película con ID: " + p.getId());
            }
        }
    }
    
    @Override
    public void delete(int id) throws SQLException{
        final String sql = "DELETE FROM Cartelera WHERE id = ?";
        
        try(Connection cn = DatabaseConnection.get();
                PreparedStatement ps = cn.prepareStatement(sql)){
            
            ps.setInt(1, id);
            int affectedRows = ps.executeUpdate();
            if(affectedRows == 0){
                throw new SQLException("No se encontró la película con ID: " + id);
            }
        }
    }
    
    @Override
    public List<Pelicula> findByGenero(String genero) throws SQLException{
        final String sql = "SELECT * FROM Cartelera WHERE genero = ? ORDER BY titulo";
        
        List<Pelicula> peliculas = new ArrayList<>();
        
        try(Connection cn = DatabaseConnection.get();
                PreparedStatement ps = cn.prepareStatement(sql)){
            ps.setString(1, genero);
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    peliculas.add(resultSetToPelicula(rs));
                }
            }
        }
        return peliculas;
    }
    
    @Override
    public List<Pelicula> findByRangoAnios(int anioDesde, int anioHasta) throws SQLException{
        final String sql = "SELECT * FROM Cartelera WHERE anio BETWEEN ? AND ? ORDER BY titulo";
        
        List<Pelicula> peliculas = new ArrayList<>();
        try(Connection cn = DatabaseConnection.get();
                PreparedStatement ps = cn.prepareStatement(sql)){
            ps.setInt(1, anioDesde);
            ps.setInt(2, anioHasta);
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    peliculas.add(resultSetToPelicula(rs));
                }
            }
        }
        return peliculas;
    }
    
    @Override
    public List<Pelicula> findByGeneroAndRangoAnios(String genero, int anioDesde, int anioHasta) throws SQLException{
        final String sql = "SELECT * FROM Cartelera WHERE genero = ? AND anio BETWEEN ? AND ? ORDER BY titulo";
        
        List<Pelicula> peliculas = new ArrayList<>();
        
        try(Connection cn = DatabaseConnection.get();
                PreparedStatement ps = cn.prepareStatement(sql)){
            ps.setString(1, genero);
            ps.setInt(2, anioDesde);
            ps.setInt(3, anioHasta);
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    peliculas.add(resultSetToPelicula(rs));
                }
            }
        }
        return peliculas;
    }
    
    /**
     * Convierte un ResultSet a objeto Película
     */
    private Pelicula resultSetToPelicula(ResultSet rs) throws SQLException{
        return new Pelicula(
            rs.getInt("id"),
            rs.getString("titulo"),
            rs.getString("director"),
            rs.getInt("anio"),
            rs.getInt("duracion"),
            Genero.valueOf(rs.getString("genero"))
        );
    }
}
