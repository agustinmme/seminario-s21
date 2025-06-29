package cfp.pkg402.utils;

import javax.swing.ImageIcon;

// Util para centralizar la carga de icon
public class IconoFactory {

    // ruta de imgs de iconos
    private static final String BASE_PATH = "/cfp/pkg402/resources/icon/";

    private static ImageIcon load(String fileName) {
        return new ImageIcon(IconoFactory.class.getResource(BASE_PATH + fileName));
    }

    public static ImageIcon person() {
        return load("people-icon.png");
    }

    public static ImageIcon diplome() {
        return load("diplome-icon.png");
    }

    public static ImageIcon save() {
        return load("save.png");
    }

    public static ImageIcon edit() {
        return load("edit.png");
    }

    public static ImageIcon add() {
        return load("add.png");
    }

    public static ImageIcon cancel() {
        return load("cancel.png");
    }

    public static ImageIcon search() {
        return load("search.png");
    }
    
}
