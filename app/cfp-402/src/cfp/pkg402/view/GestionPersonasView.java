package cfp.pkg402.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import cfp.pkg402.controller.GestionPersonasController;
import cfp.pkg402.config.AppConfig;
import cfp.pkg402.utils.IconoFactory;

// Clase UI Menu gestion personas 
public class GestionPersonasView extends JFrame {

    private JPanel contentPane;
    private GestionPersonasController controller;

    // Componentes principales
    private JTable tablaPersonas;
    private DefaultTableModel modeloPersonas;

    // Panel de detalles de la persona
    private JTextField txtDNI;
    private JTextField txtApellido;
    private JTextField txtNombre;
    private JTextField txtFechaNacimiento;
    private JTextField txtDireccion;
    private JTextField txtTelefono;
    private JTextField txtEmail;
    private JComboBox<String> cmbRol;

    // Botones
    private JButton btnNuevo;
    private JButton btnEditar;
    private JButton btnGuardar;
    private JButton btnCancelar;
    private JButton btnBuscar;
    private JTextField txtBuscar;

    // Contructor - Inicializa y Configura vista / controlador
    public GestionPersonasView() {
        controller = new GestionPersonasController(this);
        initComponents();
        controller.cargarDatos();
    }

    // Inicializo los componentes de la interfaz
    private void initComponents() {
        // Configuraciones basica
        setTitle("Gestion de Personas - " + AppConfig.TITULO_APP);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 1200, 600);
        setLocationRelativeTo(null);

        // Panel principal
        contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        contentPane.setBackground(AppConfig.COLOR_FONDO);
        setContentPane(contentPane);

        // Agregar directamente el panel de personas
        JPanel panelPersonas = crearPanelPersona();
        contentPane.add(panelPersonas, BorderLayout.CENTER);
    }

    // Crea un panel principal persona
    private JPanel crearPanelPersona() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(AppConfig.COLOR_FONDO);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Panel superior (Nuevo,editar,modificar,cancelar y buscar)
        JPanel panelSuperior = crearPanelSuperior();
        panel.add(panelSuperior, BorderLayout.NORTH);

        // Panel central final
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(750);
        splitPane.setBackground(AppConfig.COLOR_FONDO);

        // Panel izquierdo con la tabla
        JPanel panelIzquierdo = createPanelTabla();
        splitPane.setLeftComponent(panelIzquierdo);

        // Panel derecho con detalles de la persona
        JPanel panelDerecho = crearPanelDetalles();
        splitPane.setRightComponent(panelDerecho);

        panel.add(splitPane, BorderLayout.CENTER);

        return panel;
    }

    // Crea un panel botones
    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 8));
        panel.setBackground(AppConfig.COLOR_FONDO);

        // Botones con iconos
        btnNuevo = new JButton("Nuevo", IconoFactory.add());
        btnNuevo.setFont(AppConfig.FONT_NORMAL);
        btnNuevo.setFocusPainted(false);

        btnEditar = new JButton("Editar", IconoFactory.edit());
        btnEditar.setFont(AppConfig.FONT_NORMAL);
        btnEditar.setFocusPainted(false);

        btnGuardar = new JButton("Guardar", IconoFactory.save());
        btnGuardar.setFont(AppConfig.FONT_NORMAL);
        this.habilitarBtnGuardar(false);
        btnGuardar.setFocusPainted(false);

        btnCancelar = new JButton("Cancelar", IconoFactory.cancel());
        btnCancelar.setFont(AppConfig.FONT_NORMAL);
        this.habilitarBtnCancelar(false);
        btnCancelar.setFocusPainted(false);

        // Campo de buscar
        txtBuscar = new JTextField(20);
        txtBuscar.setFont(AppConfig.FONT_NORMAL);
        txtBuscar.setToolTipText("Buscar persona...");

        btnBuscar = new JButton(IconoFactory.search());
        btnBuscar.setFont(AppConfig.FONT_NORMAL);
        btnBuscar.setFocusPainted(false);
        btnBuscar.setToolTipText("Buscar");

        // Agregar componentes
        panel.add(btnNuevo);
        panel.add(btnEditar);
        panel.add(btnGuardar);
        panel.add(btnCancelar);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(new JLabel("Buscar persona:"));
        panel.add(txtBuscar);
        panel.add(btnBuscar);

        // Agregar listeners
        btnNuevo.addActionListener(e -> controller.nuevaPersona());
        btnEditar.addActionListener(e -> controller.editarPersona());
        btnGuardar.addActionListener(e -> controller.guardarPersona());
        btnBuscar.addActionListener(e -> controller.buscarPersona());
        btnCancelar.addActionListener(e -> controller.cancelar());

        return panel;
    }

    // Crea tabla de personas
    private JPanel createPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(AppConfig.COLOR_FONDO);

        // Panel de la tabla
        JPanel panelTabla = new JPanel(new BorderLayout());
        panelTabla.setBorder(BorderFactory.createTitledBorder("Listado de Personas"));
        panelTabla.setBackground(Color.WHITE);

        // Tabla de personas
        String[] columnasPersonas = {"DNI", "Apellido", "Nombre", "Fecha Nac.", "Rol"};
        modeloPersonas = new DefaultTableModel(columnasPersonas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaPersonas = new JTable(modeloPersonas);
        tablaPersonas.setFont(AppConfig.FONT_NORMAL);
        tablaPersonas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaPersonas.setRowHeight(25);
        tablaPersonas.getTableHeader().setFont(AppConfig.FONT_NORMAL_BOLD);

        // Configurar anchos de las columnas y agregar scroll
        tablaPersonas.getColumnModel().getColumn(0).setPreferredWidth(80);  // DNI
        tablaPersonas.getColumnModel().getColumn(1).setPreferredWidth(120); // Apellido
        tablaPersonas.getColumnModel().getColumn(2).setPreferredWidth(120); // Nombre
        tablaPersonas.getColumnModel().getColumn(3).setPreferredWidth(100); // Fecha Nac.
        tablaPersonas.getColumnModel().getColumn(4).setPreferredWidth(100); // Rol
        JScrollPane scrollPersonas = new JScrollPane(tablaPersonas);
        panelTabla.add(scrollPersonas, BorderLayout.CENTER);
        panel.add(panelTabla, BorderLayout.CENTER);

        // Agregar listeners
        tablaPersonas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                controller.personaSeleccionada();
            }
        });

        return panel;
    }

    // Crear detalle de personas
    private JPanel crearPanelDetalles() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Detalles de la Persona"));

        // DNI
        panel.add(crearCampoPanel("DNI: *", txtDNI = new JTextField()));
        txtDNI.setEditable(false);

        // Apellido
        panel.add(crearCampoPanel("Apellido: *", txtApellido = new JTextField()));
        txtApellido.setEditable(false);

        // Nombre
        panel.add(crearCampoPanel("Nombre: *", txtNombre = new JTextField()));
        txtNombre.setEditable(false);

        // Fecha de Nacimiento
        panel.add(crearCampoPanel("Fecha de Nacimiento:", txtFechaNacimiento = new JTextField()));
        txtFechaNacimiento.setEditable(false);
        txtFechaNacimiento.setToolTipText("Formato: dd/MM/yyyy");

        // Direccion
        panel.add(crearCampoPanel("Direccion:", txtDireccion = new JTextField()));
        txtDireccion.setEditable(false);

        // Telefono
        panel.add(crearCampoPanel("Telefono:", txtTelefono = new JTextField()));
        txtTelefono.setEditable(false);

        // Email
        panel.add(crearCampoPanel("Email:", txtEmail = new JTextField()));
        txtEmail.setEditable(false);

        // Rol combo
        JLabel lblRol = new JLabel("Rol:");
        lblRol.setFont(AppConfig.FONT_NORMAL);
        lblRol.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblRol);

        String[] roles = {"Alumno", "Profesor"};
        cmbRol = new JComboBox<>(roles);
        cmbRol.setFont(AppConfig.FONT_NORMAL);
        cmbRol.setAlignmentX(Component.LEFT_ALIGNMENT);
        cmbRol.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        cmbRol.setEnabled(false);
        panel.add(cmbRol);
        panel.add(Box.createVerticalStrut(10));

        //ocupar mas espacio si necsario
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    // Crear panel de label-campo
    private JPanel crearCampoPanel(String labelText, JTextField textField) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JLabel label = new JLabel(labelText);
        label.setFont(AppConfig.FONT_NORMAL);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        textField.setFont(AppConfig.FONT_NORMAL);
        textField.setAlignmentX(Component.LEFT_ALIGNMENT);
        textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));

        panel.add(label);
        panel.add(Box.createVerticalStrut(2));
        panel.add(textField);
        panel.add(Box.createVerticalStrut(10));

        return panel;
    }

    // Habilitar desabilitar campos
    public void habilitarEdicion(boolean habilitar) {
        txtDNI.setEditable(habilitar);
        txtApellido.setEditable(habilitar);
        txtNombre.setEditable(habilitar);
        txtFechaNacimiento.setEditable(habilitar);
        txtDireccion.setEditable(habilitar);
        txtTelefono.setEditable(habilitar);
        txtEmail.setEditable(habilitar);
        cmbRol.setEnabled(habilitar);

        // Switch de color
        Color colorFondo = habilitar ? Color.WHITE : AppConfig.COLOR_FONDO;
        txtDNI.setBackground(colorFondo);
        txtApellido.setBackground(colorFondo);
        txtNombre.setBackground(colorFondo);
        txtFechaNacimiento.setBackground(colorFondo);
        txtDireccion.setBackground(colorFondo);
        txtTelefono.setBackground(colorFondo);
        txtEmail.setBackground(colorFondo);
    }

    // Habilitar desabilitar Botones
    public void habilitarBtnNuevo(boolean habilitar) {
        btnNuevo.setEnabled(habilitar);
    }

    public void habilitarBtnEditar(boolean habilitar) {
        btnEditar.setEnabled(habilitar);
    }

    public void habilitarBtnGuardar(boolean habilitar) {
        btnGuardar.setEnabled(habilitar);
    }

    public void habilitarBtnCancelar(boolean habilitar) {
        btnCancelar.setEnabled(habilitar);
    }

    public void habilitarBtnBuscar(boolean habilitar) {
        btnBuscar.setEnabled(habilitar);
    }

    // Habilitar desabilitar Botones
    public void habilitarTabla(boolean habilitar) {
        this.getTablaPersonas().setEnabled(habilitar);
        if (habilitar) {
            this.getTablaPersonas().setBackground(Color.WHITE);
            this.getTablaPersonas().setForeground(AppConfig.COLOR_FONT);
        } else {
            this.getTablaPersonas().setBackground(AppConfig.COLOR_FONDO);
            this.getTablaPersonas().setForeground(AppConfig.COLOR_FONT);
        }
    }

    //Limpia todos los campos.
    public void limpiarCampos() {
        txtDNI.setText("");
        txtApellido.setText("");
        txtNombre.setText("");
        txtFechaNacimiento.setText("");
        txtDireccion.setText("");
        txtTelefono.setText("");
        txtEmail.setText("");
        cmbRol.setSelectedIndex(0);
    }

    // Getters para que el controller pueda acceder a los componentes
    public JTable getTablaPersonas() {
        return tablaPersonas;
    }

    public DefaultTableModel getModeloPersonas() {
        return modeloPersonas;
    }

    public JTextField getTxtDNI() {
        return txtDNI;
    }

    public JTextField getTxtApellido() {
        return txtApellido;
    }

    public JTextField getTxtNombre() {
        return txtNombre;
    }

    public JTextField getTxtFechaNacimiento() {
        return txtFechaNacimiento;
    }

    public JTextField getTxtDireccion() {
        return txtDireccion;
    }

    public JTextField getTxtTelefono() {
        return txtTelefono;
    }

    public JTextField getTxtEmail() {
        return txtEmail;
    }

    public JComboBox<String> getCmbRol() {
        return cmbRol;
    }

    public JTextField getTxtBuscar() {
        return txtBuscar;
    }
}
