
public class NodeAntri {
    private String idPendonor;
    private String namaPendonor;
    private String golonganDarah;
    private String statusAntri;  // MENUNGGU, SEDANG_DIPROSES, SELESAI
    private long waktuMasuk;     // Timestamp saat pendonor masuk antrian
    private NodeAntri berikutnya; // Pointer ke node selanjutnya

    public NodeAntri(String idPendonor, String namaPendonor, String golonganDarah) {
        this.idPendonor = idPendonor;
        this.namaPendonor = namaPendonor;
        this.golonganDarah = golonganDarah;
        this.statusAntri = "MENUNGGU";
        this.waktuMasuk = System.currentTimeMillis();
        this.berikutnya = null;
    }

    // Getter dan Setter
    public String getIdPendonor() {
        return idPendonor;
    }

    public String getNamaPendonor() {
        return namaPendonor;
    }

    public String getGolonganDarah() {
        return golonganDarah;
    }

    public String getStatusAntri() {
        return statusAntri;
    }

    public void setStatusAntri(String statusAntri) {
        this.statusAntri = statusAntri;
    }

    public long getWaktuMasuk() {
        return waktuMasuk;
    }

    public NodeAntri getBerikutnya() {
        return berikutnya;
    }

    public void setBerikutnya(NodeAntri berikutnya) {
        this.berikutnya = berikutnya;
    }

    public String formatWaktuMasuk() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm:ss");
        return sdf.format(new java.util.Date(waktuMasuk));
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s) - Golongan: %s - Status: %s - Masuk: %s",
                idPendonor, namaPendonor, getUrutanAntri(), golonganDarah, statusAntri, formatWaktuMasuk());
    }

    public String getUrutanAntri() {
        return "Antrian";
    }
}
