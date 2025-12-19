package model;

public class TBenefit {
    private String username;
    private int skor;
    private int peluruMeleset;
    private int sisaPeluru;

    public TBenefit(String username, int skor, int peluruMeleset, int sisaPeluru) {
        this.username = username;
        this.skor = skor;
        this.peluruMeleset = peluruMeleset;
        this.sisaPeluru = sisaPeluru;
    }

    // Getter dan Setter
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public int getSkor() { return skor; }
    public void setSkor(int skor) { this.skor = skor; }

    public int getPeluruMeleset() { return peluruMeleset; }
    public void setPeluruMeleset(int peluruMeleset) { this.peluruMeleset = peluruMeleset; }

    public int getSisaPeluru() { return sisaPeluru; }
    public void setSisaPeluru(int sisaPeluru) { this.sisaPeluru = sisaPeluru; }
}