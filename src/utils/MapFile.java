package src.utils;

public class MapFile {
    private String nomFichier;
    private String extension;
    private byte[] fichier;

    public MapFile(){}
    public MapFile(String nomFichier, String extension, byte[] fichier) {
        this.nomFichier = nomFichier;
        this.extension = extension;
        this.fichier = fichier;
    }
    public String getNomFichier() {
        return nomFichier;
    }
    public void setNomFichier(String nomFichier) {
        this.nomFichier = nomFichier;
    }
    public String getExtension() {
        return extension;
    }
    public void setExtension(String extension) {
        this.extension = extension;
    }
    public byte[] getFichier() {
        return fichier;
    }
    public void setFichier(byte[] fichier) {
        this.fichier = fichier;
    }
}
