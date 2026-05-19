import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AplikasiPMI app = new AplikasiPMI();
            app.setVisible(true);
        });
    }
}
