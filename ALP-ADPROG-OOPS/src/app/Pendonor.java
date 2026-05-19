import javax.swing.JPanel;

public class Pendonor extends User {
    public Pendonor(String id, String password, String name, String bloodType,
        String city, String phone, String dob) {
        super(id, password, "PENDONOR", name, bloodType, city, phone, dob);
    }

    @Override
    public JPanel createDashboardPanel(AplikasiPMI app) {
        return app.buildInterfacePendonor(this);
    }

    @Override
    public String getBadgeText() {
        return "🩸 AKSES: PENDONOR DARAH INDONESIA";
    }
}
