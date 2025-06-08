package cfp.pkg402.controller;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import cfp.pkg402.view.GestionPersonasView;

// Controlador de la vista gestion persona
public class GestionPersonasController {

    private GestionPersonasView view;
    private boolean modoEdicion = false;
    private boolean esNuevo = false;
    private int filaSeleccionada = -1;

    // Regex & formatos
    private static final Pattern PATRON_EMAIL = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );
    private static final Pattern PATRON_TELEFONO = Pattern.compile(
            "^[0-9]{2,3}-[0-9]{4}-[0-9]{4}$|^[0-9]{8,12}$"
    );
    private static final Pattern PATRON_DNI = Pattern.compile("^[0-9]{7,8}$");

    private static final SimpleDateFormat FORMATO_FECHA = new SimpleDateFormat("dd/MM/yyyy");

    // Contructor - Inicializa y Configura vista / controlador
    public GestionPersonasController(GestionPersonasView view) {
        this.view = view;
        FORMATO_FECHA.setLenient(false); // forzar que ingresen bien la fecha
    }

    // Controlar crear nueva persona
    public void nuevaPersona() {
        if (modoEdicion) {
            int opcion = JOptionPane.showConfirmDialog(
                    view,
                    "Hay cambios sin guardar. Desea continuar sin guardar?",
                    "Confirmar",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
            // Si no es cancelar
            if (opcion != JOptionPane.YES_OPTION) {
                return;
            }
        }

        // Limpiar todo
        view.getTablaPersonas().clearSelection();
        view.limpiarCampos();

        // Habilitar edicion
        view.habilitarEdicion(true);
        modoEdicion = true;
        esNuevo = true;
        view.habilitarBtnNuevo(false);
        view.habilitarBtnEditar(false);
        view.habilitarBtnBuscar(false);
        view.habilitarTabla(false);
        view.habilitarBtnGuardar(true);
        view.habilitarBtnCancelar(true);
        // Enfocar el primer campo
        view.getTxtDNI().requestFocus();
        // Mensaje al crear
        JOptionPane.showMessageDialog(
                view,
                "Complete los campos obligatorios (*) para crear una nueva persona",
                "Nueva Persona",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // Controlar Editar persona
    public void editarPersona() {
        int selectedRow = view.getTablaPersonas().getSelectedRow();

        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(
                    view,
                    "Por favor, seleccione una persona para editar",
                    "Seleccion requerida",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Habilitar edicion
        modoEdicion = true;
        esNuevo = false;
        filaSeleccionada = selectedRow;
        view.habilitarBtnNuevo(false);
        view.habilitarBtnEditar(false);
        view.habilitarBtnBuscar(false);
        view.habilitarTabla(false);
        view.habilitarBtnGuardar(true);
        view.habilitarBtnCancelar(true);
        // Habilitar campos
        view.habilitarEdicion(true);
        // Mensaje al editar
        JOptionPane.showMessageDialog(
                view,
                "Puede modificar los datos de la persona seleccionada",
                "Editar Persona",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // Controlar guardar persona(actualizar o nueva)
    public void guardarPersona() {
        if (!modoEdicion) {
            JOptionPane.showMessageDialog(
                    view,
                    "No hay datos para guardar. Usar 'Nuevo' o 'Editar' primero",
                    "Sin cambios",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        // Validar datos
        if (!validarDatos()) {
            return;
        }

        try {
            boolean eraNuevo = esNuevo;

            // Resetear estado inmediatamente
            modoEdicion = false;
            esNuevo = false;

            // Deshabilitar edicion y botones
            view.habilitarEdicion(false);
            view.habilitarBtnNuevo(true);
            view.habilitarBtnEditar(true);
            view.habilitarBtnBuscar(true);
            view.habilitarTabla(true);
            view.habilitarBtnGuardar(false);
            view.habilitarBtnCancelar(false);

            if (eraNuevo) {
                guardarNuevaPersona();
            } else {
                actualizarPersonaExistente();
            }
            // Mensaje de exito
            JOptionPane.showMessageDialog(
                    view,
                    "Persona guardada correctamente",
                    "Exito",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    view,
                    "Error al guardar: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Controlar guardar nueva persona
    private void guardarNuevaPersona() {
        String dni = view.getTxtDNI().getText().trim();

        // Verificar que el DNI no exista
        DefaultTableModel modelo = view.getModeloPersonas();
        for (int i = 0; i < modelo.getRowCount(); i++) {
            if (modelo.getValueAt(i, 0).toString().equals(dni)) {
                throw new RuntimeException("Ya existe una persona con el DNI: " + dni);
            }
        }

        // Agregar nueva fila
        Object[] nuevaFila = {
            dni,
            view.getTxtApellido().getText().trim(),
            view.getTxtNombre().getText().trim(),
            view.getTxtFechaNacimiento().getText().trim(),
            view.getCmbRol().getSelectedItem().toString()
        };

        modelo.addRow(nuevaFila);

        // Seleccionar la nueva fila
        int nuevaFilaIndex = modelo.getRowCount() - 1;
        view.getTablaPersonas().setRowSelectionInterval(nuevaFilaIndex, nuevaFilaIndex);
        view.getTablaPersonas().scrollRectToVisible(
                view.getTablaPersonas().getCellRect(nuevaFilaIndex, 0, true)
        );
    }

    // Actualizar persona
    private void actualizarPersonaExistente() {
        DefaultTableModel modelo = view.getModeloPersonas();

        // Actualizar datos en la tabla
        modelo.setValueAt(view.getTxtDNI().getText().trim(), filaSeleccionada, 0);
        modelo.setValueAt(view.getTxtApellido().getText().trim(), filaSeleccionada, 1);
        modelo.setValueAt(view.getTxtNombre().getText().trim(), filaSeleccionada, 2);
        modelo.setValueAt(view.getTxtFechaNacimiento().getText().trim(), filaSeleccionada, 3);
        modelo.setValueAt(view.getCmbRol().getSelectedItem().toString(), filaSeleccionada, 4);

        // Refrescar la tabla
        modelo.fireTableRowsUpdated(filaSeleccionada, filaSeleccionada);
    }

    // Controlar Buscar persona
    public void buscarPersona() {
        String textoBusqueda = view.getTxtBuscar().getText().trim().toLowerCase();

        // Verificar que tenga datos el campo de busqueda
        if (textoBusqueda.isEmpty()) {
            JOptionPane.showMessageDialog(
                    view,
                    "Ingrese un dni,nombre o apellido",
                    "Buscar vacia",
                    JOptionPane.WARNING_MESSAGE
            );
            view.getTxtBuscar().requestFocus();
            return;
        }

        DefaultTableModel modelo = view.getModeloPersonas();
        boolean encontrado = false;

        // Buscar en todas las filas
        for (int i = 0; i < modelo.getRowCount(); i++) {
            boolean coincide = false;

            // Buscar en todas las columnas
            for (int j = 0; j < modelo.getColumnCount(); j++) {
                String valor = modelo.getValueAt(i, j).toString().toLowerCase();
                if (valor.contains(textoBusqueda)) {
                    coincide = true;
                    break;
                }
            }
            // Se podria filtrar tambien y dejar un sola
            if (coincide) {
                // Seleccionar la fila encontrada
                view.getTablaPersonas().setRowSelectionInterval(i, i);
                view.getTablaPersonas().scrollRectToVisible(
                        view.getTablaPersonas().getCellRect(i, 0, true)
                );
                view.cargarDetallesPersona();
                encontrado = true;
                break;
            }
        }

        if (!encontrado) {
            JOptionPane.showMessageDialog(
                    view,
                    "No se encontraron persona/s que coincidan con: " + textoBusqueda,
                    "Sin resultados",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    // Controlar cuando esta selecionada
    public void personaSeleccionada() {
        if (!view.getTablaPersonas().isEnabled()) {
            return;
        }
        if (modoEdicion) {
            // Si esta en modo edicion
            int opcion = JOptionPane.showConfirmDialog(
                    view,
                    "Hay cambios sin guardar. Desea cancelar los cambios?",
                    "Cambios sin guardar",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (opcion != JOptionPane.YES_OPTION) {
                // Restaurar seleccion anterior
                if (filaSeleccionada >= 0 && filaSeleccionada < view.getTablaPersonas().getRowCount()) {
                    view.getTablaPersonas().setRowSelectionInterval(filaSeleccionada, filaSeleccionada);
                }
                return;
            } else {
                // Cancelar edicion
                modoEdicion = false;
                esNuevo = false;
                view.habilitarEdicion(false);
            }
        }

        // Cargar detalles de la persona seleccionada
        view.cargarDetallesPersona();
    }

    //Validador de datos 
    private boolean validarDatos() {
        StringBuilder errores = new StringBuilder();

        // Validar DNI
        String dni = view.getTxtDNI().getText().trim();
        if (dni.isEmpty()) {
            errores.append("- El DNI es obligatorio\n");
        } else if (!PATRON_DNI.matcher(dni).matches()) {
            errores.append("- El DNI debe tener entre 7 y 8 digitos\n");
        }

        // Validar Apellido
        String apellido = view.getTxtApellido().getText().trim();
        if (apellido.isEmpty()) {
            errores.append("- El apellido es obligatorio\n");
        } else if (apellido.length() < 2) {
            errores.append("- El apellido debe tener al menos 2 caracteres\n");
        }

        // Validar Nombre
        String nombre = view.getTxtNombre().getText().trim();
        if (nombre.isEmpty()) {
            errores.append("- El nombre es obligatorio\n");
        } else if (nombre.length() < 2) {
            errores.append("- El nombre debe tener al menos 2 caracteres\n");
        }

        // Validar Fecha de Nacimiento (opcional)
        String fechaNac = view.getTxtFechaNacimiento().getText().trim();
        if (!fechaNac.isEmpty()) {
            if (!validarFecha(fechaNac)) {
                errores.append("- La fecha de nacimiento debe tener formato dd/MM/yyyy\n");
            }
        }

        // Validar Email (opcional)
        String email = view.getTxtEmail().getText().trim();
        if (!email.isEmpty()) {
            if (!PATRON_EMAIL.matcher(email).matches()) {
                errores.append("- El formato del email no es valido\n");
            }
        }

        // Validar Telefono (opcional)
        String telefono = view.getTxtTelefono().getText().trim();
        if (!telefono.isEmpty()) {
            if (!PATRON_TELEFONO.matcher(telefono).matches()) {
                errores.append("- El telefono debe tener formato: XX-XXXX-XXXX o 8-12 digito\n");
            }
        }

        // Mostrar errores si existen
        if (errores.length() > 0) {
            JOptionPane.showMessageDialog(
                    view,
                    "Por favor, corrija los siguientes errores:\n\n" + errores.toString(),
                    "Errores de validacion",
                    JOptionPane.ERROR_MESSAGE
            );
            return false;
        }

        return true;
    }

    // Validador fecha
    private boolean validarFecha(String fecha) {
        try {
            Date fechaParseada = FORMATO_FECHA.parse(fecha);

            // Verificar que la fecha no sea futura
            Date hoy = new Date();
            if (fechaParseada.after(hoy)) {
                return false;
            }

            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    // get modo edicion
    public boolean isModoEdicion() {
        return modoEdicion;
    }

    // Cancelar
    public void cancelar() {
        if (modoEdicion) {
            modoEdicion = false;
            esNuevo = false;
            view.limpiarCampos();
            view.habilitarEdicion(false);
            view.habilitarBtnNuevo(true);
            view.habilitarBtnEditar(true);
            view.habilitarBtnBuscar(true);
            view.habilitarTabla(true);
            view.habilitarBtnGuardar(false);
            view.habilitarBtnCancelar(false);
        }
    }
}
