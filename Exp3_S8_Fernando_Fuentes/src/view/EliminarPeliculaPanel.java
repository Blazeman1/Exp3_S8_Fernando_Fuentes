
package view;

import model.Genero;
import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.time.Year;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import model.Pelicula;

public class EliminarPeliculaPanel extends JPanel {
    
    private final JTextField txtBuscarId = new JTextField(10);
    private final JButton btnBuscar = new JButton("Buscar por ID");
    private final JButton btnLimpiarBusqueda = new JButton("Limpiar");
    
    private final JTextField txtTitulo = new JTextField(20);
    private final JTextField txtDirector = new JTextField(20);
    private final JSpinner spAnio;
    private final JSpinner spDuracion;
    private final JComboBox<String> cbGenero = new JComboBox<>(Genero.names());
    
    private final JButton btnEliminar = new JButton("Eliminar Película");
    private final JButton btnLimpiar = new JButton("Limpiar Formulario");
    
    private Integer peliculaId = null;
    
    public EliminarPeliculaPanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(new EmptyBorder(16, 18, 16, 18));

        // Configurar spinners (solo lectura)
        int anioMax = Year.now().getValue() + 1;
        spAnio = new JSpinner(new SpinnerNumberModel(2024, 1900, anioMax, 1));
        spDuracion = new JSpinner(new SpinnerNumberModel(90, 1, 999, 1));
        
        // Formatear spinner de año
        JSpinner.NumberEditor edAnio = new JSpinner.NumberEditor(spAnio, "#");
        spAnio.setEditor(edAnio);
        DecimalFormat f = edAnio.getFormat();
        f.setGroupingUsed(false);
        f.setMaximumFractionDigits(0);

        // Hacer campos de solo lectura
        txtTitulo.setEditable(false);
        txtDirector.setEditable(false);        
        spAnio.setEnabled(false);
        spDuracion.setEnabled(false);
        cbGenero.setEnabled(false);
        cbGenero.setBackground(Color.WHITE);
        
        // Panel de búsqueda
        JPanel searchPanel = new JPanel(new BorderLayout(8, 8));
        searchPanel.setBorder(new TitledBorder("Buscar Película por ID"));
        
        JPanel searchControls = new JPanel(new BorderLayout(8, 8));
        searchControls.add(new JLabel("ID de la película:"), BorderLayout.NORTH);
        searchControls.add(txtBuscarId, BorderLayout.CENTER);
        
        JPanel searchButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchButtons.add(btnLimpiarBusqueda);
        searchButtons.add(btnBuscar);
        
        searchPanel.add(searchControls, BorderLayout.CENTER);
        searchPanel.add(searchButtons, BorderLayout.SOUTH);

        // Panel de formulario (solo lectura)
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 8, 8));
        formPanel.setBorder(new TitledBorder("Datos de la Película a Eliminar"));
        
        formPanel.add(new JLabel("Título:"));
        formPanel.add(txtTitulo);
        formPanel.add(new JLabel("Director:"));
        formPanel.add(txtDirector);
        formPanel.add(new JLabel("Año:"));
        formPanel.add(spAnio);
        formPanel.add(new JLabel("Duración (min):"));
        formPanel.add(spDuracion);
        formPanel.add(new JLabel("Género:"));
        formPanel.add(cbGenero);
        
        // Panel de botones de acción
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(btnLimpiar);
        actions.add(btnEliminar);

        // Deshabilitar botón eliminar inicialmente
        btnEliminar.setEnabled(false);

        // Agregar componentes al panel principal
        add(searchPanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(actions, BorderLayout.SOUTH);

        // Configurar listeners
        btnLimpiarBusqueda.addActionListener(e -> limpiarBusqueda());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
    }
    
    // Métodos Getter
    public String getTextoBusquedaId() { return txtBuscarId.getText().trim(); }
    public Integer getPeliculaId() { return peliculaId; }
    public String getTitulo(){ return txtTitulo.getText().trim(); }
    public JButton getBtnBuscar() { return btnBuscar; }
    public JButton getBtnEliminar() { return btnEliminar; }
    public JButton getBtnLimpiarBusqueda() { return btnLimpiarBusqueda; }
    public JButton getBtnLimpiar() { return btnLimpiar; }
    
    public void cargarPelicula(Pelicula pelicula) {
        this.peliculaId = pelicula.getId();
        txtTitulo.setText(pelicula.getTitulo());
        txtDirector.setText(pelicula.getDirector());
        spAnio.setValue(pelicula.getAnio());
        spDuracion.setValue(pelicula.getDuracion());
        cbGenero.setSelectedItem(pelicula.getGenero().name());
        btnEliminar.setEnabled(true);
    }
    
    public void limpiarBusqueda() {
        txtBuscarId.setText("");
        limpiarFormulario();
    }
    
    public void limpiarFormulario() {
        peliculaId = null;
        txtTitulo.setText("");
        txtDirector.setText("");
        spAnio.setValue(2024);
        spDuracion.setValue(90);
        cbGenero.setSelectedIndex(0);
        btnEliminar.setEnabled(false);
        txtBuscarId.requestFocus();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
