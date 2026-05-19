import javax.swing.JPanel;

public class Petugas extends Staff {
    public Petugas(String id, String password, String name, String bloodType,
        String city, String phone, String dob) {
        super(id, password, "PETUGAS", name, bloodType, city, phone, dob);
    }

    @Override
    public JPanel createDashboardPanel(AplikasiPMI app) {
        return app.buildInterfacePetugas(this);
    }

    @Override
    public String getBadgeText() {
        return "🩺 OTORITAS: PETUGAS KESEHATAN PMI";
    }
}
