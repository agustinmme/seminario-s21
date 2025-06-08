package cfp.pkg402.controller;

import javax.swing.JOptionPane;
import cfp.pkg402.view.MenuPrincipalView;
import cfp.pkg402.view.GestionCursosView;
import cfp.pkg402.view.GestionPersonasView;

// Controlador de la vista MenuPrincipal
public class MenuPrincipalController {

    private MenuPrincipalView view;

    // Contructor - Inicializa y Configura vista / controlador
    public MenuPrincipalController(MenuPrincipalView view) {
        this.view = view;
    }

    // Abre UI de gestion de personas
    public void abrirGestionPersonas() {
        try {
            // Crear y mostrar la vista de gestion de Persona
            GestionPersonasView personaView = new GestionPersonasView();
            personaView.setVisible(true);

        } catch (Exception e) {
            // En caso de error, mostrar mensaje y log en consola
            JOptionPane.showMessageDialog(view,
                    "Error al abrir la gestion de cursos:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);

            e.printStackTrace();
        }
    }

    // Abre UI de gestion de curso
    public void abrirGestionCursos() {
        try {
            // Crear y mostrar la vista de gestion de cursos
            GestionCursosView cursosView = new GestionCursosView();
            cursosView.setVisible(true);

        } catch (Exception e) {
            // En caso de error, mostrar mensaje y log en consola
            JOptionPane.showMessageDialog(view,
                    "Error al abrir la gestion de cursos:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);

            e.printStackTrace();
        }
    }
}
