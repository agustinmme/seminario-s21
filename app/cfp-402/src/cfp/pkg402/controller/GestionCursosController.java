package cfp.pkg402.controller;

import javax.swing.JOptionPane;
import cfp.pkg402.view.GestionCursosView;
import cfp.pkg402.model.GestionCursosModel;
import cfp.pkg402.view.components.AlumnoFormDialog;
import cfp.pkg402.model.Modulo;
import cfp.pkg402.utils.CertificadoPDFUtil;
import cfp.pkg402.utils.ConstanciaAcreditacionPDFUtil;
import java.util.List;
import java.util.*;

public class GestionCursosController {

    private GestionCursosView view;
    private GestionCursosModel model;

    public GestionCursosController(GestionCursosView view) {
        this.view = view;
        this.model = new GestionCursosModel();
    }

    // Cargar todo inicio
    public void cargarDatosIniciales() {
        try {
            // Get cursos del modelo
            List<Object[]> cursos = model.obtenerTodosCursos();

            // Cargar cursos en la tabla
            for (Object[] curso : cursos) {
                // La tabla muestra codigo, nombre, fechaInicio, fechaFin, profesor
                Object[] filaCurso = {curso[0], curso[1], curso[3], curso[4], curso[5]};
                view.getModeloCursos().addRow(filaCurso);
            }

            // Seleccionar el primer curso por defecto
            if (view.getTablaCursos().getRowCount() > 0) {
                view.getTablaCursos().setRowSelectionInterval(0, 0);
                cursoSeleccionado();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(view,
                    "Error al cargar los datos iniciales:\n" + e.getMessage(),
                    "Error de Conexion",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Get curso por codigo
    public Object[] obtenerCursoCompleto(String codigo) {
        return model.obtenerCursoPorCodigo(codigo);
    }

    // seleccion de un curso en la tabla
    public void cursoSeleccionado() {
        int selectedRow = view.getTablaCursos().getSelectedRow();
        if (selectedRow >= 0) {
            view.cargarDetallesCurso();
            String codigoCurso = view.getModeloCursos().getValueAt(selectedRow, 0).toString();
            String nombreCurso = view.getModeloCursos().getValueAt(selectedRow, 1).toString();
            cargarAlumnosPorCurso(codigoCurso, nombreCurso);

            // Actualizar estado de botones al cambiar de curso
            actualizarEstadoBotonesCertificado();
        }
    }

    // Carga alumno x curso
    private void cargarAlumnosPorCurso(String codigoCurso, String nombreCurso) {
        view.getModeloAlumnos().setRowCount(0);
        view.actualizarTituloAlumnos("Alumnos del Curso \"" + nombreCurso + "\"");

        try {
            List<Object[]> alumnos = model.obtenerAlumnosPorCurso(codigoCurso);

            // Get total de modulos del curso para mostrar el progreso
            List<String> modulosCurso = model.obtenerModulosEspecificosDelCurso(codigoCurso);
            int totalModulos = modulosCurso.size();

            if (!alumnos.isEmpty()) {
                for (Object[] alumno : alumnos) {
                    // Modificar la ultima columna para mostrar mejor informacion
                    String dni = (String) alumno[0];
                    String apellido = (String) alumno[1];
                    String nombre = (String) alumno[2];
                    int modulosAprobados = Integer.parseInt(alumno[3].toString());

                    // Crear descripcion mas informativa
                    String progreso;
                    if (totalModulos > 0) {
                        double porcentaje = (double) modulosAprobados / totalModulos * 100;
                        progreso = String.format("%d/%d (%.0f%%)", modulosAprobados, totalModulos, porcentaje);
                    } else {
                        progreso = "Sin modulos";
                    }

                    Object[] filaModificada = {dni, apellido, nombre, progreso};
                    view.getModeloAlumnos().addRow(filaModificada);
                }
            } else {
                view.getModeloAlumnos().addRow(new Object[]{"", "Sin alumnos inscriptos", "", ""});
            }
        } catch (Exception e) {
            view.getModeloAlumnos().addRow(new Object[]{"", "Error al cargar alumnos", "", ""});
            System.err.println("Error al cargar alumnos: " + e.getMessage());
        }
        actualizarEstadoBotonesCertificado();
    }

    // Update tabla de alumnos dle curso al crear uno nuevo podria unificar con el metodo de arriva...
    private void actualizarTablaAlumnos(String codigoCurso, String nombreCurso) {
        view.getModeloAlumnos().setRowCount(0);
        view.actualizarTituloAlumnos("Alumnos del Curso \"" + nombreCurso + "\"");

        try {
            List<Object[]> alumnos = model.obtenerAlumnosPorCurso(codigoCurso);

            // Get total de modulos del curso para mostrar el progreso
            List<String> modulosCurso = model.obtenerModulosEspecificosDelCurso(codigoCurso);
            int totalModulos = modulosCurso.size();

            if (!alumnos.isEmpty()) {
                for (Object[] alumno : alumnos) {
                    String dni = (String) alumno[0];
                    String apellido = (String) alumno[1];
                    String nombre = (String) alumno[2];
                    int modulosAprobados = Integer.parseInt(alumno[3].toString());

                    // Crear descripcion mas informativa
                    String progreso;
                    if (totalModulos > 0) {
                        double porcentaje = (double) modulosAprobados / totalModulos * 100;
                        progreso = String.format("%d/%d (%.0f%%)", modulosAprobados, totalModulos, porcentaje);
                    } else {
                        progreso = "Sin modulos";
                    }

                    Object[] filaModificada = {dni, apellido, nombre, progreso};
                    view.getModeloAlumnos().addRow(filaModificada);
                }
                int ultimaFila = view.getModeloAlumnos().getRowCount() - 1;
                view.getTablaAlumnos().setRowSelectionInterval(ultimaFila, ultimaFila);
                actualizarEstadoBotonesCertificado();
            } else {
                view.getModeloAlumnos().addRow(new Object[]{"", "Sin alumnos inscriptos", "", ""});
            }
        } catch (Exception e) {
            view.getModeloAlumnos().addRow(new Object[]{"", "Error al cargar alumnos", "", ""});
            System.err.println("Error al actualizar tabla alumnos: " + e.getMessage());
        }
    }

    // Activar crear
    public void nuevoCurso() {
        view.habilitarEdicion(true);
        view.limpiarCampos();
        view.getTablaCursos().clearSelection();

        view.habilitarBtnNuevo(false);
        view.habilitarBtnEditar(false);
        view.habilitarBtnGuardar(true);
        view.habilitarBtnCancelar(true);
        view.habilitarBtnBuscar(false);
        view.habilitarTablas(false);

        String nuevoCodigo = model.generarNuevoCodigo();
        view.getTxtCodigo().setText(nuevoCodigo);
        view.getTxtNombre().requestFocus();
    }

    // Activar edit
    public void editarCurso() {
        int selectedRow = view.getTablaCursos().getSelectedRow();
        if (selectedRow >= 0) {
            view.habilitarEdicion(true);

            view.habilitarBtnNuevo(false);
            view.habilitarBtnEditar(false);
            view.habilitarBtnGuardar(true);
            view.habilitarBtnCancelar(true);
            view.habilitarBtnBuscar(false);
            view.habilitarTablas(false);

            view.getTxtNombre().requestFocus();
        } else {
            JOptionPane.showMessageDialog(view,
                    "Por favor, seleccione un curso para editar",
                    "Curso no seleccionado",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    // Guardar por update o nuevo
    public void guardarCurso() {
        String codigo = view.getTxtCodigo().getText().trim();
        String nombre = view.getTxtNombre().getText().trim();
        String descripcion = view.getTxtDescripcion().getText().trim();
        String fechaInicio = view.getTxtFechaInicio().getText().trim();
        String fechaFin = view.getTxtFechaFin().getText().trim();
        String profesor = (String) view.getCboProfesor().getSelectedItem();
        List<String> modulos = view.getModulosTexto();

        // Validar campos obligatorios
        if (codigo.isEmpty() || nombre.isEmpty() || fechaInicio.isEmpty()
                || fechaFin.isEmpty() || profesor == null) {
            JOptionPane.showMessageDialog(view,
                    "Por favor, complete todos los campos obligatorios (marcados con *)",
                    "Campos incompletos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validar modulos duplicados
        if (!modulos.isEmpty()) {
            Set<String> modulosUnicos = new HashSet<>();
            for (String modulo : modulos) {
                if (modulosUnicos.contains(modulo)) {
                    JOptionPane.showMessageDialog(view,
                            "No se pueden repetir modulos. El modulo \"" + modulo + "\" esta seleccionado mas de una vez",
                            "Modulos duplicados",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                modulosUnicos.add(modulo);
            }
        }

        // Validar formato de fechas
        if (!model.validarFormatoFecha(fechaInicio) || !model.validarFormatoFecha(fechaFin)) {
            JOptionPane.showMessageDialog(view,
                    "Por favor, ingrese las fechas en formato dd/MM/yyyy",
                    "Formato de fecha incorrecto",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Object[] curso = {codigo, nombre, descripcion, fechaInicio, fechaFin, profesor, modulos};

        int selectedRow = view.getTablaCursos().getSelectedRow();
        boolean exito = false;
        boolean esNuevo = selectedRow < 0;

        try {
            if (!esNuevo) {
                // Actualizar curso existente
                String codigoOriginal = view.getModeloCursos().getValueAt(selectedRow, 0).toString();
                exito = model.actualizarCurso(codigoOriginal, curso);

                if (exito) {
                    view.getModeloCursos().setValueAt(codigo, selectedRow, 0);
                    view.getModeloCursos().setValueAt(nombre, selectedRow, 1);
                    view.getModeloCursos().setValueAt(fechaInicio, selectedRow, 2);
                    view.getModeloCursos().setValueAt(fechaFin, selectedRow, 3);
                    view.getModeloCursos().setValueAt(profesor, selectedRow, 4);
                }
            } else {
                // Crear nuevo curso
                exito = model.agregarCurso(curso);

                if (exito) {
                    Object[] filaCurso = {codigo, nombre, fechaInicio, fechaFin, profesor};
                    view.getModeloCursos().addRow(filaCurso);
                    int ultimaFila = view.getModeloCursos().getRowCount() - 1;
                    view.getTablaCursos().setRowSelectionInterval(ultimaFila, ultimaFila);
                }
            }

            if (exito) {
                cancelar();
                view.getTablaCursos().repaint();

                StringBuilder mensaje = new StringBuilder();
                mensaje.append("Curso ").append(esNuevo ? "creado" : "actualizado").append(" exitosamente:\n");
                mensaje.append(codigo).append(" - ").append(nombre).append("\n");
                mensaje.append("Profesor: ").append(profesor).append("\n");

                if (!modulos.isEmpty()) {
                    mensaje.append("Modulos (").append(modulos.size()).append("):\n");
                    for (int i = 0; i < modulos.size(); i++) {
                        mensaje.append("- ").append(modulos.get(i));
                        if (i < modulos.size() - 1) {
                            mensaje.append("\n");
                        }
                    }
                } else {
                    mensaje.append("Sin modulos asignados");
                }

                JOptionPane.showMessageDialog(view,
                        mensaje.toString(),
                        "Guardar Curso",
                        JOptionPane.INFORMATION_MESSAGE);

                if (esNuevo) {
                    cursoSeleccionado();
                }
            } else {
                JOptionPane.showMessageDialog(view,
                        "Error al guardar el curso. Verifique que el codigo no exista o que la conexion a la base de datos este disponible",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view,
                    "Error al guardar el curso:\n" + e.getMessage(),
                    "Error de Base de Datos",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Cancelar
    public void cancelar() {
        view.habilitarEdicion(false);

        view.habilitarBtnNuevo(true);
        view.habilitarBtnEditar(true);
        view.habilitarBtnGuardar(false);
        view.habilitarBtnCancelar(false);
        view.habilitarBtnBuscar(true);
        view.habilitarTablas(true);

        int selectedRow = view.getTablaCursos().getSelectedRow();
        if (selectedRow >= 0) {
            view.cargarDetallesCurso();
        } else {
            view.limpiarCampos();
        }
    }

    // Nuscar vurso
    public void buscarCurso() {
        String textoBusqueda = view.getTxtBuscar().getText().trim();

        try {
            List<Object[]> cursosEncontrados = model.buscarCursos(textoBusqueda);

            view.getModeloCursos().setRowCount(0);
            for (Object[] curso : cursosEncontrados) {
                Object[] filaCurso = {curso[0], curso[1], curso[3], curso[4], curso[5]};
                view.getModeloCursos().addRow(filaCurso);
            }

            if (cursosEncontrados.isEmpty() && !textoBusqueda.isEmpty()) {
                JOptionPane.showMessageDialog(view,
                        "No se encontraron cursos que coincidan con: \"" + textoBusqueda + "\"",
                        "Sin resultados",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view,
                    "Error al buscar cursos:\n" + e.getMessage(),
                    "Error de Busqueda",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Inscribir alumno
    public void mostrarFormularioAgregarAlumno() {
        int selectedRowCurso = view.getTablaCursos().getSelectedRow();

        if (selectedRowCurso < 0) {
            JOptionPane.showMessageDialog(view,
                    "Por favor, seleccione un curso primero",
                    "Curso no seleccionado",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String codigoCurso = view.getModeloCursos().getValueAt(selectedRowCurso, 0).toString();
        String nombreCurso = view.getModeloCursos().getValueAt(selectedRowCurso, 1).toString();

        // Obtener los modulos especificos del curso EN EL ORDEN CORRECTO
        List<String> modulosDelCurso = obtenerModulosEspecificosDelCurso(codigoCurso);

        if (modulosDelCurso.isEmpty()) {
            JOptionPane.showMessageDialog(view,
                    "Error: El curso seleccionado no tiene modulos definidos.\n"
                    + "No se puede inscribir alumnos a un curso sin modulos.",
                    "Curso sin modulos",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Crear el buscador de alumnos disponibles
        AlumnoFormDialog.AlumnoBuscador buscador = new AlumnoFormDialog.AlumnoBuscador() {
            @Override
            public List<Object[]> buscarAlumnosPorDni(String dni) {
                return model.buscarAlumnosDisponiblesPorCurso(dni, codigoCurso);
            }
        };

        // Crear el dialog 
        AlumnoFormDialog dialog = new AlumnoFormDialog(view, nombreCurso, buscador);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            Object[] alumno = dialog.getDatosAlumno();

            if (alumno != null) {
                // Validar modulos aprobados
                int modulosAprobados = 0;
                try {
                    modulosAprobados = Integer.parseInt((String) alumno[3]);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(view,
                            "Error: La cantidad de modulos aprobados debe ser un numero valido",
                            "Error de Validacion",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Validar que no se aprueben mas modulos de los que tiene el curso
                if (modulosAprobados > modulosDelCurso.size()) {
                    JOptionPane.showMessageDialog(view,
                            "Error: No se pueden aprobar mas modulos (" + modulosAprobados
                            + ") de los que tiene el curso (" + modulosDelCurso.size() + ")",
                            "Error de Validacion",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Mostrar confirmacion con los modulos ESPECiFICOS del curso que se van a aprobar
                if (modulosAprobados > 0) {
                    StringBuilder mensaje = new StringBuilder();
                    mensaje.append("Se inscribira al alumno ").append(dialog.getApellido())
                            .append(", ").append(dialog.getNombre()).append("\n");
                    mensaje.append("DNI: ").append(dialog.getDni()).append("\n");
                    mensaje.append("Curso: ").append(nombreCurso).append("\n\n");

                    mensaje.append("MODULOS QUE SE MARCARaN COMO APROBADOS (").append(modulosAprobados)
                            .append("/").append(modulosDelCurso.size()).append("):\n");

                    for (int i = 0; i < modulosAprobados; i++) {
                        mensaje.append("✓ Modulo ").append(i + 1).append(": ").append(modulosDelCurso.get(i)).append("\n");
                    }

                    if (modulosAprobados < modulosDelCurso.size()) {
                        mensaje.append("\nMODULOS QUE QUEDARaN PENDIENTES:\n");
                        for (int i = modulosAprobados; i < modulosDelCurso.size(); i++) {
                            mensaje.append("○ Modulo ").append(i + 1).append(": ").append(modulosDelCurso.get(i)).append("\n");
                        }
                    }

                    int confirmacion = JOptionPane.showConfirmDialog(view,
                            mensaje.toString(),
                            "Confirmar Inscripcion con Modulos Especificos",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE);

                    if (confirmacion != JOptionPane.YES_OPTION) {
                        return; // Usuario cancelo
                    }
                }

                // Agregar al modelo
                boolean exito = model.agregarAlumno(codigoCurso, alumno);

                if (exito) {
                    // Actualizar la tabla de alumnos
                    actualizarTablaAlumnos(codigoCurso, nombreCurso);

                    StringBuilder mensajeExito = new StringBuilder();
                    mensajeExito.append("Alumno inscripto exitosamente!\n\n")
                            .append("Alumno: ").append(dialog.getApellido()).append(", ").append(dialog.getNombre()).append("\n")
                            .append("DNI: ").append(dialog.getDni()).append("\n")
                            .append("Curso: ").append(nombreCurso).append("\n\n");

                    if (modulosAprobados > 0) {
                        mensajeExito.append("Modulos marcados como APROBADOS:\n");
                        for (int i = 0; i < modulosAprobados; i++) {
                            mensajeExito.append("✓ ").append(modulosDelCurso.get(i)).append("\n");
                        }
                    } else {
                        mensajeExito.append("El alumno inicia sin modulos aprobados");
                    }

                    JOptionPane.showMessageDialog(view,
                            mensajeExito.toString(),
                            "Inscripcion Completada",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(view,
                            "Error: El DNI ya esta inscripto en este curso o no se pudo realizar la inscripcion.\n"
                            + "Verifique que el alumno no este ya inscripto.",
                            "Error de Inscripcion",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(view,
                        "Error: No se pudieron obtener los datos del alumno",
                        "Error de Datos",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Get moudlos de cada curso
    private List<String> obtenerModulosEspecificosDelCurso(String codigoCurso) {
        // Obtener el curso completo con sus modulos
        Object[] cursoCompleto = obtenerCursoCompleto(codigoCurso);

        if (cursoCompleto != null && cursoCompleto.length > 6 && cursoCompleto[6] instanceof List) {
            @SuppressWarnings("unchecked")
            List<String> modulos = (List<String>) cursoCompleto[6];
            return new ArrayList<>(modulos); // Crear una copia
        }

        return new ArrayList<>(); 
    }

    // Update modulos aprobados
    public void editarModulosAlumno() {
        int selectedRowAlumno = view.getTablaAlumnos().getSelectedRow();
        int selectedRowCurso = view.getTablaCursos().getSelectedRow();

        if (selectedRowCurso < 0) {
            JOptionPane.showMessageDialog(view,
                    "Por favor, seleccione un curso primero",
                    "Curso no seleccionado",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (selectedRowAlumno < 0) {
            JOptionPane.showMessageDialog(view,
                    "Por favor, seleccione un alumno para editar sus modulos",
                    "Alumno no seleccionado",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Verificar que no sea la fila de "Sin alumnos inscriptos"
        String dni = view.getModeloAlumnos().getValueAt(selectedRowAlumno, 0).toString();
        if (dni.isEmpty()) {
            JOptionPane.showMessageDialog(view,
                    "No hay alumnos validos para editar en este curso",
                    "Sin alumnos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String codigoCurso = view.getModeloCursos().getValueAt(selectedRowCurso, 0).toString();
        String nombreCurso = view.getModeloCursos().getValueAt(selectedRowCurso, 1).toString();
        String apellido = view.getModeloAlumnos().getValueAt(selectedRowAlumno, 1).toString();
        String nombre = view.getModeloAlumnos().getValueAt(selectedRowAlumno, 2).toString();

        // Extraer el numero de modulos del texto "5/7 (71%)"
        String progresoTexto = view.getModeloAlumnos().getValueAt(selectedRowAlumno, 3).toString();
        int modulosActuales = 0;
        try {
            if (progresoTexto.contains("/")) {
                modulosActuales = Integer.parseInt(progresoTexto.split("/")[0]);
            }
        } catch (NumberFormatException e) {
            // Si no se puede parsear, usar 0....
        }

        // Obtener modulos especificos del curso
        List<String> modulosDelCurso = obtenerModulosEspecificosDelCurso(codigoCurso);

        if (modulosDelCurso.isEmpty()) {
            JOptionPane.showMessageDialog(view,
                    "Error: El curso no tiene modulos definidos",
                    "Curso sin modulos",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Mostrar estado actual y pedir nueva cantidad
        StringBuilder mensajeActual = new StringBuilder();
        mensajeActual.append("Alumno: ").append(apellido).append(", ").append(nombre).append("\n");
        mensajeActual.append("DNI: ").append(dni).append("\n");
        mensajeActual.append("Curso: ").append(nombreCurso).append("\n\n");
        mensajeActual.append("ESTADO ACTUAL (").append(modulosActuales).append("/").append(modulosDelCurso.size()).append(" aprobados):\n");

        for (int i = 0; i < modulosDelCurso.size(); i++) {
            String estado = (i < modulosActuales) ? "✓ APROBADO" : "○ Pendiente";
            mensajeActual.append("Modulo ").append(i + 1).append(": ").append(modulosDelCurso.get(i))
                    .append(" - ").append(estado).append("\n");
        }

        mensajeActual.append("\nIngrese la nueva cantidad de modulos aprobados (0-").append(modulosDelCurso.size()).append("):");

        String input = JOptionPane.showInputDialog(view, mensajeActual.toString(),
                "Editar Modulos Aprobados", JOptionPane.QUESTION_MESSAGE);

        if (input != null) {
            try {
                int nuevosModulos = Integer.parseInt(input.trim());

                if (nuevosModulos < 0 || nuevosModulos > modulosDelCurso.size()) {
                    JOptionPane.showMessageDialog(view,
                            "Error: La cantidad debe estar entre 0 y " + modulosDelCurso.size(),
                            "Valor invalido",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Mostrar confirmacion del cambio
                if (nuevosModulos != modulosActuales) {
                    StringBuilder confirmacion = new StringBuilder();
                    confirmacion.append("Se cambiara el estado de los modulos:\n\n");

                    if (nuevosModulos > 0) {
                        confirmacion.append("MODULOS QUE QUEDARaN APROBADOS (").append(nuevosModulos).append("):\n");
                        for (int i = 0; i < nuevosModulos; i++) {
                            confirmacion.append("✓ Modulo ").append(i + 1).append(": ").append(modulosDelCurso.get(i)).append("\n");
                        }
                    }

                    if (nuevosModulos < modulosDelCurso.size()) {
                        confirmacion.append("\nMODULOS QUE QUEDARaN PENDIENTES:\n");
                        for (int i = nuevosModulos; i < modulosDelCurso.size(); i++) {
                            confirmacion.append("○ Modulo ").append(i + 1).append(": ").append(modulosDelCurso.get(i)).append("\n");
                        }
                    }

                    int respuesta = JOptionPane.showConfirmDialog(view,
                            confirmacion.toString(),
                            "Confirmar Cambios",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE);

                    if (respuesta != JOptionPane.YES_OPTION) {
                        return;
                    }
                }

                // Actualizar en la base de datos
                boolean exito = model.actualizarModulosAlumno(codigoCurso, dni, nuevosModulos);

                if (exito) {
                    // Refrescar la tabla
                    actualizarTablaAlumnos(codigoCurso, nombreCurso);

                    StringBuilder resultado = new StringBuilder();
                    resultado.append("¡Modulos actualizados exitosamente!\n\n");
                    resultado.append("Alumno: ").append(apellido).append(", ").append(nombre).append("\n");
                    resultado.append("Modulos aprobados: ").append(nuevosModulos).append("/").append(modulosDelCurso.size());

                    JOptionPane.showMessageDialog(view,
                            resultado.toString(),
                            "Actualizacion Exitosa",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(view,
                            "Error al actualizar los modulos del alumno.\n"
                            + "Verifique la conexion a la base de datos.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
                actualizarEstadoBotonesCertificado();

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(view,
                        "Error: Ingrese un numero valido",
                        "Valor invalido",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Mostrar detalle de alumno (NUEVA FEATURE, tuve que agregarla para desbugear)
    public void mostrarDetallesModulosAlumno() {
        int selectedRowAlumno = view.getTablaAlumnos().getSelectedRow();
        int selectedRowCurso = view.getTablaCursos().getSelectedRow();

        if (selectedRowCurso < 0 || selectedRowAlumno < 0) {
            JOptionPane.showMessageDialog(view,
                    "Por favor, seleccione un curso y un alumno",
                    "Seleccion incompleta",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String dni = view.getModeloAlumnos().getValueAt(selectedRowAlumno, 0).toString();
        if (dni.isEmpty()) {
            return; // No hay alumno valido seleccionado
        }

        String codigoCurso = view.getModeloCursos().getValueAt(selectedRowCurso, 0).toString();
        String nombreCurso = view.getModeloCursos().getValueAt(selectedRowCurso, 1).toString();
        String apellido = view.getModeloAlumnos().getValueAt(selectedRowAlumno, 1).toString();
        String nombre = view.getModeloAlumnos().getValueAt(selectedRowAlumno, 2).toString();

        // Obtener estado de modulos
        List<String> modulosCurso = obtenerModulosEspecificosDelCurso(codigoCurso);
        List<String> modulosAprobados = model.obtenerModulosAprobadosPorAlumno(codigoCurso, dni);

        StringBuilder detalle = new StringBuilder();
        detalle.append("DETALLE DE MODULOS\n\n");
        detalle.append("Alumno: ").append(apellido).append(", ").append(nombre).append("\n");
        detalle.append("DNI: ").append(dni).append("\n");
        detalle.append("Curso: ").append(nombreCurso).append("\n\n");
        detalle.append("Progreso: ").append(modulosAprobados.size()).append("/").append(modulosCurso.size()).append(" modulos aprobados\n\n");

        for (int i = 0; i < modulosCurso.size(); i++) {
            String modulo = modulosCurso.get(i);
            boolean aprobado = i < modulosAprobados.size();
            String estado = aprobado ? "✓ APROBADO" : "○ Pendiente";
            detalle.append("Modulo ").append(i + 1).append(": ").append(modulo).append(" - ").append(estado).append("\n");
        }

        JOptionPane.showMessageDialog(view,
                detalle.toString(),
                "Detalle de Modulos - " + apellido + ", " + nombre,
                JOptionPane.INFORMATION_MESSAGE);
    }


    // Update estado de los botones, se actualiza segun alumno seleccionado
    public void actualizarEstadoBotonesCertificado() {
        int selectedRowAlumno = view.getTablaAlumnos().getSelectedRow();
        int selectedRowCurso = view.getTablaCursos().getSelectedRow();

        // Si no hay seleccion, deshabilitar todos los botones
        if (selectedRowAlumno < 0 || selectedRowCurso < 0) {
            view.getBtnGenerarCertificado().setEnabled(false);
            view.getBtnCompletarCertificado().setEnabled(false);
            view.getBtnGenerarFicha().setEnabled(true); // Ficha siempre disponible
            return;
        }

        // Verificar que no sea la fila de "Sin alumnos inscriptos"
        String dni = view.getModeloAlumnos().getValueAt(selectedRowAlumno, 0).toString();
        if (dni.isEmpty()) {
            view.getBtnGenerarCertificado().setEnabled(false);
            view.getBtnCompletarCertificado().setEnabled(false);
            view.getBtnGenerarFicha().setEnabled(false);
            return;
        }

        // Obtener informacion del progreso del alumno
        String codigoCurso = view.getModeloCursos().getValueAt(selectedRowCurso, 0).toString();
        List<String> modulosDelCurso = obtenerModulosEspecificosDelCurso(codigoCurso);
        List<String> modulosAprobados = model.obtenerModulosAprobadosPorAlumno(codigoCurso, dni);

        // Verificar si el alumno completo el 100% de los modulos
        boolean cursoCompleto = !modulosDelCurso.isEmpty() && modulosAprobados.size() == modulosDelCurso.size();

        // Habilitar/deshabilitar botones segun el progreso
        view.getBtnGenerarCertificado().setEnabled(cursoCompleto);
        view.getBtnCompletarCertificado().setEnabled(cursoCompleto);
        view.getBtnGenerarFicha().setEnabled(true); 

        // Actualizar tooltips para dar informacion al usuario
        if (cursoCompleto) {
            view.getBtnGenerarCertificado().setEnabled(true);
            view.getBtnCompletarCertificado().setEnabled(true);
            view.getBtnGenerarCertificado().setToolTipText("Generar certificado - Alumno con curso completo");
            view.getBtnCompletarCertificado().setToolTipText("Completar certificado fisico - Alumno con curso completo");
        } else {
            view.getBtnGenerarCertificado().setEnabled(false);
            view.getBtnCompletarCertificado().setEnabled(false);
            int totalModulos = modulosDelCurso.size();
            int aprobados = modulosAprobados.size();
            String mensaje = String.format("Certificado no disponible - Progreso: %d/%d modulos (%.0f%%)",
                    aprobados, totalModulos,
                    totalModulos > 0 ? (double) aprobados / totalModulos * 100 : 0);

            view.getBtnGenerarCertificado().setToolTipText(mensaje);
            view.getBtnCompletarCertificado().setToolTipText(mensaje);
        }

        view.getBtnGenerarFicha().setToolTipText("Generar ficha de acreditacion - Disponible independientemente del progreso");
    }

    public void generarCertificado() {
        // Validaciones de seleccion
        int selectedRowAlumno = view.getTablaAlumnos().getSelectedRow();
        int selectedRowCurso = view.getTablaCursos().getSelectedRow();

        if (selectedRowCurso < 0) {
            JOptionPane.showMessageDialog(view,
                    "Por favor, seleccione un curso primero",
                    "Curso no seleccionado",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (selectedRowAlumno < 0) {
            JOptionPane.showMessageDialog(view,
                    "Por favor, seleccione un alumno para generar su certificado",
                    "Alumno no seleccionado",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Verificar que no sea la fila de "Sin alumnos inscriptos"
        String dni = view.getModeloAlumnos().getValueAt(selectedRowAlumno, 0).toString();
        if (dni.isEmpty()) {
            JOptionPane.showMessageDialog(view,
                    "No hay alumnos validos para certificar en este curso",
                    "Sin alumnos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Obtener datos del alumno
            String apellido = view.getModeloAlumnos().getValueAt(selectedRowAlumno, 1).toString();
            String nombre = view.getModeloAlumnos().getValueAt(selectedRowAlumno, 2).toString();
            String nombreCompleto = apellido + ", " + nombre;

            // Obtener datos del curso
            String codigoCurso = view.getModeloCursos().getValueAt(selectedRowCurso, 0).toString();
            String nombreCurso = view.getModeloCursos().getValueAt(selectedRowCurso, 1).toString();

            // Obtener modulos con informacion completa (nombre y horas)
            List<Modulo> modulosInfo = model.obtenerModulosInfoPorCurso(codigoCurso);
            List<String> modulos = new ArrayList<>();

            // Convertir modulos info a lista de strings para el certificado
            for (Modulo modulo : modulosInfo) {
                modulos.add(modulo.getNombre());
            }

            // Calcular horas totales reales sumando las horas de todos los modulos
            int horasTotalesReales = model.obtenerHorasTotalesReales(codigoCurso);
            String horasDuracion = String.valueOf(horasTotalesReales);

            // Llamar al utilitario generador de PDF 
            boolean exito = CertificadoPDFUtil.generarCertificado(
                    nombreCompleto,
                    dni,
                    nombreCurso,
                    horasDuracion,
                    modulos
            );

            if (!exito) {
                JOptionPane.showMessageDialog(view,
                        "La generacion del certificado fue cancelada por el usuario",
                        "Operacion Cancelada",
                        JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(view,
                    "Error al generar el certificado:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Genera un certificado fisico (con texto blanco) - solo si tiene 100%
     * completo
     */
    public void completarCertificadoFisico() {
        // Verificar seleccion
        int selectedRowAlumno = view.getTablaAlumnos().getSelectedRow();
        int selectedRowCurso = view.getTablaCursos().getSelectedRow();

        if (selectedRowCurso < 0) {
            JOptionPane.showMessageDialog(view,
                    "Por favor, seleccione un curso primero",
                    "Curso no seleccionado",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (selectedRowAlumno < 0) {
            JOptionPane.showMessageDialog(view,
                    "Por favor, seleccione un alumno para generar su certificado",
                    "Alumno no seleccionado",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Verificar que no sea la fila de "Sin alumnos inscriptos"
        String dni = view.getModeloAlumnos().getValueAt(selectedRowAlumno, 0).toString();
        if (dni.isEmpty()) {
            JOptionPane.showMessageDialog(view,
                    "No hay alumnos validos para certificar en este curso",
                    "Sin alumnos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Obtener datos del alumno
            String apellido = view.getModeloAlumnos().getValueAt(selectedRowAlumno, 1).toString();
            String nombre = view.getModeloAlumnos().getValueAt(selectedRowAlumno, 2).toString();
            String nombreCompleto = apellido + ", " + nombre;

            // Obtener datos del curso
            String codigoCurso = view.getModeloCursos().getValueAt(selectedRowCurso, 0).toString();
            String nombreCurso = view.getModeloCursos().getValueAt(selectedRowCurso, 1).toString();

            // Obtener modulos con informacion completa (nombre y horas)
            List<Modulo> modulosInfo = model.obtenerModulosInfoPorCurso(codigoCurso);
            List<String> modulos = new ArrayList<>();

            // Convertir modulos info a lista de strings para el certificado
            for (Modulo modulo : modulosInfo) {
                modulos.add(modulo.getNombre());
            }

            // Calcular horas totales reales sumando las horas de todos los modulos
            int horasTotalesReales = model.obtenerHorasTotalesReales(codigoCurso);
            String horasDuracion = String.valueOf(horasTotalesReales);

            // Llamar al utilitario generador de PDF invertido
            boolean exito = CertificadoPDFUtil.generarCertificadoInvertido(
                    nombreCompleto,
                    dni,
                    nombreCurso,
                    horasDuracion,
                    modulos
            );

            if (!exito) {
                JOptionPane.showMessageDialog(view,
                        "La generacion del certificado fue cancelada por el usuario",
                        "Operacion Cancelada",
                        JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(view,
                    "Error al generar el certificado:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // Generar ficha de acreditacion
    public void generarFichaAcreditacion() {
        // Verificar que hay un alumno seleccionado
        int selectedRowAlumno = view.getTablaAlumnos().getSelectedRow();
        int selectedRowCurso = view.getTablaCursos().getSelectedRow();

        if (selectedRowCurso < 0) {
            JOptionPane.showMessageDialog(view,
                    "Por favor, seleccione un curso primero",
                    "Curso no seleccionado",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (selectedRowAlumno < 0) {
            JOptionPane.showMessageDialog(view,
                    "Por favor, seleccione un alumno para generar su constancia de acreditacion",
                    "Alumno no seleccionado",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Verificar que no sea la fila de "Sin alumnos inscriptos"
        String dni = view.getModeloAlumnos().getValueAt(selectedRowAlumno, 0).toString();
        if (dni.isEmpty()) {
            JOptionPane.showMessageDialog(view,
                    "No hay alumnos validos para acreditar en este curso",
                    "Sin alumnos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Obtener datos del alumno
            String apellido = view.getModeloAlumnos().getValueAt(selectedRowAlumno, 1).toString();
            String nombre = view.getModeloAlumnos().getValueAt(selectedRowAlumno, 2).toString();
            String nombreCompleto = apellido + ", " + nombre;

            // Obtener datos del curso
            String codigoCurso = view.getModeloCursos().getValueAt(selectedRowCurso, 0).toString();
            String nombreCurso = view.getModeloCursos().getValueAt(selectedRowCurso, 1).toString();

            // Obtener modulos con informacion completa 
            List<String> modulosAprobadosNombres = model.obtenerModulosAprobadosPorAlumno(codigoCurso, dni);
            List<Modulo> todosLosModulosInfo = model.obtenerModulosInfoPorCurso(codigoCurso);
            List<ConstanciaAcreditacionPDFUtil.ModuloInfo> modulos = new ArrayList<>();

            // Solo incluir los modulos que el alumno tiene aprobados
            for (int i = 0; i < modulosAprobadosNombres.size() && i < todosLosModulosInfo.size(); i++) {
                Modulo modulo = todosLosModulosInfo.get(i);
                modulos.add(new ConstanciaAcreditacionPDFUtil.ModuloInfo(
                        modulo.getNombre(),
                        modulo.getHoras()
                ));
            }

            // Calcular horas totales reales sumando las horas de todos los modulos
            int horasAprobadas = 0;
            for (ConstanciaAcreditacionPDFUtil.ModuloInfo modulo : modulos) {
                horasAprobadas += modulo.getHoras();
            }
            String horasDuracion = String.valueOf(horasAprobadas);

            // Llamar al utilitario generador de PDF de constancia de acreditacion
            boolean exito = ConstanciaAcreditacionPDFUtil.generarConstancia(
                    nombreCompleto,
                    dni,
                    nombreCurso,
                    horasDuracion,
                    modulos
            );

            if (!exito) {
                JOptionPane.showMessageDialog(view,
                        "La generacion de la constancia de acreditacion fue cancelada por el usuario",
                        "Operacion Cancelada",
                        JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(view,
                    "Error al generar la constancia de acreditacion:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
