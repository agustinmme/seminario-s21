package cfp.pkg402.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.border.EmptyBorder;

import cfp.pkg402.controller.MenuPrincipalController;
import cfp.pkg402.utils.IconoFactory;
import cfp.pkg402.config.AppConfig;

// Clase UI Menu principal 
public class MenuPrincipalView extends JFrame {

    private JPanel contentPane;
    private MenuPrincipalController controller;

    // Contructor - Inicializa y Configura vista / controlador
    public MenuPrincipalView() {
        controller = new MenuPrincipalController(this);
        initComponents();
    }

    // Inicializo los componentes de la interfaz
    private void initComponents() {
        // Configuraciones basica
        setTitle(AppConfig.TITULO_APP);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, AppConfig.ANCHO_VENTANA, AppConfig.ALTO_VENTANA);
        setLocationRelativeTo(null);

        // Panel principal
        contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        contentPane.setBackground(AppConfig.COLOR_FONDO);
        setContentPane(contentPane);

        // Panel del titulo
        JPanel panelTitulo = new JPanel();
        panelTitulo.setBackground(AppConfig.COLOR_FONDO);
        panelTitulo.setBorder(new EmptyBorder(20, 0, 30, 0));
        contentPane.add(panelTitulo, BorderLayout.NORTH);

        JLabel lblTitulo = new JLabel("Seleccione una opcion");
        lblTitulo.setFont(AppConfig.FONT_TITULO);
        panelTitulo.add(lblTitulo);

        // Panel central para las opciones
        JPanel panelOpciones = new JPanel();
        panelOpciones.setBackground(AppConfig.COLOR_FONDO);
        panelOpciones.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        contentPane.add(panelOpciones, BorderLayout.CENTER);

        // Gestion de Personas
        JPanel panelPersonas = crearOpcionPanel("Gestion de Personas");
        panelOpciones.add(panelPersonas);

        // Icono de personas
        JLabel iconoPersonas = new JLabel();
        iconoPersonas.setIcon(IconoFactory.person());
        iconoPersonas.setHorizontalAlignment(SwingConstants.CENTER);
        iconoPersonas.setBorder(new EmptyBorder(15, 0, 10, 0));
        panelPersonas.add(iconoPersonas, BorderLayout.CENTER);

        // Panel para el texto y boton de Personas
        JPanel textPersonas = new JPanel();
        textPersonas.setBackground(Color.WHITE);
        textPersonas.setLayout(new BoxLayout(textPersonas, BoxLayout.Y_AXIS));
        panelPersonas.add(textPersonas, BorderLayout.SOUTH);

        JLabel lblPersonas = new JLabel("Personas");
        lblPersonas.setFont(AppConfig.FONT_SUBTITULO);
        lblPersonas.setAlignmentX(Component.CENTER_ALIGNMENT);
        textPersonas.add(lblPersonas);

        JLabel lblDescPersonas = new JLabel("Alumnos y Profesores");
        lblDescPersonas.setFont(AppConfig.FONT_NORMAL);
        lblDescPersonas.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblDescPersonas.setBorder(new EmptyBorder(3, 0, 15, 0));
        textPersonas.add(lblDescPersonas);

        JButton btnPersonas = new JButton("Acceder ›");
        btnPersonas.setFont(AppConfig.FONT_NORMAL);
        btnPersonas.setFocusPainted(false);
        btnPersonas.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnPersonas.setBorder(new EmptyBorder(5, 15, 5, 15));
        btnPersonas.setMargin(new Insets(5, 15, 5, 15));
        textPersonas.add(btnPersonas);
        textPersonas.add(Box.createVerticalStrut(15));

        // Gestion de Cursos
        JPanel panelCursos = crearOpcionPanel("Gestion de Cursos");
        panelOpciones.add(panelCursos);

        // Icono de cursos
        JLabel iconoCursos = new JLabel();
        iconoCursos.setIcon(IconoFactory.diplome());
        iconoCursos.setHorizontalAlignment(SwingConstants.CENTER);
        iconoCursos.setBorder(new EmptyBorder(15, 0, 10, 0));
        panelCursos.add(iconoCursos, BorderLayout.CENTER);

        // Panel para el texto y boton de Cursos
        JPanel textCursos = new JPanel();
        textCursos.setBackground(Color.WHITE);
        textCursos.setLayout(new BoxLayout(textCursos, BoxLayout.Y_AXIS));
        panelCursos.add(textCursos, BorderLayout.SOUTH);

        JLabel lblCursos = new JLabel("Cursos");
        lblCursos.setFont(AppConfig.FONT_SUBTITULO);
        lblCursos.setAlignmentX(Component.CENTER_ALIGNMENT);
        textCursos.add(lblCursos);

        JLabel lblDescCursos = new JLabel("Inscripciones y certificados");
        lblDescCursos.setFont(AppConfig.FONT_NORMAL);
        lblDescCursos.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblDescCursos.setBorder(new EmptyBorder(3, 0, 15, 0));
        textCursos.add(lblDescCursos);

        JButton btnCursos = new JButton("Acceder ›");
        btnCursos.setFont(AppConfig.FONT_NORMAL);
        btnCursos.setFocusPainted(false);
        btnCursos.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnCursos.setBorder(new EmptyBorder(5, 15, 5, 15));
        btnCursos.setMargin(new Insets(5, 15, 5, 15));
        textCursos.add(btnCursos);
        textCursos.add(Box.createVerticalStrut(15));

        // Agregar ActionListeners a los botones
        btnPersonas.addActionListener((ActionEvent e) -> {
            controller.abrirGestionPersonas();
        });

        btnCursos.addActionListener((ActionEvent e) -> {
            controller.abrirGestionCursos();
        });
    }

    // Crea un panel de opciones
    private JPanel crearOpcionPanel(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(AppConfig.ANCHO_PANEL_OPCION, AppConfig.ALTO_PANEL_OPCION));
        panel.setBorder(BorderFactory.createLineBorder(AppConfig.COLOR_BORDE));

        // Agrear titulo al panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(AppConfig.COLOR_PANEL_TITULO);
        titlePanel.setLayout(new BorderLayout());
        titlePanel.setBorder(new EmptyBorder(5, 10, 5, 10));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(AppConfig.FONT_NORMAL);
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        panel.add(titlePanel, BorderLayout.NORTH);

        return panel;
    }
}
