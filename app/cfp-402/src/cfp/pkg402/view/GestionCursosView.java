package cfp.pkg402.view;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.ArrayList;

import cfp.pkg402.controller.GestionCursosController;
import cfp.pkg402.config.AppConfig;
import cfp.pkg402.utils.IconoFactory;

// Clase UI Menu principal - Gestion completa de cursos
public class GestionCursosView extends JFrame {

    private JPanel contentPane;
    private GestionCursosController controller;

    // Componentes principales
    private JTable tablaCursos;
    private JTable tablaAlumnos;
    private DefaultTableModel modeloCursos;
    private DefaultTableModel modeloAlumnos;

    // Panel de detalles del curso
    private JTextField txtCodigo;
    private JTextField txtNombre;
    private JTextArea txtDescripcion;
    private JTextField txtFechaInicio;
    private JTextField txtFechaFin;
    private JComboBox<String> cboProfesor;

    // Modulos
    private JComboBox<String>[] cboModulos;
    private JPanel panelModulos;

    // Botones principales
    private JButton btnNuevo;
    private JButton btnEditar;
    private JButton btnGuardar;
    private JButton btnCancelar;
    private JButton btnBuscar;
    private JTextField txtBuscar;

    // Botones de certificados
    private JButton btnGenerarCertificado;
    private JButton btnCompletarCertificado;
    private JButton btnGenerarFicha;

    // Botones para gestion alumno
    private JButton btnAgregarAlumno;
    private JButton btnEditarModulos;
    private JButton btnVerDetalles;

    // Panel de alumnos
    private JPanel panelAlumnos;

    // Constructor - Inicializa y Configura vista / controlador
    public GestionCursosView() {
        controller = new GestionCursosController(this);
        initComponents();
        controller.cargarDatosIniciales();
        controller.actualizarEstadoBotonesCertificado();
    }

    // Inicializo los componentes de la interfaz
    private void initComponents() {
        setTitle("Gestion de Cursos - " + AppConfig.TITULO_APP);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 1200, 700);
        setLocationRelativeTo(null);

        // Panel principal
        contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        contentPane.setBackground(AppConfig.COLOR_FONDO);
        setContentPane(contentPane);

        // Agregar directamente el panel de cursos
        JPanel panelCursos = crearPanelCurso();
        contentPane.add(panelCursos, BorderLayout.CENTER);
    }

    // Crea un panel de curso
    private JPanel crearPanelCurso() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(AppConfig.COLOR_FONDO);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Panel superior
        JPanel panelSuperior = crearPanelSuperior();
        panel.add(panelSuperior, BorderLayout.NORTH);

        // Panel central 
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(900);
        splitPane.setBackground(AppConfig.COLOR_FONDO);

        // Panel izquierdo
        JPanel panelIzquierdo = crearPanelTablas();
        splitPane.setLeftComponent(panelIzquierdo);

        // Panel derecho
        JPanel panelDerecho = crearDetallesCurso();
        splitPane.setRightComponent(panelDerecho);

        panel.add(splitPane, BorderLayout.CENTER);

        return panel;
    }

    // Crea un panel de botones cursos - VERSIoN COMPLETA
    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBackground(AppConfig.COLOR_FONDO);

        // Botones con iconos
        btnNuevo = new JButton("Nuevo", IconoFactory.add());
        btnNuevo.setFont(AppConfig.FONT_NORMAL);
        btnNuevo.setFocusPainted(false);
        btnNuevo.setToolTipText("Crear nuevo curso");

        btnEditar = new JButton("Editar", IconoFactory.edit());
        btnEditar.setFont(AppConfig.FONT_NORMAL);
        btnEditar.setFocusPainted(false);
        btnEditar.setToolTipText("Editar curso seleccionado");

        btnGuardar = new JButton("Guardar", IconoFactory.save());
        btnGuardar.setFont(AppConfig.FONT_NORMAL);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setToolTipText("Guardar cambios");
        this.habilitarBtnGuardar(false);

        btnCancelar = new JButton("Cancelar", IconoFactory.cancel());
        btnCancelar.setFont(AppConfig.FONT_NORMAL);
        btnCancelar.setFocusPainted(false);
        btnCancelar.setToolTipText("Cancelar edicion");
        this.habilitarBtnCancelar(false);

        // Campo buscar
        txtBuscar = new JTextField(20);
        txtBuscar.setFont(AppConfig.FONT_NORMAL);
        txtBuscar.setToolTipText("Buscar curso...");

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
        panel.add(new JLabel("Buscar curso:"));
        panel.add(txtBuscar);
        panel.add(btnBuscar);

        // Agregar listeners
        btnNuevo.addActionListener(e -> controller.nuevoCurso());
        btnEditar.addActionListener(e -> controller.editarCurso());
        btnGuardar.addActionListener(e -> controller.guardarCurso());
        btnBuscar.addActionListener(e -> controller.buscarCurso());
        btnCancelar.addActionListener(e -> controller.cancelar());

        return panel;
    }

    // Crea tablas
    private JPanel crearPanelTablas() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(AppConfig.COLOR_FONDO);

        // Titulo
        JPanel panelCursos = new JPanel(new BorderLayout());
        panelCursos.setBorder(BorderFactory.createTitledBorder("Listado de Cursos"));
        panelCursos.setBackground(Color.WHITE);

        // Tabla de cursos
        String[] columnasCursos = {"Codigo", "Nombre", "Fecha Inicio", "Fecha Fin", "Profesor"};
        modeloCursos = new DefaultTableModel(columnasCursos, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaCursos = new JTable(modeloCursos);
        tablaCursos.setFont(AppConfig.FONT_NORMAL);
        tablaCursos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaCursos.setRowHeight(25);
        tablaCursos.getTableHeader().setFont(AppConfig.FONT_NORMAL_BOLD);

        // Configurar anchos de columnas
        tablaCursos.getColumnModel().getColumn(0).setPreferredWidth(80);  // Codigo
        tablaCursos.getColumnModel().getColumn(1).setPreferredWidth(250); // Nombre
        tablaCursos.getColumnModel().getColumn(2).setPreferredWidth(100); // Fecha Inicio
        tablaCursos.getColumnModel().getColumn(3).setPreferredWidth(100); // Fecha Fin
        tablaCursos.getColumnModel().getColumn(4).setPreferredWidth(150); // Profesor

        // Listener para tabla de curso
        tablaCursos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                controller.cursoSeleccionado();
            }
        });

        JScrollPane scrollCursos = new JScrollPane(tablaCursos);
        scrollCursos.setPreferredSize(new Dimension(0, 200));
        panelCursos.add(scrollCursos, BorderLayout.CENTER);

        // Panel inferior alumnos
        panelAlumnos = new JPanel(new BorderLayout());
        panelAlumnos.setBorder(BorderFactory.createTitledBorder("Alumnos del Curso"));
        panelAlumnos.setBackground(Color.WHITE);

        // Configurar tabla de alumnos con mejor informacion
        configurarTablaAlumnos();

        JScrollPane scrollAlumnos = new JScrollPane(tablaAlumnos);
        scrollAlumnos.setPreferredSize(new Dimension(0, 150));

        // Panel de botones para alumnos mejorado
        JPanel panelBotonesAlumnos = crearPanelGestionAlumnos();

        // Agregar componentes al panel de alumnos
        panelAlumnos.add(scrollAlumnos, BorderLayout.CENTER);
        panelAlumnos.add(panelBotonesAlumnos, BorderLayout.SOUTH);

        // Dividir los paneles verticalmente
        JSplitPane splitVertical = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitVertical.setTopComponent(panelCursos);
        splitVertical.setBottomComponent(panelAlumnos);
        splitVertical.setDividerLocation(250);

        panel.add(splitVertical, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Configurar tabla de alumnos con mejor informacion
     */
    private void configurarTablaAlumnos() {
        // Cambiar el header de la ultima columna para ser mas descriptivo
        String[] columnasAlumnos = {"DNI", "Apellido", "Nombre", "Progreso Modulos"};
        modeloAlumnos = new DefaultTableModel(columnasAlumnos, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaAlumnos = new JTable(modeloAlumnos);
        tablaAlumnos.setFont(AppConfig.FONT_NORMAL);
        tablaAlumnos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaAlumnos.setRowHeight(25);
        tablaAlumnos.getTableHeader().setFont(AppConfig.FONT_NORMAL_BOLD);

        // Configurar anchos de columnas optimizados
        tablaAlumnos.getColumnModel().getColumn(0).setPreferredWidth(80);  // DNI
        tablaAlumnos.getColumnModel().getColumn(1).setPreferredWidth(120); // Apellido
        tablaAlumnos.getColumnModel().getColumn(2).setPreferredWidth(120); // Nombre
        tablaAlumnos.getColumnModel().getColumn(3).setPreferredWidth(100); // Progreso

        // Listener para tabla de alumnos
        tablaAlumnos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                // Llamar al controlador para actualizar botones
                if (controller != null) {
                    controller.actualizarEstadoBotonesCertificado();
                }
            }
        });

        // Agregar listener para doble clic
        tablaAlumnos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    controller.mostrarDetallesModulosAlumno();
                }
            }
        });
    }

    /**
     * Panel de gestion de alumnos con funcionalidades completas
     */
    private JPanel crearPanelGestionAlumnos() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));

        // Boton Agregar Alumno
        btnAgregarAlumno = new JButton("Agregar Alumno", IconoFactory.add());
        btnAgregarAlumno.setFont(AppConfig.FONT_NORMAL);
        btnAgregarAlumno.setFocusPainted(false);
        btnAgregarAlumno.setToolTipText("Inscribir un nuevo alumno al curso seleccionado");
        btnAgregarAlumno.addActionListener(e -> controller.mostrarFormularioAgregarAlumno());

        // Boton Editar Modulos
        btnEditarModulos = new JButton("Editar Modulos", IconoFactory.edit());
        btnEditarModulos.setFont(AppConfig.FONT_NORMAL);
        btnEditarModulos.setFocusPainted(false);
        btnEditarModulos.setToolTipText("Editar modulos aprobados del alumno seleccionado");
        btnEditarModulos.addActionListener(e -> controller.editarModulosAlumno());

        // Boton Ver Detalles
        btnVerDetalles = new JButton("Ver Detalle", IconoFactory.search());
        btnVerDetalles.setFont(AppConfig.FONT_NORMAL);
        btnVerDetalles.setFocusPainted(false);
        btnVerDetalles.setToolTipText("Ver detalle de modulos del alumno seleccionado");
        btnVerDetalles.addActionListener(e -> controller.mostrarDetallesModulosAlumno());

        // Agregar componentes al panel
        panel.add(btnAgregarAlumno);
        panel.add(btnEditarModulos);
        panel.add(btnVerDetalles);

        return panel;
    }

    // Panel detalle curso
    private JPanel crearDetallesCurso() {
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(Color.WHITE);
        panelPrincipal.setBorder(BorderFactory.createTitledBorder("Detalles del Curso"));

        // Panel de contenido con scroll
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);

        // Inicializar array de ComboBox para modulos
        cboModulos = new JComboBox[10];

        // Codigo
        contentPanel.add(crearCampoFormularioCurso("Codigo: *", txtCodigo = new JTextField()));
        txtCodigo.setEditable(false);

        // Nombre
        contentPanel.add(crearCampoFormularioCurso("Nombre: *", txtNombre = new JTextField()));
        txtNombre.setEditable(false);

        // Descripcion
        JLabel lblDesc = new JLabel("Descripcion:");
        lblDesc.setFont(AppConfig.FONT_NORMAL);
        lblDesc.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(lblDesc);

        txtDescripcion = new JTextArea(4, 20);
        txtDescripcion.setFont(AppConfig.FONT_NORMAL);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        txtDescripcion.setEditable(false);
        txtDescripcion.setBackground(new Color(238, 238, 238));
        JScrollPane scrollDesc = new JScrollPane(txtDescripcion);
        scrollDesc.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollDesc.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        contentPanel.add(scrollDesc);
        contentPanel.add(Box.createVerticalStrut(10));

        // Fechas
        contentPanel.add(crearCampoFormularioCurso("Fecha Inicio: *", txtFechaInicio = new JTextField()));
        txtFechaInicio.setEditable(false);
        txtFechaInicio.setToolTipText("Formato: dd/MM/yyyy");

        contentPanel.add(crearCampoFormularioCurso("Fecha Fin: *", txtFechaFin = new JTextField()));
        txtFechaFin.setEditable(false);
        txtFechaFin.setToolTipText("Formato: dd/MM/yyyy");

        // Profesor como comboBox
        contentPanel.add(crearComboBox("Profesor: *", cboProfesor = new JComboBox<>()));
        inicializarProfesores();
        cboProfesor.setEnabled(false);

        // Panel para modulos - comboBox
        panelModulos = new JPanel();
        panelModulos.setLayout(new BoxLayout(panelModulos, BoxLayout.Y_AXIS));
        panelModulos.setBackground(Color.WHITE);
        panelModulos.setBorder(BorderFactory.createTitledBorder("Modulos del Curso"));
        panelModulos.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Crear los 10 comboBox para modulos
        inicializarModulos();

        contentPanel.add(panelModulos);
        contentPanel.add(Box.createVerticalStrut(10));

        // Scroll para el contenido
        JScrollPane scrollContent = new JScrollPane(contentPanel);
        scrollContent.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollContent.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollContent.getVerticalScrollBar().setUnitIncrement(16);

        panelPrincipal.add(scrollContent, BorderLayout.CENTER);

        // Panel de certificados - FIJO en la parte inferior
        JPanel panelCertificacion = new JPanel();
        panelCertificacion.setLayout(new BoxLayout(panelCertificacion, BoxLayout.Y_AXIS));
        panelCertificacion.setBorder(BorderFactory.createTitledBorder("Certificaciones"));
        panelCertificacion.setBackground(Color.WHITE);
        panelCertificacion.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnGenerarCertificado = new JButton("Generar Certificado");
        btnGenerarCertificado.setFont(AppConfig.FONT_NORMAL);
        btnGenerarCertificado.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnGenerarCertificado.setFocusPainted(false);
        btnGenerarCertificado.addActionListener(e -> controller.generarCertificado());

        btnCompletarCertificado = new JButton("Completar Certificado Fisico");
        btnCompletarCertificado.setFont(AppConfig.FONT_NORMAL);
        btnCompletarCertificado.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnCompletarCertificado.setFocusPainted(false);
        btnCompletarCertificado.addActionListener(e -> controller.completarCertificadoFisico());

        btnGenerarFicha = new JButton("Generar Ficha de Acreditacion");
        btnGenerarFicha.setFont(AppConfig.FONT_NORMAL);
        btnGenerarFicha.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnGenerarFicha.setFocusPainted(false);
        btnGenerarFicha.addActionListener(e -> controller.generarFichaAcreditacion());

        panelCertificacion.add(btnGenerarCertificado);
        panelCertificacion.add(Box.createVerticalStrut(5));
        panelCertificacion.add(btnCompletarCertificado);
        panelCertificacion.add(Box.createVerticalStrut(5));
        panelCertificacion.add(btnGenerarFicha);

        panelPrincipal.add(panelCertificacion, BorderLayout.SOUTH);

        return panelPrincipal;
    }

    // Valor por defecto profesores - Se quita cuando este funcionando el modelo/db
    private void inicializarProfesores() {
        cboProfesor.addItem("Juan Perez");
        cboProfesor.addItem("Maria Gonzalez");
        cboProfesor.addItem("Carlos Rodriguez");
        cboProfesor.addItem("Ana Martinez");
        cboProfesor.addItem("Luis Garcia");
        cboProfesor.addItem("Elena Lopez");
        cboProfesor.addItem("Roberto Fernandez");
        cboProfesor.addItem("Laura Jimenez");
        cboProfesor.addItem("Pedro Silva");
        cboProfesor.addItem("Carmen Torres");
    }

    // Valor por defecto modulos - Se quita cuando este funcionando el modelo/db
    private void inicializarModulos() {
        String[] opcionesModulos = {
            "", // Opcion vacia
            "Practica Basica",
            "Introduccion al Curso",
            "Fundamentos Basicos",
            "Conceptos Intermedios",
            "Tecnicas Avanzadas",
            "Practica Supervisada",
            "Proyecto Final",
            "Evaluacion Teorica",
            "Evaluacion Practica",
            "Trabajo en Equipo",
            "Comunicacion Efectiva",
            "Resolucion de Problemas",
            "Liderazgo",
            "Innovacion y Creatividad",
            "etica Profesional"
        };

        for (int i = 0; i < 10; i++) {
            cboModulos[i] = new JComboBox<>(opcionesModulos);
            cboModulos[i].setFont(AppConfig.FONT_NORMAL);
            cboModulos[i].setEnabled(false);
            cboModulos[i].setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));

            JPanel panelModulo = crearComboBox("Modulo " + (i + 1) + ":", cboModulos[i]);
            panelModulos.add(panelModulo);
        }
    }

    // Cargar detalles
    public void cargarDetallesCurso() {
        int selectedRow = tablaCursos.getSelectedRow();
        if (selectedRow >= 0) {
            String codigo = modeloCursos.getValueAt(selectedRow, 0).toString();

            // Obtener datos completos del curso desde el controller
            Object[] cursoCompleto = controller.obtenerCursoCompleto(codigo);

            if (cursoCompleto != null) {
                txtCodigo.setText((String) cursoCompleto[0]);
                txtNombre.setText((String) cursoCompleto[1]);
                txtDescripcion.setText((String) cursoCompleto[2]);
                txtFechaInicio.setText((String) cursoCompleto[3]);
                txtFechaFin.setText((String) cursoCompleto[4]);
                cboProfesor.setSelectedItem((String) cursoCompleto[5]);

                // Cargar modulos
                if (cursoCompleto.length > 6 && cursoCompleto[6] instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<String> modulos = (List<String>) cursoCompleto[6];

                    // Limpiar primero - selecionar opcion vacia
                    for (int i = 0; i < 10; i++) {
                        cboModulos[i].setSelectedIndex(0);
                    }

                    // Llenar comboBox con los modulos guardados
                    for (int i = 0; i < modulos.size() && i < 10; i++) {
                        String modulo = modulos.get(i);
                        if (modulo != null && !modulo.trim().isEmpty()) {
                            cboModulos[i].setSelectedItem(modulo);
                        }
                    }
                }
            }
        }
    }

    // Actualizar titulo alumnos
    public void actualizarTituloAlumnos(String nuevoTitulo) {
        if (panelAlumnos != null) {
            ((TitledBorder) panelAlumnos.getBorder()).setTitle(nuevoTitulo);
            panelAlumnos.repaint();
        }
    }

    // Habilitar - deshabilitar inscripcion alumno
    public void habilitarBotonesAlumnos(boolean habilitar) {
        btnAgregarAlumno.setEnabled(habilitar);
        btnEditarModulos.setEnabled(habilitar);
        btnVerDetalles.setEnabled(habilitar);
    }

    // Habilitar - deshabilitar campos
    public void habilitarEdicion(boolean habilitar) {
        txtCodigo.setEditable(habilitar);
        txtNombre.setEditable(habilitar);
        txtDescripcion.setEditable(habilitar);
        txtFechaInicio.setEditable(habilitar);
        txtFechaFin.setEditable(habilitar);
        cboProfesor.setEnabled(habilitar);

        // Habilitar/deshabilitar modulos
        for (int i = 0; i < 10; i++) {
            cboModulos[i].setEnabled(habilitar);
        }

        // Cambiar color de fondo 
        Color colorFondo = habilitar ? Color.WHITE : AppConfig.COLOR_FONDO;
        txtCodigo.setBackground(colorFondo);
        txtNombre.setBackground(colorFondo);
        txtDescripcion.setBackground(colorFondo);
        txtFechaInicio.setBackground(colorFondo);
        txtFechaFin.setBackground(colorFondo);
    }

    // Metodos para habilitar/deshabilitar botones
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

    // Metodos para habilitar/deshabilitar tablas
    public void habilitarTablas(boolean habilitar) {
        this.getTablaCursos().setEnabled(habilitar);
        this.getTablaAlumnos().setEnabled(habilitar);

        // Habilitar/deshabilitar botones de alumnos
        this.habilitarBotonesAlumnos(habilitar);

        if (habilitar) {
            this.getTablaCursos().setBackground(Color.WHITE);
            this.getTablaCursos().setForeground(AppConfig.COLOR_FONT);
            this.getTablaAlumnos().setBackground(Color.WHITE);
            this.getTablaAlumnos().setForeground(AppConfig.COLOR_FONT);

        } else {
            this.getTablaCursos().setBackground(AppConfig.COLOR_FONDO);
            this.getTablaCursos().setForeground(AppConfig.COLOR_FONT);
            this.getTablaAlumnos().setBackground(AppConfig.COLOR_FONDO);
            this.getTablaAlumnos().setForeground(AppConfig.COLOR_FONT);
        }
    }

    // Limpiar campos
    public void limpiarCampos() {
        txtCodigo.setText("");
        txtNombre.setText("");
        txtDescripcion.setText("");
        txtFechaInicio.setText("");
        txtFechaFin.setText("");
        cboProfesor.setSelectedIndex(0);

        // Limpiar modulos - selecionar vacio
        for (int i = 0; i < 10; i++) {
            cboModulos[i].setSelectedIndex(0);
        }
    }

    // Crear label y campo del formulario
    private JPanel crearCampoFormularioCurso(String labelText, JTextField textField) {
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

    // Crear label con combobox
    private JPanel crearComboBox(String labelText, JComboBox<String> comboBox) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JLabel label = new JLabel(labelText);
        label.setFont(AppConfig.FONT_NORMAL);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        comboBox.setFont(AppConfig.FONT_NORMAL);
        comboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        comboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));

        panel.add(label);
        panel.add(Box.createVerticalStrut(2));
        panel.add(comboBox);
        panel.add(Box.createVerticalStrut(10));

        return panel;
    }

    // Getters para que el controller pueda acceder a los componentes
    public JTable getTablaCursos() {
        return tablaCursos;
    }

    public JTable getTablaAlumnos() {
        return tablaAlumnos;
    }

    public DefaultTableModel getModeloCursos() {
        return modeloCursos;
    }

    public DefaultTableModel getModeloAlumnos() {
        return modeloAlumnos;
    }

    public JTextField getTxtCodigo() {
        return txtCodigo;
    }

    public JTextField getTxtNombre() {
        return txtNombre;
    }

    public JTextArea getTxtDescripcion() {
        return txtDescripcion;
    }

    public JTextField getTxtFechaInicio() {
        return txtFechaInicio;
    }

    public JTextField getTxtFechaFin() {
        return txtFechaFin;
    }

    public JComboBox<String> getCboProfesor() {
        return cboProfesor;
    }

    public JTextField getTxtBuscar() {
        return txtBuscar;
    }

    public JComboBox<String>[] getCboModulos() {
        return cboModulos;
    }

    public List<String> getModulosTexto() {
        List<String> modulos = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            String modulo = (String) cboModulos[i].getSelectedItem();
            if (modulo != null && !modulo.trim().isEmpty()) {
                modulos.add(modulo);
            }
        }
        return modulos;
    }

    // Getters para los botones de certificado
    public JButton getBtnGenerarCertificado() {
        return btnGenerarCertificado;
    }

    public JButton getBtnCompletarCertificado() {
        return btnCompletarCertificado;
    }

    public JButton getBtnGenerarFicha() {
        return btnGenerarFicha;
    }

    // Getters adicionales para los nuevos botones de gestion de alumnos
    public JButton getBtnEditarModulos() {
        return btnEditarModulos;
    }

    public JButton getBtnVerDetalles() {
        return btnVerDetalles;
    }
}