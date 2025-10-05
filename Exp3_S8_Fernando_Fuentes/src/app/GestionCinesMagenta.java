package app;

import dao.DatabaseConnection;
import dao.PeliculaDAO;
import controller.MainController;
import service.PeliculaService;
import view.MainFrame;

import javax.swing.*;

/**
 * Punto de entrada principal de la aplicación Gestion Cines Magenta
 * 
 * Responsabilidades:
 * - Verifica la conexión a la base de datos al iniciar
 * - Mostrar diálogos informativos sobre el estado de la conexión
 * - Inicializar los componentes MVC (Model-View-Controller)
 * - Lanzar la interfaz gráfica
 * 
 * Flujo de ejecución:
 * 1. Testea la conexión a la base de datos
 * 2. Muestra mensaje de éxito/error al usuario
 * 3. Si la conexión es exitosa, inicializa la aplicación
 * 4. Crea instancias de View, Service y Controller
 */

public class GestionCinesMagenta {

    /**
     * Método principal - punto de entrada de la aplicación
     * @param args Argumentos de línea de comandos (no utilizado)
     */
    public static void main(String[] args) {
        //Prueba la conexión a la base de datos
        boolean ok = DatabaseConnection.databaseTest();
        String msg = ok ? "Conexión a Cine_DB exitosa."
                        : "ERROR conectando a Cine_DB. Revisa credenciales/servicio.";
        System.out.println(msg); //Muestra el mensaje en consola
        
        // Muestra al usuario el estado de conexión
        JOptionPane.showMessageDialog(null, msg, "Estado de Conexión",
                ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
        
        // Si la conexión falla, termina la ejecución
        if (!ok) return;
        
        // Inicializa la interfaz gráfica en el hilo de eventos de Swing
        SwingUtilities.invokeLater(() -> {
            MainFrame view = new MainFrame();
            PeliculaService service = new PeliculaService(new PeliculaDAO());
            new MainController(view, service);
            view.setVisible(true);
        });
    }
}
