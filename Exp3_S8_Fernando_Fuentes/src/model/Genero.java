package model;

/**
 * Enumeración que representa los géneros cinematográficos disponibles
 * 
 * Responsabilidades:
 * - Definir los géneros de películas permitidos
 * - Proporcionar métodos utilitarios para trabajar con géneros
 * - Garantiza consistencia en los valores de género
 * 
 * Características:
 * - Valores predefinidos y validados
 * - Método para obtener nombres como array de Strings
 * - Compatible con JComboBox y otros componentes de UI
 * 
 */

public enum Genero {
    // Enum con géneros cinematográficos
    Accion, Drama, Comedia, Terror, Aventura, Ciencia_Ficcion, Romance, Thriller;
    
    /**
     * Obtiene los nombres de los géneros como array de Strings
     * 
     * Uso principal: Poblar JComboBox en la interfaz gráfica
     * 
     * @return String[] Array con los nombres de todos los géneros
     */
    public static String[] names(){
        Genero[] vals = values();
        String[] out = new String[vals.length];
        for (int i = 0; i < vals.length; i++) out[i] = vals[i].name();
        return out;
    }
    
}
