package cfp.pkg402;  
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import cfp.pkg402.view.MenuPrincipalView;


public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    // Establecer diseno parecido del sistema donde ejecuta.
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    
                    // Crear y mostrar la ventana principal
                    MenuPrincipalView menuPrincipal = new MenuPrincipalView();
                    menuPrincipal.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}