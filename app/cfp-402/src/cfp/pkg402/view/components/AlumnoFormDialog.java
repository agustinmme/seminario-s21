package cfp.pkg402.view.components;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import cfp.pkg402.config.AppConfig;
import cfp.pkg402.utils.IconoFactory;

// Clase UI Dialog
public class AlumnoFormDialog extends JDialog {

    private JTextField txtDni;
    private JTable tablaResultados;
    private DefaultTableModel modeloTabla;
    private JScrollPane scrollPane;
    private JButton btnBuscar;
    private JButton btnAgregar;
    private JButton btnCancelar;
    private JTextField txtModulosAprobados;
    private JLabel lblModulosAprobados;

    private boolean confirmed = false;
    private Object[] alumnoSeleccionado = null;

    // Callback - esto o separarlo de controller
    public interface AlumnoBuscador {

        List<Object[]> buscarAlumnosPorDni(String dni);
    }

    private AlumnoBuscador buscador;

    // Contructor - Inicializa y Configura vista / controlador
    public AlumnoFormDialog(Frame parent, String nombreCurso, AlumnoBuscador buscador) {
        super(parent, true);
        this.buscador = buscador;
        initComponents(nombreCurso);
        setLocationRelativeTo(parent);
        this.mostrarTodosLosAlumnos(false);
    }

    private void initComponents(String nombreCurso) {
        setTitle("Agregar Alumno");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(true);

        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Panel superior
        JLabel lblTitulo = new JLabel("Agregar alumno al curso: " + nombreCurso);
        lblTitulo.setFont(AppConfig.FONT_NORMAL.deriveFont(Font.BOLD));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        panelPrincipal.add(lblTitulo, BorderLayout.NORTH);

        // Panel busqueda/resultado
        JPanel centerPanel = crearPanelCentral();
        panelPrincipal.add(centerPanel, BorderLayout.CENTER);

        // Panel bontones
        JPanel buttonsPanel = crearPanelBotones();
        panelPrincipal.add(buttonsPanel, BorderLayout.SOUTH);

        add(panelPrincipal);

        // Configurar tamano inicial 
        setSize(600, 450); // TODO: Usar APP Config
        setMinimumSize(new Dimension(500, 350));
    }

    private JPanel crearPanelCentral() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // Panel de busqueda
        JPanel panelBusqueda = crearPanelBusqueda();
        panel.add(panelBusqueda, BorderLayout.NORTH);

        // Panel de resultados
        JPanel panelResultados = crearPanelResultados();
        panel.add(panelResultados, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelBusqueda() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("Busqueda"));

        JLabel lblDni = new JLabel("DNI:");
        lblDni.setFont(AppConfig.FONT_NORMAL);

        txtDni = new JTextField(15);
        txtDni.setFont(AppConfig.FONT_NORMAL);

        // Botones
        btnBuscar = new JButton(IconoFactory.search());
        btnBuscar.setFont(AppConfig.FONT_NORMAL);
        btnBuscar.addActionListener(e -> buscarAlumnos());

        JButton btnMostrarTodos = new JButton("Mostrar Todos");
        btnMostrarTodos.setFont(AppConfig.FONT_NORMAL);
        btnMostrarTodos.addActionListener(e -> mostrarTodosLosAlumnos(true));

        panel.add(lblDni);
        panel.add(txtDni);
        panel.add(btnBuscar);
        panel.add(btnMostrarTodos);

        return panel;
    }

    private JPanel crearPanelResultados() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        // Crear sub panales
        JPanel modulosPanel = crearPanelModulos();
        JPanel resultsPanel = crearPanelTablaResultados();

        panel.add(resultsPanel, BorderLayout.CENTER);
        panel.add(modulosPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel crearPanelTablaResultados() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Resultados"));

        // Configurar tabla no editable
        String[] columnas = {"DNI", "Apellido", "Nombre"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaResultados = new JTable(modeloTabla);
        tablaResultados.setFont(AppConfig.FONT_NORMAL);
        tablaResultados.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaResultados.getTableHeader().setFont(AppConfig.FONT_NORMAL_BOLD);

        // Configurar ancho de columnas
        tablaResultados.getColumnModel().getColumn(0).setPreferredWidth(80);  // DNI
        tablaResultados.getColumnModel().getColumn(1).setPreferredWidth(120); // Apellido
        tablaResultados.getColumnModel().getColumn(2).setPreferredWidth(120); // Nombre

        // Listener para selecionar fila
        tablaResultados.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                actualizarEstadoBotones();
            }
        });

        scrollPane = new JScrollPane(tablaResultados);
        scrollPane.setPreferredSize(new Dimension(500, 180)); // TODO: Ver app config

        mostrarMensajeInicial();

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelModulos() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("Informacion del Alumno"));

        lblModulosAprobados = new JLabel("Modulos Aprobados:");
        lblModulosAprobados.setFont(AppConfig.FONT_NORMAL);

        txtModulosAprobados = new JTextField(10);
        txtModulosAprobados.setFont(AppConfig.FONT_NORMAL);
        txtModulosAprobados.setEditable(true);
        txtModulosAprobados.setText("");

        panel.add(lblModulosAprobados);
        panel.add(txtModulosAprobados);

        return panel;
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        btnAgregar = new JButton("Agregar");
        btnAgregar.setFont(AppConfig.FONT_NORMAL);
        btnAgregar.addActionListener(e -> confirmar());
        btnAgregar.setEnabled(false);

        btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(AppConfig.FONT_NORMAL);
        btnCancelar.addActionListener(e -> cancelar());

        panel.add(btnAgregar);
        panel.add(btnCancelar);

        return panel;
    }

    private void mostrarTodosLosAlumnos(Boolean inicio) {
        try {
            // Limpiar resultados anteriores
            limpiarResultados();

            // Buscar todos los alumnos 
            List<Object[]> resultados = buscador.buscarAlumnosPorDni("");

            if (inicio && (resultados == null || resultados.isEmpty())) {
                JOptionPane.showMessageDialog(this,
                        "No hay alumnos disponibles para agregar a este curso.",
                        "Sin alumnos disponibles",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Agregar resultados a la tabla
            for (Object[] alumno : resultados) {
                modeloTabla.addRow(alumno);
            }

            // Habilitar la tabla para permitir seleccion
            tablaResultados.setEnabled(true);

            actualizarEstadoBotones();

            // Mostrar cantidad de resultados
            if (inicio) {
                JOptionPane.showMessageDialog(this,
                        "Se encontraron " + resultados.size() + " alumnos disponibles.",
                        "Resultados",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar alumnos: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscarAlumnos() {
        String dni = txtDni.getText().trim();

        if (dni.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, ingrese un DNI para buscar o use 'Mostrar Todos'.",
                    "DNI requerido",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!dni.matches("\\d+")) {
            JOptionPane.showMessageDialog(this,
                    "El DNI debe contener solo numeros.",
                    "DNI invalido",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Limpiar resultados anteriores
            limpiarResultados();

            // Buscar alumnos
            List<Object[]> resultados = buscador.buscarAlumnosPorDni(dni);

            if (resultados == null || resultados.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No se encontraron alumnos disponibles con el DNI especificado.\n"
                        + "El alumno puede estar ya inscripto en este curso.",
                        "Sin resultados",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Agregar resultados a la tabla
            for (Object[] alumno : resultados) {
                modeloTabla.addRow(alumno);
            }

            // Habilitar la tabla para permitir seleccion
            tablaResultados.setEnabled(true);

            actualizarEstadoBotones();

            // Auto-seleccionar si hay un solo resultado
            if (resultados.size() == 1) {
                tablaResultados.setRowSelectionInterval(0, 0);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al buscar alumnos: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarResultados() {
        modeloTabla.setRowCount(0);

        // Verificar que txtModulosAprobados este inicializado antes de usarlo
        if (txtModulosAprobados != null) {
            txtModulosAprobados.setText("0");
        }

        actualizarEstadoBotones();
    }

    private void actualizarEstadoBotones() {
        boolean haySeleccion = tablaResultados.getSelectedRow() != -1
                && tablaResultados.isEnabled()
                && modeloTabla.getRowCount() > 0;
        btnAgregar.setEnabled(haySeleccion);
    }

    // Mostar mensaje y deshabilitar selecion
    private void mostrarMensajeInicial() {
        Object[] mensajeInicial = {"", "Ingrese DNI para buscar alumno", ""};
        modeloTabla.addRow(mensajeInicial);
        tablaResultados.setEnabled(false);

        // Reset modulos
        if (txtModulosAprobados != null) {
            txtModulosAprobados.setText("");
        }
    }

    private void confirmar() {
        int filaSeleccionada = tablaResultados.getSelectedRow();

        if (filaSeleccionada == -1 || !tablaResultados.isEnabled()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, seleccione un alumno de la lista",
                    "Seleccion requerida",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validar el campo de modulos aprobados
        String modulosTexto = txtModulosAprobados.getText().trim();
        if (modulosTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, ingrese la cantidad de modulos aprobados",
                    "Modulos requeridos",
                    JOptionPane.WARNING_MESSAGE);
            txtModulosAprobados.requestFocus();
            return;
        }

        try {
            int modulosAprobados = Integer.parseInt(modulosTexto);
            if (modulosAprobados < 0) {
                JOptionPane.showMessageDialog(this,
                        "La cantidad de modulos aprobados no puede ser negativa",
                        "Valor invalido",
                        JOptionPane.WARNING_MESSAGE);
                txtModulosAprobados.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, ingrese un numero valido para los modulos aprobados",
                    "Numero invalido",
                    JOptionPane.WARNING_MESSAGE);
            txtModulosAprobados.requestFocus();
            return;
        }

        // Crear array con datos del alumno
        alumnoSeleccionado = new Object[4];
        alumnoSeleccionado[0] = modeloTabla.getValueAt(filaSeleccionada, 0); // DNI
        alumnoSeleccionado[1] = modeloTabla.getValueAt(filaSeleccionada, 1); // Apellido
        alumnoSeleccionado[2] = modeloTabla.getValueAt(filaSeleccionada, 2); // Nombre
        alumnoSeleccionado[3] = modulosTexto; // Modulos

        confirmed = true;
        dispose();
    }

    // Cerrar y liberar memoria
    private void cancelar() {
        confirmed = false;
        alumnoSeleccionado = null;
        dispose();
    }

    // Getters
    public boolean isConfirmed() {
        return confirmed;
    }

    public Object[] getAlumnoSeleccionado() {
        return alumnoSeleccionado;
    }

    public String getDni() {
        return alumnoSeleccionado != null ? (String) alumnoSeleccionado[0] : null;
    }

    public String getApellido() {
        return alumnoSeleccionado != null ? (String) alumnoSeleccionado[1] : null;
    }

    public String getNombre() {
        return alumnoSeleccionado != null ? (String) alumnoSeleccionado[2] : null;
    }

    public String getModulos() {
        return alumnoSeleccionado != null ? (String) alumnoSeleccionado[3] : null;
    }

    public String getModulosAprobados() {
        return txtModulosAprobados != null ? txtModulosAprobados.getText() : "";
    }

    public Object[] getDatosAlumno() {
        return getAlumnoSeleccionado();
    }

}
