
package view;

import model.Genero;
import model.Pelicula;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.Year;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

public class ListarPeliculaPanel extends JPanel {

    // Componentes de filtros
    private final JComboBox<String> cbGenero = new JComboBox<>();
    private final JSpinner spAnioDesde;
    private final JSpinner spAnioHasta;
    private final JButton btnAplicarFiltros = new JButton("Aplicar Filtros");
    private final JButton btnLimpiarFiltros = new JButton("Limpiar Filtros");
    private final JButton btnActualizar = new JButton("Actualizar Lista");
    
    // Componentes de tabla
    private final JTable tablaPeliculas;
    private final DefaultTableModel modeloTabla;
    private final JScrollPane scrollTabla;
    
    public ListarPeliculaPanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(new EmptyBorder(16, 18, 16, 18));

        // Configurar spinners para años
        int anioActual = Year.now().getValue();
        spAnioDesde = new JSpinner(new SpinnerNumberModel(1900, 1900, anioActual, 1));
        spAnioHasta = new JSpinner(new SpinnerNumberModel(anioActual, 1900, anioActual, 1));
        
        // Configurar combo box de géneros
        configurarComboGenero();
        
        // Configurar tabla
        modeloTabla = new DefaultTableModel(
            new Object[]{"ID", "Título", "Director", "Año", "Duración", "Género"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hacer la tabla de solo lectura
            }
        };
        
        tablaPeliculas = new JTable(modeloTabla);
        tablaPeliculas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaPeliculas.getTableHeader().setReorderingAllowed(false);
        
        // Ajustar anchos de columnas
        tablaPeliculas.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        tablaPeliculas.getColumnModel().getColumn(1).setPreferredWidth(200); // Título
        tablaPeliculas.getColumnModel().getColumn(2).setPreferredWidth(150); // Director
        tablaPeliculas.getColumnModel().getColumn(3).setPreferredWidth(60);  // Año
        tablaPeliculas.getColumnModel().getColumn(4).setPreferredWidth(70);  // Duración
        tablaPeliculas.getColumnModel().getColumn(5).setPreferredWidth(100); // Género
        
        scrollTabla = new JScrollPane(tablaPeliculas);
        scrollTabla.setBorder(new TitledBorder("Lista de Películas (0 películas)"));
        
        // Crear panel de filtros
        JPanel panelFiltros = crearPanelFiltros();
        
        // Crear panel de botones
        JPanel panelBotones = crearPanelBotones();
        
        // Agregar componentes al panel principal
        add(panelFiltros, BorderLayout.NORTH);
        add(scrollTabla, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
    }
    
    private void configurarComboGenero() {
        cbGenero.addItem("Todos los géneros");
        for (String genero : Genero.names()) {
            cbGenero.addItem(genero);
        }
    }
    
    private JPanel crearPanelFiltros() {
        JPanel panel = new JPanel(new GridLayout(2, 3, 8, 8));
        panel.setBorder(new TitledBorder("Filtros de Búsqueda"));
        
        panel.add(new JLabel("Género:"));
        panel.add(new JLabel("Año Desde:"));
        panel.add(new JLabel("Año Hasta:"));
        
        panel.add(cbGenero);
        panel.add(spAnioDesde);
        panel.add(spAnioHasta);
        
        return panel;
    }
    
    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        panel.add(btnActualizar);
        panel.add(btnAplicarFiltros);
        panel.add(btnLimpiarFiltros);
        
        return panel;
    }
    
    // Métodos Getter para los botones
    public JButton getBtnActualizar() { return btnActualizar; }
    public JButton getBtnAplicarFiltros() { return btnAplicarFiltros; }
    public JButton getBtnLimpiarFiltros() { return btnLimpiarFiltros; }
    
    // Métodos para obtener valores de filtros
    public String getFiltroGenero() {
        String seleccion = (String) cbGenero.getSelectedItem();
        return "Todos los géneros".equals(seleccion) ? null : seleccion;
    }
    
    public int getFiltroAnioDesde() {
        return (int) spAnioDesde.getValue();
    }
    
    public int getFiltroAnioHasta() {
        return (int) spAnioHasta.getValue();
    }
    
    // Métodos para manipular la tabla
    public void limpiarTabla() {
        modeloTabla.setRowCount(0);
        actualizarTituloTabla(0);
    }
    
    public void agregarPeliculaATabla(Pelicula pelicula) {
        modeloTabla.addRow(new Object[]{
            pelicula.getId(),
            pelicula.getTitulo(),
            pelicula.getDirector(),
            pelicula.getAnio(),
            pelicula.getDuracion() + " min",
            pelicula.getGenero().name()
        });
    }
    
    public void cargarPeliculasEnTabla(java.util.List<Pelicula> peliculas) {
        limpiarTabla();
        for (Pelicula pelicula : peliculas) {
            agregarPeliculaATabla(pelicula);
        }
        actualizarTituloTabla(peliculas.size());
    }
    
    public void actualizarTituloTabla(int cantidad) {
        scrollTabla.setBorder(new TitledBorder("Lista de Películas (" + cantidad + " películas)"));
    }
    
    public void limpiarFiltros() {
        cbGenero.setSelectedIndex(0);
        int anioActual = Year.now().getValue();
        spAnioDesde.setValue(1900);
        spAnioHasta.setValue(anioActual);
    }
    
    // Método para obtener la película seleccionada
    public Pelicula getPeliculaSeleccionada() {
        int filaSeleccionada = tablaPeliculas.getSelectedRow();
        if (filaSeleccionada == -1) {
            return null;
        }
        
        try {
            int id = (int) modeloTabla.getValueAt(filaSeleccionada, 0);
            String titulo = (String) modeloTabla.getValueAt(filaSeleccionada, 1);
            String director = (String) modeloTabla.getValueAt(filaSeleccionada, 2);
            int anio = (int) modeloTabla.getValueAt(filaSeleccionada, 3);
            String duracionStr = (String) modeloTabla.getValueAt(filaSeleccionada, 4);
            int duracion = Integer.parseInt(duracionStr.replace(" min", ""));
            Genero genero = Genero.valueOf((String) modeloTabla.getValueAt(filaSeleccionada, 5));
            
            return new Pelicula(id, titulo, director, anio, duracion, genero);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // </editor-fold>
@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 405, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 306, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    // End of variables declaration//GEN-END:variables
}
