package cfp.pkg402.model;

import cfp.pkg402.dao.PersonaDAO;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GestionPersonasModel {

    private PersonaDAO personaDAO;
    private SimpleDateFormat formatoFecha;

    public GestionPersonasModel() {
        this.personaDAO = new PersonaDAO();
        this.formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
        this.formatoFecha.setLenient(false);
    }

    // obteners todas las personas para mostrar en la tabla
    public List<Object[]> obtenerTodasPersonas() {
        List<Persona> personas = personaDAO.obtenerTodas();
        List<Object[]> resultado = new ArrayList<>();

        for (Persona persona : personas) {
            Object[] fila = new Object[5];
            fila[0] = persona.getDni();
            fila[1] = persona.getApellido();
            fila[2] = persona.getNombre();
            fila[3] = persona.getFechaNacimiento() != null
                    ? formatoFecha.format(persona.getFechaNacimiento()) : "";
            fila[4] = persona.getRol();
            resultado.add(fila);
        }

        return resultado;
    }

    // Busca personas por DNI, nombre o apellido
    public List<Object[]> buscarPersonas(String texto) {
        List<Persona> personas = new ArrayList<>();

        // Buscar por DNI si el texto son numeros
        if (texto.matches("\\d+")) {
            personas.addAll(personaDAO.buscarPorDni(texto));
        }

        // Buscar por nombre/apellido
        personas.addAll(personaDAO.buscarPorNombre(texto));

        // Convertir a Object[] y eliminar duplicados
        List<Object[]> resultado = new ArrayList<>();
        Set<String> dnisAgregados = new HashSet<>();

        for (Persona persona : personas) {
            if (!dnisAgregados.contains(persona.getDni())) {
                Object[] fila = new Object[5];
                fila[0] = persona.getDni();
                fila[1] = persona.getApellido();
                fila[2] = persona.getNombre();
                fila[3] = persona.getFechaNacimiento() != null
                        ? formatoFecha.format(persona.getFechaNacimiento()) : "";
                fila[4] = persona.getRol();
                resultado.add(fila);
                dnisAgregados.add(persona.getDni());
            }
        }

        return resultado;
    }

    // Get por Dni
    public Persona obtenerPersonaPorDni(String dni) {
        return personaDAO.obtenerPorDni(dni);
    }

    // Add persona 
    public boolean agregarPersona(String dni, String apellido, String nombre,
            String fechaNacimiento, String direccion,
            String telefono, String email, String rol) {

        // Verificar si ya existe el DNI
        if (personaDAO.existeDni(dni)) {
            throw new RuntimeException("Ya existe una persona con el DNI: " + dni);
        }

        Persona persona = new Persona();
        persona.setDni(dni);
        persona.setApellido(apellido);
        persona.setNombre(nombre);
        persona.setDireccion(direccion);
        persona.setTelefono(telefono);
        persona.setEmail(email);
        persona.setRol(rol);

        // Convertir fecha si esta presente
        if (fechaNacimiento != null && !fechaNacimiento.trim().isEmpty()) {
            try {
                Date fecha = formatoFecha.parse(fechaNacimiento);
                persona.setFechaNacimiento(fecha);
            } catch (ParseException e) {
                throw new RuntimeException("Error al parsear fecha: " + e.getMessage());
            }
        }

        return personaDAO.insertar(persona);
    }

    // Update persona
    public boolean actualizarPersona(String dniOriginal, String dni, String apellido,
            String nombre, String fechaNacimiento, String direccion,
            String telefono, String email, String rol) {

        Persona persona = personaDAO.obtenerPorDni(dniOriginal);
        if (persona == null) {
            throw new RuntimeException("No se encontro la persona con DNI: " + dniOriginal);
        }

        // Si cambio el DNI, verificar que no exista
        if (!dniOriginal.equals(dni) && personaDAO.existeDni(dni)) {
            throw new RuntimeException("Ya existe una persona con el DNI: " + dni);
        }

        persona.setDni(dni);
        persona.setApellido(apellido);
        persona.setNombre(nombre);
        persona.setDireccion(direccion);
        persona.setTelefono(telefono);
        persona.setEmail(email);
        persona.setRol(rol);

        // Convertir fecha si esta presente
        if (fechaNacimiento != null && !fechaNacimiento.trim().isEmpty()) {
            try {
                Date fecha = formatoFecha.parse(fechaNacimiento);
                persona.setFechaNacimiento(fecha);
            } catch (ParseException e) {
                throw new RuntimeException("Error al parsear fecha: " + e.getMessage());
            }
        } else {
            persona.setFechaNacimiento(null);
        }

        return personaDAO.actualizar(persona);
    }

    // Lista de los profesores
    public List<String> obtenerNombresProfesores() {
        List<Persona> profesores = personaDAO.obtenerProfesores();
        List<String> nombres = new ArrayList<>();

        for (Persona profesor : profesores) {
            nombres.add(profesor.getNombreCompleto());
        }

        return nombres;
    }

    // Valida formato de fecha
    public boolean validarFormatoFecha(String fecha) {
        if (fecha == null || fecha.trim().isEmpty()) {
            return true; 
        }

        try {
            formatoFecha.parse(fecha);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    // Verifico si existe un DNI 
    public boolean existeDni(String dni, String dniOriginal) {
        // Si es el mismo DNI original no hay conflicto
        if (dni.equals(dniOriginal)) {
            return false;
        }
        return personaDAO.existeDni(dni);
    }
}
