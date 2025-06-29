package cfp.pkg402.model;

import cfp.pkg402.dao.CursoDAO;
import cfp.pkg402.dao.PersonaDAO;
import cfp.pkg402.model.Modulo;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class GestionCursosModel {

    private CursoDAO cursoDAO;
    private PersonaDAO personaDAO;
    private SimpleDateFormat formatoFecha;

    public GestionCursosModel() {
        this.cursoDAO = new CursoDAO();
        this.personaDAO = new PersonaDAO();
        this.formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
        this.formatoFecha.setLenient(false);
    }

    // Add curso
    public boolean agregarCurso(Object[] cursoData) {
        try {
            // Validar que tenga modulos
            if (cursoData.length < 7 || !(cursoData[6] instanceof List)) {
                return false;
            }
            
            @SuppressWarnings("unchecked")
            List<String> modulos = (List<String>) cursoData[6];
            
            if (modulos.isEmpty()) {
                return false;
            }
            
            // Buscar el profesor por nombre
            List<Persona> profesores = personaDAO.obtenerProfesores();
            int profesorId = -1;
            String nombreProfesor = (String) cursoData[5];
            
            for (Persona profesor : profesores) {
                if (profesor.getNombreCompleto().equals(nombreProfesor)) {
                    profesorId = profesor.getId();
                    break;
                }
            }
            
            if (profesorId == -1) {
                return false;
            }
            
            // Crear el objeto Curso
            Curso curso = new Curso();
            curso.setCodigo((String) cursoData[0]);
            curso.setNombre((String) cursoData[1]);
            curso.setDescripcion((String) cursoData[2]);
            curso.setFechaInicio(formatoFecha.parse((String) cursoData[3]));
            curso.setFechaFin(formatoFecha.parse((String) cursoData[4]));
            curso.setProfesorId(profesorId);
            
            return cursoDAO.insertar(curso, modulos);
            
        } catch (ParseException e) {
            System.err.println("Error al parsear fechas: " + e.getMessage());
            return false;
        }
    }
    
    // Update curso
    public boolean actualizarCurso(String codigo, Object[] cursoData) {
        try {
            // Validar que tenga modulos
            if (cursoData.length < 7 || !(cursoData[6] instanceof List)) {
                return false;
            }
            
            @SuppressWarnings("unchecked")
            List<String> modulos = (List<String>) cursoData[6];
            
            if (modulos.isEmpty()) {
                return false;
            }
            
            // Obtener el curso existente
            Curso cursoExistente = cursoDAO.obtenerPorCodigo(codigo);
            if (cursoExistente == null) {
                return false;
            }
            
            // Buscar el profesor por nombre
            List<Persona> profesores = personaDAO.obtenerProfesores();
            int profesorId = -1;
            String nombreProfesor = (String) cursoData[5];
            
            for (Persona profesor : profesores) {
                if (profesor.getNombreCompleto().equals(nombreProfesor)) {
                    profesorId = profesor.getId();
                    break;
                }
            }
            
            if (profesorId == -1) {
                return false;
            }
            
            // Actualizar los datos del curso
            cursoExistente.setCodigo((String) cursoData[0]);
            cursoExistente.setNombre((String) cursoData[1]);
            cursoExistente.setDescripcion((String) cursoData[2]);
            cursoExistente.setFechaInicio(formatoFecha.parse((String) cursoData[3]));
            cursoExistente.setFechaFin(formatoFecha.parse((String) cursoData[4]));
            cursoExistente.setProfesorId(profesorId);
            
            return cursoDAO.actualizar(cursoExistente, modulos);
            
        } catch (ParseException e) {
            System.err.println("Error al parsear fechas: " + e.getMessage());
            return false;
        }
    }

    // Valida el formato de fecha
    public boolean validarFormatoFecha(String fecha) {
        if (fecha == null || fecha.length() != 10) {
            return false;
        }
        
        try {
            formatoFecha.parse(fecha);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    // Update modulos del cursos
    public boolean actualizarModulosCurso(String codigoCurso, List<String> nuevosModulos) {
        if (nuevosModulos == null || nuevosModulos.isEmpty()) {
            return false;
        }
        
        Curso curso = cursoDAO.obtenerPorCodigo(codigoCurso);
        if (curso == null) {
            return false;
        }
        
        return cursoDAO.actualizar(curso, nuevosModulos);
    }

    // Get de todos los cursos para mostrar en la tabla
    public List<Object[]> obtenerTodosCursos() {
        List<Curso> cursos = cursoDAO.obtenerTodos();
        List<Object[]> resultado = new ArrayList<>();

        for (Curso curso : cursos) {
            Object[] fila = new Object[7];
            fila[0] = curso.getCodigo();
            fila[1] = curso.getNombre();
            fila[2] = curso.getDescripcion();
            fila[3] = formatoFecha.format(curso.getFechaInicio());
            fila[4] = formatoFecha.format(curso.getFechaFin());
            fila[5] = curso.getNombreProfesor();
            fila[6] = curso.getModulos();
            resultado.add(fila);
        }

        return resultado;
    }

    // Get de curso completo por codigo
    public Object[] obtenerCursoPorCodigo(String codigo) {
        Curso curso = cursoDAO.obtenerPorCodigo(codigo);
        if (curso != null) {
            Object[] resultado = new Object[7];
            resultado[0] = curso.getCodigo();
            resultado[1] = curso.getNombre();
            resultado[2] = curso.getDescripcion();
            resultado[3] = formatoFecha.format(curso.getFechaInicio());
            resultado[4] = formatoFecha.format(curso.getFechaFin());
            resultado[5] = curso.getNombreProfesor();
            resultado[6] = curso.getModulos();
            return resultado;
        }
        return null;
    }

    // Busca cursos segun el termino ingresado
    public List<Object[]> buscarCursos(String termino) {
        List<Curso> cursos = cursoDAO.buscarCursos(termino);
        List<Object[]> resultado = new ArrayList<>();

        for (Curso curso : cursos) {
            Object[] fila = new Object[7];
            fila[0] = curso.getCodigo();
            fila[1] = curso.getNombre();
            fila[2] = curso.getDescripcion();
            fila[3] = formatoFecha.format(curso.getFechaInicio());
            fila[4] = formatoFecha.format(curso.getFechaFin());
            fila[5] = curso.getNombreProfesor();
            fila[6] = curso.getModulos();
            resultado.add(fila);
        }

        return resultado;
    }

    // Get de alumnos por curso
    public List<Object[]> obtenerAlumnosPorCurso(String codigoCurso) {
        List<AlumnoInscripcion> alumnos = cursoDAO.obtenerAlumnosPorCurso(codigoCurso);
        List<Object[]> resultado = new ArrayList<>();

        for (AlumnoInscripcion alumno : alumnos) {
            Object[] fila = new Object[4];
            fila[0] = alumno.getDni();
            fila[1] = alumno.getApellido();
            fila[2] = alumno.getNombre();
            fila[3] = String.valueOf(alumno.getModulosAprobados());
            resultado.add(fila);
        }

        return resultado;
    }

    // Get de los modulos especificos de un curso 
    public List<String> obtenerModulosEspecificosDelCurso(String codigoCurso) {
        Object[] cursoCompleto = obtenerCursoPorCodigo(codigoCurso);

        if (cursoCompleto != null && cursoCompleto.length > 6 && cursoCompleto[6] instanceof List) {
            @SuppressWarnings("unchecked")
            List<String> modulos = (List<String>) cursoCompleto[6];
            return new ArrayList<>(modulos); // Creo una copia para evitar modificaciones
        }

        return new ArrayList<>(); // Lista vacia si no hay modulos
    }


    // Valida que la cantidad de modulos aprobados sea correcta 
    public boolean validarModulosAprobados(String codigoCurso, int modulosAprobados) {
        if (modulosAprobados < 0) {
            return false;
        }

        List<String> modulosCurso = obtenerModulosEspecificosDelCurso(codigoCurso);
        return modulosAprobados <= modulosCurso.size();
    }

    // Buscar alumnos disponibles para inscribir en un curso
    public List<Object[]> buscarAlumnosDisponiblesPorCurso(String dni, String codigoCurso) {
        List<AlumnoInscripcion> alumnos = cursoDAO.buscarAlumnosDisponibles(dni, codigoCurso);
        List<Object[]> resultado = new ArrayList<>();

        for (AlumnoInscripcion alumno : alumnos) {
            Object[] fila = new Object[4];
            fila[0] = alumno.getDni();
            fila[1] = alumno.getApellido();
            fila[2] = alumno.getNombre();
            fila[3] = String.valueOf(alumno.getModulosAprobados());
            resultado.add(fila);
        }

        return resultado;
    }

    // Add un alumno validando especificamente los modulos del curso
    public boolean agregarAlumno(String codigoCurso, Object[] alumno) {
        String dniAlumno = (String) alumno[0];

        // Verificar que el alumno no este ya inscripto
        List<Object[]> alumnosEnCurso = obtenerAlumnosPorCurso(codigoCurso);
        for (Object[] alumnoExistente : alumnosEnCurso) {
            if (alumnoExistente[0].equals(dniAlumno)) {
                return false; // Ya existe
            }
        }

        // Validar modulos aprobados
        int modulosAprobados = 0;
        try {
            modulosAprobados = Integer.parseInt((String) alumno[3]);
        } catch (NumberFormatException e) {
            return false; 
        }

        // Verificar que no se aprueben mas modulos de los que tiene el curso
        if (!validarModulosAprobados(codigoCurso, modulosAprobados)) {
            return false;
        }

        return cursoDAO.inscribirAlumno(codigoCurso, dniAlumno, modulosAprobados);
    }

    // Actualiza los modulos aprobados de un alumno especifico
    public boolean actualizarModulosAlumno(String codigoCurso, String dniAlumno, int nuevosModulosAprobados) {
        // Validar la cantidad de modulos
        if (!validarModulosAprobados(codigoCurso, nuevosModulosAprobados)) {
            return false;
        }

        return cursoDAO.actualizarModulosAlumno(codigoCurso, dniAlumno, nuevosModulosAprobados);
    }

    // Get de los modulos especificos aprobados por un alumno
    public List<String> obtenerModulosAprobadosPorAlumno(String codigoCurso, String dniAlumno) {
        return cursoDAO.obtenerModulosAprobadosPorAlumno(codigoCurso, dniAlumno);
    }

    // Get resumen del progreso de un alumno en un curso
    public String obtenerResumenProgresoAlumno(String codigoCurso, String dniAlumno) {
        List<String> modulosCurso = obtenerModulosEspecificosDelCurso(codigoCurso);
        List<String> modulosAprobados = obtenerModulosAprobadosPorAlumno(codigoCurso, dniAlumno);

        int totalModulos = modulosCurso.size();
        int aprobados = modulosAprobados.size();

        if (totalModulos == 0) {
            return "Sin modulos definidos";
        }

        double porcentaje = (double) aprobados / totalModulos * 100;
        return String.format("%d/%d modulos aprobados (%.1f%%)", aprobados, totalModulos, porcentaje);
    }

    // Valida que la inscripcion de un alumno sea correcta
    public boolean validarInscripcionAlumno(String codigoCurso, String dniAlumno, int modulosAprobados) {
        // Obtener modulos del curso
        List<String> modulosCurso = obtenerModulosEspecificosDelCurso(codigoCurso);

        // Validar que no se aprueben mas modulos de los que tiene el curso
        if (modulosAprobados > modulosCurso.size()) {
            return false;
        }

        // Validar que el alumno no este ya inscripto
        List<Object[]> alumnosEnCurso = obtenerAlumnosPorCurso(codigoCurso);
        for (Object[] alumnoExistente : alumnosEnCurso) {
            if (alumnoExistente[0].equals(dniAlumno)) {
                return false; // Ya existe
            }
        }

        return true;
    }

    // Nuevo codigo de curso
    public String generarNuevoCodigo() {
        return cursoDAO.generarNuevoCodigo();
    }

    // Get de informacion completa de modulos por curso
    public List<Modulo> obtenerModulosInfoPorCurso(String codigoCurso) {
        Curso curso = cursoDAO.obtenerPorCodigo(codigoCurso);
        if (curso != null) {
            return cursoDAO.obtenerModulosInfoPorCurso(curso.getId());
        }
        return new ArrayList<>();
    }

    // Get horas totales reales de un curso
    public int obtenerHorasTotalesReales(String codigoCurso) {
        Curso curso = cursoDAO.obtenerPorCodigo(codigoCurso);
        if (curso != null) {
            return cursoDAO.obtenerHorasTotalesReales(curso.getId());
        }
        return 120; // Valor por defecto
    }

    // Get lista de profesores para el combo box
    public List<String> obtenerProfesores() {
        List<Persona> profesores = personaDAO.obtenerProfesores();
        List<String> nombresProfesores = new ArrayList<>();

        for (Persona profesor : profesores) {
            nombresProfesores.add(profesor.getNombreCompleto());
        }

        return nombresProfesores;
    }
}
