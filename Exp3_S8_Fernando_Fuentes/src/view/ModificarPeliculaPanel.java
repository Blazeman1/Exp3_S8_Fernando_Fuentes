
package view;

import model.Genero;
import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.time.Year;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import model.Pelicula;


public class ModificarPeliculaPanel extends javax.swing.JPanel {
    
    private final JTextField txtBuscar = new JTextField();
    private final JButton btnBuscar = new JButton("Buscar");
    private final JButton btnLimpiarBusqueda = new JButton("Limpiar");
    
    private final JTextField txtTitulo = new JTextField();
    private final JTextField txtDirector = new JTextField();
    private final JSpinner spAnio;
    private final JSpinner spDuracion;
    private final JComboBox<String> cbGenero = new JComboBox<>(Genero.names());
    
    private final JButton btnGuardar = new JButton("Guardar Cambios");
    private final JButton btnLimpiar = new JButton("Limpiar Formulario");
    
    private Integer peliculaId = null;
    
    public ModificarPeliculaPanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(new EmptyBorder(16, 18, 16, 18));

        // Configurar spinners
        int anioMax = Year.now().getValue() + 1;
        spAnio = new JSpinner(new SpinnerNumberModel(2024, 1900, anioMax, 1));
        spDuracion = new JSpinner(new SpinnerNumberModel(90, 1, 999, 1));
        
        // Formatear spinner de año
        JSpinner.NumberEditor edAnio = new JSpinner.NumberEditor(spAnio, "#");
        spAnio.setEditor(edAnio);
        DecimalFormat f = edAnio.getFormat();
        f.setGroupingUsed(false);
        f.setMaximumFractionDigits(0);

        // Panel de búsqueda
        JPanel searchPanel = new JPanel(new BorderLayout(8, 8));
        searchPanel.setBorder(new TitledBorder("Buscar Película"));
        
        JPanel searchControls = new JPanel(new BorderLayout(8, 8));
        searchControls.add(new JLabel("Título o parte del título:"), BorderLayout.NORTH);
        searchControls.add(txtBuscar, BorderLayout.CENTER);
        
        JPanel searchButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchButtons.add(btnLimpiarBusqueda);
        searchButtons.add(btnBuscar);
        
        searchPanel.add(searchControls, BorderLayout.CENTER);
        searchPanel.add(searchButtons, BorderLayout.SOUTH);
    
    
        // Panel de formulario
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 8, 8));
        formPanel.setBorder(new TitledBorder("Datos de la Película"));
        
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
        actions.add(btnGuardar);

        // Deshabilitar formulario inicialmente
        setFormEnabled(false);

        // Agregar componentes al panel principal
        add(searchPanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(actions, BorderLayout.SOUTH);
        
        // Configurar listeners
        btnLimpiarBusqueda.addActionListener(e -> limpiarBusqueda());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
    }
    
    public String getTextoBusqueda() { return txtBuscar.getText().trim(); }
    public String getTitulo() { return txtTitulo.getText().trim(); }
    public String getDirector() { return txtDirector.getText().trim(); }
    public int getAnio() { return (int) spAnio.getValue(); }
    public int getDuracion() { return (int) spDuracion.getValue(); }
    public String getGenero() { return (String) cbGenero.getSelectedItem(); }
    public Integer getPeliculaId() { return peliculaId; }
    
    public JButton getBtnBuscar() { return btnBuscar; }
    public JButton getBtnGuardar() { return btnGuardar; }
    public JButton getBtnLimpiarBusqueda() { return btnLimpiarBusqueda; }
    public JButton getBtnLimpiar() { return btnLimpiar; }
    
    public void setFormEnabled(boolean enabled) {
        txtTitulo.setEnabled(enabled);
        txtDirector.setEnabled(enabled);
        spAnio.setEnabled(enabled);
        spDuracion.setEnabled(enabled);
        cbGenero.setEnabled(enabled);
        btnGuardar.setEnabled(enabled);
        btnLimpiar.setEnabled(enabled);
    }

    public void cargarPelicula(Pelicula pelicula) {
        this.peliculaId = pelicula.getId();
        txtTitulo.setText(pelicula.getTitulo());
        txtDirector.setText(pelicula.getDirector());
        spAnio.setValue(pelicula.getAnio());
        spDuracion.setValue(pelicula.getDuracion());
        cbGenero.setSelectedItem(pelicula.getGenero().name());
        setFormEnabled(true);
    }
    
    public void limpiarBusqueda() {
        txtBuscar.setText("");
        limpiarFormulario();
    }
    
    public void limpiarFormulario() {
        peliculaId = null;
        txtTitulo.setText("");
        txtDirector.setText("");
        spAnio.setValue(2024);
        spDuracion.setValue(90);
        cbGenero.setSelectedIndex(0);
        setFormEnabled(false);
        txtBuscar.requestFocus();
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
