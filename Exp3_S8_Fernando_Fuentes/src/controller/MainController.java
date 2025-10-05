package controller;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.List;
import model.*;
import service.*;
import view.*;

import javax.swing.*;

/**
 * Controlador principal que coordina las interacciones entre la vista y los servicios
 * 
 * Responsabilidades:
 * - Gestionar eventos de la interfaz de usuario
 * - Validar datos antes de enviarlos al servicio
 * - Coordinar el flujo entre la vista y la capa de negocio
 * - Mostrar mensajes de feedback al usuario
 * 
 * Patrón: Controller en arquitectura MVC
 */

public class MainController {

    private final MainFrame view;           // Referencia a la vista principal
    private final PeliculaService service;  // Referencia al servicio de negocio
    private final AtomicBoolean saving = new AtomicBoolean(false);  // Control de concurrencia

    /**
     * Constructor del controlador principal
     * 
     * @param view Vista principal de la aplicación
     * @param service Servicio de gestión de películas
     */
    public MainController(MainFrame view, PeliculaService service) {
        this.view = view;
        this.service = service;
        bind(); // Configura los listeners de eventos
        init(); // Inicializacion adicional
    }
    
    /**
     * Inicialización del controlador
     */
    private void init(){
        // Mostrar el panel de agregar por defecto al iniciar
        view.mostrarPanel("AGREGAR");
        view.getFormPanel().clear();
        
        // Precargar datos para el panel de listar (opcional)
        precargarDatosListar();
    }

    private void precargarDatosListar() {
        // Esto se ejecuta en segundo plano para no bloquear la UI
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    List<Pelicula> peliculas = service.findAll();
                    SwingUtilities.invokeLater(() -> cargarPeliculasEnTabla(peliculas));
                } catch (Exception ex) {
                    System.err.println("Error al precargar datos: " + ex.getMessage());
                }
                return null;
            }
        };
        worker.execute();
    }
    
    /**
     * Configura los listeners para los eventos de la interfaz
     * Vincula los componentes de UI con sus respectivos manejadores
     */
    private void bind() {
        // Navegación entre paneles
        
        // Botón "Agregar" - Mostrar panel de agregar película
        view.getBtnAgregar().addActionListener(e -> {
            view.mostrarPanel("AGREGAR");
            view.getFormPanel().clear();
        });
        
        // Botón "Modificar" - Mostrar panel de modificar película
        view.getBtnModificar().addActionListener(e -> {
            view.mostrarPanel("MODIFICAR");
            view.getModificarPanel().limpiarFormulario();
            System.out.println("Navegando a panel: ELIMINAR");
        });
        
        // Botón "Eliminar" - Mostrar panel de eliminar película
        view.getBtnEliminar().addActionListener(e -> {
            view.mostrarPanel("ELIMINAR");
            view.getEliminarPanel().limpiarFormulario();
        });
        
        // Botón "Listar" - Mostrar panel de listar películas
        view.getBtnListar().addActionListener(e -> {
            view.mostrarPanel("LISTAR");
            onListar(); // Cargar datos automáticamente al mostrar el panel
        });
        
        // Listeners para el panel de listar
        ListarPeliculaPanel listarPanel = view.getListarPanel();
        listarPanel.getBtnActualizar().addActionListener(e -> onListar());
        listarPanel.getBtnAplicarFiltros().addActionListener(e -> onAplicarFiltros());
        listarPanel.getBtnLimpiarFiltros().addActionListener(e -> onLimpiarFiltros());

        // Agregar película
        view.getFormPanel().getBtnGuardar().addActionListener(e -> onSave());
        
        // Modificar película
        ModificarPeliculaPanel modPanel = view.getModificarPanel();
        
        // Botón "Buscar" en el panel de modificar
        modPanel.getBtnBuscar().addActionListener(e -> onBuscarModificar());
        
        // Botón "Guardar Cambios" en el panel de modificar
        modPanel.getBtnGuardar().addActionListener(e -> onModificar());
        
        // Botón "Limpiar Formulario" en el panel de modificar
        modPanel.getBtnLimpiar().addActionListener(e -> {
            modPanel.limpiarFormulario();
            JOptionPane.showMessageDialog(view, "Formulario limpiado", "Información", 
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        // Botón "Limpiar Búsqueda" en el panel de modificar
        modPanel.getBtnLimpiarBusqueda().addActionListener(e -> {
            modPanel.limpiarBusqueda();
            System.out.println("Búsqueda limpiada en panel MODIFICAR");
        });
        
        // Eliminar película
        EliminarPeliculaPanel delPanel = view.getEliminarPanel();
        
        // Botón "Buscar por ID" en el panel de eliminar
        delPanel.getBtnBuscar().addActionListener(e -> onBuscarEliminar());
        
        // Botón "Eliminar Película" en el panel de eliminar
        delPanel.getBtnEliminar().addActionListener(e -> onEliminar());
        
        // Botón "Limpiar Formulario" en el panel de eliminar
        delPanel.getBtnLimpiar().addActionListener(e -> {
            delPanel.limpiarFormulario();
            JOptionPane.showMessageDialog(view, "Formulario limpiado", "Información", 
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        // Botón "Limpiar Búsqueda" en el panel de eliminar
        delPanel.getBtnLimpiarBusqueda().addActionListener(e -> {
            delPanel.limpiarBusqueda();
            System.out.println("Búsqueda limpiada en panel ELIMINAR");
        });
    }
    
    /**
     * Maneja el evento de guardado de una película
     * 
     * Flujo:
     * 1. Obtiene datos del formulario
     * 2. Convierte y valida los datos
     * 3. Invoca el servicio para persistir
     * 4. Muestra feedback al usuario
     * 5. Limpia el formulario en caso de éxito
     */

    private void onSave() {
        if (saving.get()) return;
        saving.set(true);
        
        PeliculaFormPanel form = view.getFormPanel();
        try {
            System.out.println("Iniciando proceso de guardado de película...");
            
            // Validar campos obligatorios
            if (form.getTitulo().isEmpty() || form.getDirector().isEmpty()) {
                JOptionPane.showMessageDialog(view, "Título y Director son campos obligatorios.", "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Convierte el género de String a Enum
            Genero genero = Genero.valueOf(form.getGenero());
            
            // Crea el objeto Pelicula con los datos del formulario
            Pelicula p = new Pelicula(
                    form.getTitulo(),
                    form.getDirector(),
                    form.getAnio(),
                    form.getDuracion(),
                    genero
            );
            
            // Persiste la película mediante el servicio
            int id = service.add(p);
            
            // Muestra mensaje de éxito con el ID generado
            JOptionPane.showMessageDialog(view, "Película guardada (ID: " + id + ")", "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
            
            // Limpia el formulario para nueva entrada
            form.clear();

        } catch (IllegalArgumentException ex) { // valueOf falló
            // Error de conversión de género
            JOptionPane.showMessageDialog(view, "Género inválido.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            // Libera el flag de guardado
            saving.set(false);
        }
    }
    
    private void onBuscarModificar() {
        ModificarPeliculaPanel panel = view.getModificarPanel();
        String busqueda = panel.getTextoBusqueda();
        
        if (busqueda.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Ingrese un título para buscar.", "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            List<Pelicula> resultados = service.findByTitle(busqueda);
            
            if (resultados.isEmpty()) {
                JOptionPane.showMessageDialog(view, "No se encontraron películas con ese título.", 
                    "Búsqueda", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            if (resultados.size() == 1) {
                // Un solo resultado, cargar directamente
                panel.cargarPelicula(resultados.get(0));
                JOptionPane.showMessageDialog(view, "Película encontrada y cargada.", "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Múltiples resultados, mostrar selección
                String[] opciones = resultados.stream()
                    .map(p -> p.getId() + " - " + p.getTitulo() + " (" + p.getAnio() + ")")
                    .toArray(String[]::new);
                
                String seleccion = (String) JOptionPane.showInputDialog(view,
                    "Seleccione la película a modificar:",
                    "Múltiples resultados",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    opciones,
                    opciones[0]);
                
                if (seleccion != null) {
                    int idSeleccionado = Integer.parseInt(seleccion.split(" - ")[0]);
                    Pelicula seleccionada = resultados.stream()
                        .filter(p -> p.getId() == idSeleccionado)
                        .findFirst()
                        .orElse(null);
                    
                    if (seleccionada != null) {
                        panel.cargarPelicula(seleccionada);
                    }
                }
            }
        } catch(Exception ex){
            JOptionPane.showMessageDialog(view, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void onModificar() {
        ModificarPeliculaPanel panel = view.getModificarPanel();
        
        try {
            // Validar campos obligatorios
            if (panel.getTitulo().isEmpty() || panel.getDirector().isEmpty()) {
                JOptionPane.showMessageDialog(view, "Título y Director son campos obligatorios.", "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (panel.getPeliculaId() == null) {
                JOptionPane.showMessageDialog(view, "No hay película cargada para modificar.", "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            Genero genero = Genero.valueOf(panel.getGenero());
            
            Pelicula p = new Pelicula(
                panel.getPeliculaId(),
                panel.getTitulo(),
                panel.getDirector(),
                panel.getAnio(),
                panel.getDuracion(),
                genero
            );
            
            int confirmacion = JOptionPane.showConfirmDialog(view,
                "¿Está seguro de que desea modificar esta película?\n" +
                "ID: " + p.getId() + "\n" +
                "Título: " + p.getTitulo(),
                "Confirmar modificación",
                JOptionPane.YES_NO_OPTION);
            
            if (confirmacion == JOptionPane.YES_OPTION) {
                service.update(p);
                JOptionPane.showMessageDialog(view, "Película modificada exitosamente.", "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
                panel.limpiarFormulario();
            }
            
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(view, "Género inválido.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void onBuscarEliminar() {
        EliminarPeliculaPanel panel = view.getEliminarPanel();
        String idTexto = panel.getTextoBusquedaId();
        
        if (idTexto.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Ingrese un ID para buscar.", "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            int id = Integer.parseInt(idTexto);
            Pelicula pelicula = service.findById(id);
            panel.cargarPelicula(pelicula);
            JOptionPane.showMessageDialog(view, "Película encontrada.", "Éxito",
                JOptionPane.INFORMATION_MESSAGE);
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(view, "ID debe ser un número válido.", "Error", 
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void onEliminar() {
        EliminarPeliculaPanel panel = view.getEliminarPanel();
        
        if (panel.getPeliculaId() == null) {
            JOptionPane.showMessageDialog(view, "No hay película cargada para eliminar. Busque una película primero.", "Sin película cargada",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int confirmacion = JOptionPane.showConfirmDialog(view,
                "¿Esta seguro de que desea ELIMINAR permanentemente esta película?\n" +
                "ID: " + panel.getPeliculaId() + "\n" +
                "Título: " + panel.getTitulo() + "\n\n" +
                "Esta acción no se puede deshacer.",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (confirmacion == JOptionPane.YES_OPTION) {
                service.delete(panel.getPeliculaId());
                JOptionPane.showMessageDialog(view, "Película eliminada exitosamente.", "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
                
                panel.limpiarFormulario();
                System.out.println("Película eliminada: ID= " + panel.getPeliculaId());
            } else {
                System.out.println("Eliminación cancelada por el usuario.");
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void onListar() {
        try {
            List<Pelicula> peliculas = service.findAll();
            cargarPeliculasEnTabla(peliculas);
            JOptionPane.showMessageDialog(view, 
                    "Lista actualizada correctamente." + peliculas.size() + " películas encontradas.",
                    "Éxito",JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Error al cargar las películas: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void onAplicarFiltros() {
        try {
            ListarPeliculaPanel panel = view.getListarPanel();
            String genero = panel.getFiltroGenero();
            int anioDesde = panel.getFiltroAnioDesde();
            int anioHasta = panel.getFiltroAnioHasta();
            
            // Validar rango de años
            if (anioDesde > anioHasta) {
                JOptionPane.showMessageDialog(view, "El año 'desde' no puede ser mayor al año 'hasta'.", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Usar mpetodo de servicio que aplica filtros en la base de datos
            List<Pelicula> peliculasFiltradas;
            
            if(genero == null){
                // Solo filtro por años
                peliculasFiltradas = service.findByRangoAnios(anioDesde, anioHasta);
            }else{
                // Filtro por género y años
                peliculasFiltradas = service.findByGeneroAndRangoAnios(genero, anioDesde, anioHasta);
            }
            
            cargarPeliculasEnTabla(peliculasFiltradas);
            
            // Mostrar mensaje con resultados
            String mensaje = "Filtros aplicados. Se encontraron " + peliculasFiltradas.size() + " películas.";
            if(genero != null){
                mensaje += "\nGénero: " + genero;
            }
            mensaje += "\nAños: " + anioDesde + " - " + anioHasta;
            
            JOptionPane.showMessageDialog(view, mensaje, 
                    "Filtros aplicados.", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Error al aplicar filtros: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void onLimpiarFiltros(){
        ListarPeliculaPanel panel = view.getListarPanel();
        panel.limpiarFiltros();
        // Recargar la lista completa después de limpiar filtros
        onListar();
    }
    
    private void cargarPeliculasEnTabla(List<Pelicula> peliculas) {
        ListarPeliculaPanel panel = view.getListarPanel();
        panel.cargarPeliculasEnTabla(peliculas);
    }
    
}
