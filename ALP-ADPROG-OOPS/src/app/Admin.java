import javax.swing.JPanel;

public class Admin extends Staff {
    public Admin(String id, String password, String name, String bloodType,
                 String city, String phone, String dob) {
        super(id, password, "SUPER_ADMIN", name, bloodType, city, phone, dob);
    }

    @Override
    public JPanel createDashboardPanel(AplikasiPMI app) {
        return app.buildInterfaceAdmin(this);
    }

    @Override
    public String getBadgeText() {
        return "🛡️ OTORITAS: SUPER ADMIN (KENDALI AKSES MUTLAK)";
    }
}
