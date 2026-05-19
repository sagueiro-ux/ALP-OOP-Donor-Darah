import javax.swing.JPanel;

public abstract class User {
    protected final String id;
    protected final String password;
    protected final String role;
    protected final String name;
    protected final String bloodType;
    protected final String city;
    protected final String phone;
    protected final String dob;

    public User(String id, String password, String role, String name,
                String bloodType, String city, String phone, String dob) {
        this.id = id;
        this.password = password;
        this.role = role;
        this.name = name;
        this.bloodType = bloodType;
        this.city = city;
        this.phone = phone;
        this.dob = dob;
    }

    public String getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public String getName() {
        return name;
    }

    public String getBloodType() {
        return bloodType;
    }

    public String getCity() {
        return city;
    }

    public String getPhone() {
        return phone;
    }

    public String getDob() {
        return dob;
    }

    public abstract JPanel createDashboardPanel(AplikasiPMI app);

    public abstract String getBadgeText();
}
