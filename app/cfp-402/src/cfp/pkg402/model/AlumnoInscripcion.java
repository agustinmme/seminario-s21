package cfp.pkg402.model;

public class AlumnoInscripcion {

    private String dni;
    private String apellido;
    private String nombre;
    private int modulosAprobados;
    private String estado;
    private boolean certificadoGenerado;

    public AlumnoInscripcion() {
    }

    public AlumnoInscripcion(String dni, String apellido, String nombre, int modulosAprobados) {
        this.dni = dni;
        this.apellido = apellido;
        this.nombre = nombre;
        this.modulosAprobados = modulosAprobados;
    }

    // Getters y Setters
    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getModulosAprobados() {
        return modulosAprobados;
    }

    public void setModulosAprobados(int modulosAprobados) {
        this.modulosAprobados = modulosAprobados;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public boolean isCertificadoGenerado() {
        return certificadoGenerado;
    }

    public void setCertificadoGenerado(boolean certificadoGenerado) {
        this.certificadoGenerado = certificadoGenerado;
    }

    public String getNombreCompleto() {
        return apellido + ", " + nombre;
    }
}
