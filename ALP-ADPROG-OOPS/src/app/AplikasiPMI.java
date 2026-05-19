import java.awt.*;
import java.awt.geom.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.*;

public class AplikasiPMI extends JFrame {

    private final HashMap<String, String> dataPengguna     = new HashMap<>(); 
    private final HashMap<String, String> dataRole         = new HashMap<>(); 
    private final HashMap<String, String> dataNama         = new HashMap<>(); 
    private final HashMap<String, String> dataGolDarah     = new HashMap<>(); 
    private final HashMap<String, String> dataKota         = new HashMap<>(); 
    private final HashMap<String, String> dataTelepon      = new HashMap<>(); 
    private final HashMap<String, String> dataTanggalLahir = new HashMap<>(); 
    private final HashMap<String, Double> dataHb            = new HashMap<>(); 
    private final HashMap<String, String> dataHbStatus      = new HashMap<>(); 

    private static final String FILE_NAME = getDynamicFilePath();
    private static final String DATA_HB_FILE = getDynamicHbFilePath();

    private static String getDynamicFilePath() {
        String userDir = System.getProperty("user.dir");
        return userDir + File.separator + "pmi_database.txt";
    }

    private static final Color MERAH_PMI    = new Color(196, 22, 28);
    private static final Color MERAH_TUA    = new Color(130, 10, 14);
    private static final Color MERAH_MUDA   = new Color(220, 50, 55);
    private static final Color PUTIH        = new Color(255, 255, 255);
    private static final Color ABU_HANGAT   = new Color(110, 85, 80);
    private static final Color GELAP        = new Color(18, 8, 8);
    private static final Color KARTU_BG     = new Color(255, 253, 252);
    private static final Color BORDER_WARNA = new Color(215, 175, 168);
    private static final Color HIJAU        = new Color(22, 120, 55);

    private JPanel cardPanel;
    private CardLayout cardLayout;
    
    private JLabel labelSambutan;
    private JLabel labelRoleBadge;
    private JLabel labelInfoDinamis;
    private JPanel panelKontenKhususRole;

    private static final String[] GOLONGAN_DARAH = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-", "-"};
    private static final String[] KOTA_PMI = {
        "Jakarta Pusat", "Jakarta Selatan", "Jakarta Utara", "Jakarta Barat", "Jakarta Timur",
        "Surabaya", "Bandung", "Medan", "Semarang", "Makassar"
    };

    private User currentUser;
    private String currentUserId = ""; 

    public AplikasiPMI() {
        setTitle("PMI — Sistem Informasi Donor Darah Indonesia (File & CRUD)");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        muatDataDariFile();
        muatDataHbDariFile();

        cardLayout = new CardLayout();
        cardPanel  = new JPanel(cardLayout);
        cardPanel.setBackground(GELAP);

        cardPanel.add(buildPanelLogin(),     "LOGIN");
        cardPanel.add(buildPanelDaftar(),    "DAFTAR");
        cardPanel.add(buildPanelDashboard(), "DASHBOARD");

        setContentPane(cardPanel);
        cardLayout.show(cardPanel, "LOGIN");
    }

    private void muatDataDariFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            prepareDefaultUsers();
            simpanDataKeFile();
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String baris;
            while ((baris = br.readLine()) != null) {
                if (baris.trim().isEmpty()) continue;
                String[] token = baris.split("\\|", -1); 
                if (token.length >= 8) {
                    String id   = token[0];
                    String pass = token[1];
                    String role = token[2];
                    String nama = token[3];
                    String gol  = token[4];
                    String kota = token[5];
                    String telp = token[6];
                    String tgl  = token[7];

                    dataPengguna.put(id, pass);
                    dataRole.put(id, role);
                    dataNama.put(id, nama);
                    dataGolDarah.put(id, gol);
                    dataKota.put(id, kota);
                    dataTelepon.put(id, telp);
                    dataTanggalLahir.put(id, tgl);
                }
            }

            if (dataPengguna.isEmpty()) {
                prepareDefaultUsers();
                simpanDataKeFile();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat database file!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void simpanDataKeFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (String id : dataPengguna.keySet()) {
                String pass = dataPengguna.get(id);
                String role = dataRole.getOrDefault(id, "-");
                String nama = dataNama.getOrDefault(id, "-");
                String gol  = dataGolDarah.getOrDefault(id, "-");
                String kota = dataKota.getOrDefault(id, "-");
                String telp = dataTelepon.getOrDefault(id, "-");
                String tgl  = dataTanggalLahir.getOrDefault(id, "-");

                String baris = String.format("%s|%s|%s|%s|%s|%s|%s|%s", id, pass, role, nama, gol, kota, telp, tgl);
                bw.write(baris);
                bw.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan perubahan ke database file!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static String getDynamicHbFilePath() {
        String userDir = System.getProperty("user.dir");
        return userDir + File.separator + "datahb.txt";
    }

    private void muatDataHbDariFile() {
        File file = new File(DATA_HB_FILE);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String baris;
            while ((baris = br.readLine()) != null) {
                if (baris.trim().isEmpty()) continue;
                String[] token = baris.split("\\|", -1);
                if (token.length >= 3) {
                    String id = token[0];
                    try {
                        double hb = Double.parseDouble(token[1]);
                        String status = token[2];
                        dataHb.put(id, hb);
                        dataHbStatus.put(id, status);
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat file HB data!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void simpanDataHbKeFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(DATA_HB_FILE))) {
            for (String id : dataHb.keySet()) {
                String status = dataHbStatus.getOrDefault(id, "");
                String baris = String.format("%s|%s|%s", id, dataHb.get(id), status);
                bw.write(baris);
                bw.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan perubahan data HB ke file!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String hitungStatusHb(double hb) {
        return hb >= 12.5 && hb <= 17.0 ? "LAYAK" : "TIDAK LAYAK";
    }

    private void simpanHbUntukPendonor(String id, double hb, String status) {
        dataHb.put(id, hb);
        dataHbStatus.put(id, status);
        simpanDataHbKeFile();
    }

    private String getLabelStatusHb(String id) {
        String status = dataHbStatus.getOrDefault(id, "BELUM DIINPUT");
        if ("LAYAK".equals(status)) return "LAYAK";
        if ("TERVERIFIKASI".equals(status)) return "TERVERIFIKASI";
        if ("TIDAK LAYAK".equals(status)) return "TIDAK LAYAK";
        return "BELUM DIINPUT";
    }

    private boolean isLayakUntukVerifikasi(String id) {
        return "LAYAK".equals(dataHbStatus.get(id));
    }

    private void tandaiTerverifikasi(String id) {
        dataHbStatus.put(id, "TERVERIFIKASI");
        simpanDataHbKeFile();
    }

    private String formatListEntryForPendonor(String id) {
        String text = id + " - " + dataNama.getOrDefault(id, "-") + " [" + dataGolDarah.getOrDefault(id, "-") + "]";
        if (dataHb.containsKey(id)) {
            String status = getLabelStatusHb(id);
            text += String.format(" | HB=%.1f | %s", dataHb.get(id), status);
        } else {
            text += " | HB belum diinput";
        }
        return text;
    }

    private void prepareDefaultUsers() {
        if (!dataPengguna.containsKey("ADMIN001")) {
            dataPengguna.put("ADMIN001", enkripsiPassword("admin123"));
            dataRole.put("ADMIN001", "SUPER_ADMIN");
            dataNama.put("ADMIN001", "Admin PMI");
            dataGolDarah.put("ADMIN001", "A+");
            dataKota.put("ADMIN001", "Jakarta Pusat");
            dataTelepon.put("ADMIN001", "081234567890");
            dataTanggalLahir.put("ADMIN001", "01-01-1990");
        }
        if (!dataPengguna.containsKey("PETUGAS001")) {
            dataPengguna.put("PETUGAS001", enkripsiPassword("petugas123"));
            dataRole.put("PETUGAS001", "PETUGAS");
            dataNama.put("PETUGAS001", "Petugas PMI");
            dataGolDarah.put("PETUGAS001", "B+");
            dataKota.put("PETUGAS001", "Jakarta Selatan");
            dataTelepon.put("PETUGAS001", "081234567891");
            dataTanggalLahir.put("PETUGAS001", "02-02-1992");
        }
    }

    private User createUserFromStorage(String id) {
        if (id == null) return null;
        String password = dataPengguna.get(id);
        String role = dataRole.getOrDefault(id, "PENDONOR");
        String nama = dataNama.getOrDefault(id, "User");
        String gol = dataGolDarah.getOrDefault(id, "-");
        String kota = dataKota.getOrDefault(id, "Umum");
        String telepon = dataTelepon.getOrDefault(id, "-");
        String tgl = dataTanggalLahir.getOrDefault(id, "-");

        if ("SUPER_ADMIN".equalsIgnoreCase(role)) {
            return new Admin(id, password, nama, gol, kota, telepon, tgl);
        }
        if ("PETUGAS".equalsIgnoreCase(role)) {
            return new Petugas(id, password, nama, gol, kota, telepon, tgl);
        }
        return new Pendonor(id, password, nama, gol, kota, telepon, tgl);
    }

    private JPanel buildPanelLogin() {
        JPanel root = new PanelLatar();
        root.setLayout(new GridBagLayout());

        JPanel kartu = buatKartu();
        kartu.setLayout(new BoxLayout(kartu, BoxLayout.Y_AXIS));
        kartu.setPreferredSize(new Dimension(480, 440));//620

        kartu.add(Box.createVerticalStrut(20));
        kartu.add(Box.createVerticalStrut(6));
        kartu.add(labelTengah("SIMULASI DONOR DARAH", font("Arial", Font.BOLD, 23), MERAH_PMI));
        kartu.add(labelTengah("Matkul ADPROG & DS Kelompok 6", font("Arial", Font.ITALIC, 14), ABU_HANGAT));
        kartu.add(garisDekoratif());
        kartu.add(Box.createVerticalStrut(15));

        kartu.add(labelTengah("Masuk ke Sistem", font("Arial", Font.BOLD, 20), MERAH_TUA));
        kartu.add(Box.createVerticalStrut(20));

        JTextField idField = fieldStyled();
        JPasswordField passField = passFieldStyled();
        JLabel pesanLabel = labelPesan();

        kartu.add(padded(labelField("NIK / ID Pegawai")));
        kartu.add(padded(idField));
        kartu.add(Box.createVerticalStrut(10));
        kartu.add(padded(labelField("Kata Sandi")));
        kartu.add(padded(passField));
        kartu.add(Box.createVerticalStrut(8));
        kartu.add(padded(pesanLabel));
        kartu.add(Box.createVerticalStrut(15));

        JButton btnMasuk = tombolUtama("Autentikasi Masuk");
        btnMasuk.addActionListener(e -> {
            String idInput = idField.getText().trim();
            String pass    = new String(passField.getPassword());

            if (idInput.isEmpty() || pass.isEmpty()) {
                tampilPesan(pesanLabel, "Harap isi ID dan kata sandi.", false); return;
            }

            String hashTersimpan = dataPengguna.get(idInput);
            if (hashTersimpan != null && hashTersimpan.equals(enkripsiPassword(pass))) {
                tampilPesan(pesanLabel, "Autentikasi Berhasil! Memuat Data...", true);
                currentUserId = idInput;
                currentUser = createUserFromStorage(idInput);
                Timer t = new Timer(600, ev -> {
                    idField.setText(""); passField.setText(""); pesanLabel.setText(" ");
                    rakitDashboardSesuaiRole(currentUser);
                    cardLayout.show(cardPanel, "DASHBOARD");
                });
                t.setRepeats(false); t.start();
            } else {
                tampilPesan(pesanLabel, "ID atau kata sandi salah.", false);
                goyangKartu(kartu);
            }
        });

            
        kartu.add(padded(btnMasuk));
        kartu.add(Box.createVerticalStrut(12));
        kartu.add(pemisah());
        kartu.add(Box.createVerticalStrut(10));

        JButton btnDaftar = tombolLink("Pendonor Baru? Daftar Akun PMI");
        btnDaftar.addActionListener(e -> cardLayout.show(cardPanel, "DAFTAR"));
        kartu.add(padded(btnDaftar));

        root.add(kartu);
        return root;
    }

    private JPanel buildPanelDaftar() {
        JPanel root = new PanelLatar();
        root.setLayout(new GridBagLayout());

        JPanel kartu = buatKartu();
        kartu.setLayout(new BoxLayout(kartu, BoxLayout.Y_AXIS));
        kartu.setPreferredSize(new Dimension(500, 720));

        kartu.add(Box.createVerticalStrut(15));
        kartu.add(labelTengah("REGISTRASI DONOR DARAH BARU", font("Arial", Font.BOLD, 14), MERAH_PMI));
        kartu.add(garisDekoratif());
        kartu.add(Box.createVerticalStrut(10));

        JTextField namaField     = fieldStyled();
        JTextField nikField      = fieldStyled();
        JTextField tglLahirField = fieldStyled();
        JTextField teleponField  = fieldStyled();
        JPasswordField passField = passFieldStyled();
        JLabel pesanLabel        = labelPesan();

        JLabel labelWarningNIK = new JLabel(" ");
        labelWarningNIK.setFont(font("Arial", Font.ITALIC, 11));
        labelWarningNIK.setForeground(new Color(200, 50, 0));

        nikField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void cekNIK() {
                int len = nikField.getText().trim().length();
                if (len == 0) {
                    labelWarningNIK.setText(" ");
                } else if (len < 16) {
                    labelWarningNIK.setForeground(new Color(200, 50, 0));
                    labelWarningNIK.setText("NIK kurang — saat ini " + len + " digit (harus 16 digit)");
                } else if (len > 16) {
                    labelWarningNIK.setForeground(new Color(200, 50, 0));
                    labelWarningNIK.setText("NIK melebihi batas — saat ini " + len + " digit (harus 16 digit)");
                } else {
                    labelWarningNIK.setForeground(new Color(22, 120, 55));
                    labelWarningNIK.setText("NIK valid (16 digit)");
                }
            }
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { cekNIK(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { cekNIK(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { cekNIK(); }
        });

        JComboBox<String> cbGolDarah = new JComboBox<>(GOLONGAN_DARAH); styleComboBox(cbGolDarah);
        JComboBox<String> cbKota = new JComboBox<>(KOTA_PMI); styleComboBox(cbKota);

        kartu.add(padded(labelField("Nama Lengkap Sesuai KTP"))); kartu.add(padded(namaField));
        kartu.add(padded(labelField("NIK (16 Digit)")));
        kartu.add(padded(nikField));
        kartu.add(padded(labelWarningNIK));
        kartu.add(padded(labelField("Tanggal Lahir (DD-MM-YYYY)")));kartu.add(padded(tglLahirField));
        kartu.add(padded(labelField("Nomor WA/Telepon"))); kartu.add(padded(teleponField));
        kartu.add(padded(labelField("Golongan Darah"))); kartu.add(padded(cbGolDarah));
        kartu.add(padded(labelField("Cabang Kota Terdekat"))); kartu.add(padded(cbKota));
        kartu.add(padded(labelField("Kata Sandi Mandiri"))); kartu.add(padded(passField));
        kartu.add(padded(pesanLabel));
        kartu.add(Box.createVerticalStrut(10));

        JButton btnDaftar = tombolUtama("Daftar Anggota Donor");
        btnDaftar.addActionListener(e -> {
            String nama  = namaField.getText().trim();
            String nik   = nikField.getText().trim();
            String tgl   = tglLahirField.getText().trim();
            String telp  = teleponField.getText().trim();
            String pass  = new String(passField.getPassword());

            if (nama.isEmpty() || nik.isEmpty() || tgl.isEmpty() || telp.isEmpty() || pass.isEmpty()) {
                tampilPesan(pesanLabel, "Seluruh field wajib diisi, tidak boleh ada yang kosong.", false);
                return;
            }

            if (nik.length() != 16) {
                tampilPesan(pesanLabel, "NIK harus tepat 16 digit! Saat ini: " + nik.length() + " digit.", false);
                nikField.requestFocus();
                return;
            }

            if (!nik.matches("\\d{16}")) {
                tampilPesan(pesanLabel, "NIK harus terdiri dari 16 angka saja (0-9).", false);
                nikField.requestFocus();
                return;
            }

            if (dataPengguna.containsKey(nik)) {
                JOptionPane.showMessageDialog(this, 
                    "🚨 Gagal Registrasi!\nNIK / ID [" + nik + "] sudah terdaftar di sistem.\nData tidak bisa ditimpa (No Override).", 
                    "Peringatan Duplikasi", JOptionPane.WARNING_MESSAGE);
                tampilPesan(pesanLabel, "NIK/ID sudah digunakan!", false);
                return;
            }

            dataPengguna.put(nik, enkripsiPassword(pass));
            dataRole.put(nik, "PENDONOR");
            dataNama.put(nik, nama);
            dataGolDarah.put(nik, (String) cbGolDarah.getSelectedItem());
            dataKota.put(nik, (String) cbKota.getSelectedItem());
            dataTelepon.put(nik, telp);
            dataTanggalLahir.put(nik, tgl);

            simpanDataKeFile();

            tampilPesan(pesanLabel, "Registrasi Sukses! Silakan Masuk.", true);
            Timer t = new Timer(1000, ev -> {
                namaField.setText(""); nikField.setText(""); tglLahirField.setText("");
                teleponField.setText(""); passField.setText("");
                cardLayout.show(cardPanel, "LOGIN");
            });
            t.setRepeats(false); t.start();
        });
        kartu.add(padded(btnDaftar));

        kartu.add(Box.createVerticalStrut(10));
        JButton btnKembali = tombolLink("Kembali ke Gerbang Login");
        btnKembali.addActionListener(e -> cardLayout.show(cardPanel, "LOGIN"));
        kartu.add(padded(btnKembali));

        JScrollPane scroll = new JScrollPane(kartu);
        scroll.setOpaque(false); scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);

        root.add(scroll);
        return root;
    }

    private JPanel buildPanelDashboard() {
        JPanel root = new PanelLatar();
        root.setLayout(new BorderLayout());

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(MERAH_TUA);
        topBar.setBorder(new EmptyBorder(12, 24, 12, 24));

        JLabel logoBar = new JLabel("SIMULASI DONOR DARAH MATKUL ADPROG & DS - KELOMPOK 6");
        logoBar.setFont(font("Arial", Font.BOLD, 16));
        logoBar.setForeground(PUTIH);
        topBar.add(logoBar, BorderLayout.WEST);

        JButton btnKeluar = new JButton("Log Out");
        btnKeluar.setFont(font("Arial", Font.BOLD, 12));
        btnKeluar.setForeground(ABU_HANGAT);
        btnKeluar.setBackground(Color.WHITE);
        btnKeluar.setBorder(new EmptyBorder(6, 16, 6, 16));
        btnKeluar.addActionListener(e -> {
            currentUser = null;
            currentUserId = "";
            cardLayout.show(cardPanel, "LOGIN");
        });
        topBar.add(btnKeluar, BorderLayout.EAST);
        root.add(topBar, BorderLayout.NORTH);

        JPanel tengah = new JPanel(new GridBagLayout());
        tengah.setOpaque(false);

        JPanel kartu = buatKartu();
        kartu.setLayout(new BoxLayout(kartu, BoxLayout.Y_AXIS));
        kartu.setPreferredSize(new Dimension(800, 580));

        kartu.add(Box.createVerticalStrut(20));
        labelSambutan = new JLabel("Memuat Profil...", SwingConstants.CENTER);
        labelSambutan.setFont(font("Arial", Font.BOLD, 22));
        labelSambutan.setForeground(MERAH_PMI);
        labelSambutan.setAlignmentX(Component.CENTER_ALIGNMENT);
        kartu.add(labelSambutan);

        labelRoleBadge = new JLabel("[ ROLE ]", SwingConstants.CENTER);
        labelRoleBadge.setFont(font("Arial", Font.BOLD, 13));
        labelRoleBadge.setForeground(HIJAU);
        labelRoleBadge.setAlignmentX(Component.CENTER_ALIGNMENT);
        kartu.add(labelRoleBadge);

        labelInfoDinamis = new JLabel(" ", SwingConstants.CENTER);
        labelInfoDinamis.setFont(font("Arial", Font.PLAIN, 12));
        labelInfoDinamis.setForeground(ABU_HANGAT);
        labelInfoDinamis.setAlignmentX(Component.CENTER_ALIGNMENT);
        kartu.add(labelInfoDinamis);

        kartu.add(Box.createVerticalStrut(10));
        kartu.add(garisDekoratif());
        kartu.add(Box.createVerticalStrut(15));

        panelKontenKhususRole = new JPanel();
        panelKontenKhususRole.setOpaque(false);
        panelKontenKhususRole.setLayout(new BorderLayout());
        kartu.add(panelKontenKhususRole);

        tengah.add(kartu);
        root.add(tengah, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setBackground(new Color(25, 10, 10));
        JLabel lblFooter = new JLabel("Sistem Basis Data Berkas Tanpa Enkripsi PMI © 2026");
        lblFooter.setFont(font("Arial", Font.PLAIN, 11));
        lblFooter.setForeground(new Color(160, 130, 125));
        footer.add(lblFooter);
        root.add(footer, BorderLayout.SOUTH);

        return root;
    }

    private void rakitDashboardSesuaiRole(User user) {
        String nama = user.getName();
        String kota = user.getCity();

        labelSambutan.setText("Selamat Datang, " + nama);
        labelInfoDinamis.setText("Wilayah/Cabang: " + kota + " | Kontak: " + user.getPhone());

        panelKontenKhususRole.removeAll();
        labelRoleBadge.setText(user.getBadgeText());

        if (user instanceof Admin) {
            labelRoleBadge.setForeground(Color.BLUE);
        } else if (user instanceof Petugas) {
            labelRoleBadge.setForeground(HIJAU);
        } else {
            labelRoleBadge.setForeground(MERAH_PMI);
            labelInfoDinamis.setText("Golongan Darah: " + user.getBloodType() + " | " + labelInfoDinamis.getText());
        }

        panelKontenKhususRole.add(user.createDashboardPanel(this));
        panelKontenKhususRole.revalidate();
        panelKontenKhususRole.repaint();
    }

    JPanel buildInterfaceAdmin(Admin admin) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);

        JLabel judulGrid = new JLabel("Basis Data Berkas Terdaftar dalam Berkas (Total: " + dataPengguna.size() + ")", SwingConstants.CENTER);
        judulGrid.setFont(font("Arial", Font.BOLD, 13));
        judulGrid.setForeground(MERAH_TUA);
        panel.add(judulGrid, BorderLayout.NORTH);

        String[] kolom = {"ID / NIK Pengguna", "Nama Lengkap", "Peran (Role)", "Domisili"};
        Object[][] dataTabel = new Object[dataPengguna.size()][4];
        
        int barisIdx = 0;
        for (Map.Entry<String, String> entri : dataPengguna.entrySet()) {
            String userId = entri.getKey();
            dataTabel[barisIdx][0] = userId;
            dataTabel[barisIdx][1] = dataNama.getOrDefault(userId, "-");
            dataTabel[barisIdx][2] = dataRole.getOrDefault(userId, "-");
            dataTabel[barisIdx][3] = dataKota.getOrDefault(userId, "-");
            barisIdx++;
        }

        JTable tabelUser = new JTable(dataTabel, kolom);
        JScrollPane jsp = new JScrollPane(tabelUser);
        jsp.setPreferredSize(new Dimension(650, 180));
        panel.add(jsp, BorderLayout.CENTER);

        JPanel panelAksi = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        panelAksi.setOpaque(false);
        
        JButton btnTambahUser = new JButton("Tambah/Daftar Akun");
        btnTambahUser.addActionListener(e -> {
            JTextField txtId = new JTextField();
            JTextField txtNama = new JTextField();
            JComboBox<String> cbRole = new JComboBox<>(new String[]{"PETUGAS", "PENDONOR"});
            JComboBox<String> cbKota = new JComboBox<>(KOTA_PMI);
            JComboBox<String> cbGol = new JComboBox<>(GOLONGAN_DARAH);
            
            JRadioButton rbDefault = new JRadioButton("Gunakan Sandi Default (12345678)", true);
            JRadioButton rbKustom = new JRadioButton("Tentukan Sendiri:");
            ButtonGroup bgSandi = new ButtonGroup();
            bgSandi.add(rbDefault); bgSandi.add(rbKustom);
            
            JTextField txtSandiKustom = new JTextField();
            txtSandiKustom.setEnabled(false);
            
            rbDefault.addActionListener(ev -> txtSandiKustom.setEnabled(false));
            rbKustom.addActionListener(ev -> txtSandiKustom.setEnabled(true));

            Object[] dialogKomponen = {
                "ID Pengguna / NIK:", txtId,
                "Nama Lengkap:", txtNama,
                "Peran Otoritas:", cbRole,
                "Golongan Darah (Pendonor saja):", cbGol,
                "Cabang Lokasi PMI:", cbKota,
                "Opsi Kata Sandi:", rbDefault, rbKustom, txtSandiKustom
            };

            int opsi = JOptionPane.showConfirmDialog(this, dialogKomponen, "Pendaftaran Akun Baru oleh Admin", JOptionPane.OK_CANCEL_OPTION);
            if (opsi == JOptionPane.OK_OPTION) {
                String id = txtId.getText().trim();
                String nama = txtNama.getText().trim();
                
                if (id.isEmpty() || nama.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "ID dan Nama tidak boleh kosong!");
                    return;
                }

                if (dataPengguna.containsKey(id)) {
                    JOptionPane.showMessageDialog(this, 
                        "Tindakan Ditolak!\nID / NIK [" + id + "] sudah terdaftar di database.\nAdmin dilarang menimpa (No Override) data lama.", 
                        "Kesalahan Input Data", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String passwordFinal;
                if (rbDefault.isSelected()) {
                    passwordFinal = "12345678";
                } else {
                    passwordFinal = txtSandiKustom.getText().trim();
                    if (passwordFinal.isEmpty()) passwordFinal = "12345678"; 
                }

                dataPengguna.put(id, enkripsiPassword(passwordFinal));
                dataRole.put(id, (String) cbRole.getSelectedItem());
                dataNama.put(id, nama);
                dataKota.put(id, (String) cbKota.getSelectedItem());
                dataGolDarah.put(id, (String) cbGol.getSelectedItem());
                dataTelepon.put(id, "0812345xxxxx");
                dataTanggalLahir.put(id, "01-01-2000");

                simpanDataKeFile(); 
                JOptionPane.showMessageDialog(this, "Akun berhasil dibuat dan disimpan permanen!");
                rakitDashboardSesuaiRole(currentUser);
            }
        });

        JButton btnHapusUser = new JButton("Cabut Akses (Delete)");
        btnHapusUser.addActionListener(e -> {
            String targetId = JOptionPane.showInputDialog(this, "Masukkan NIK/ID Target yang ingin dihapus:");
            if (dataPengguna.containsKey(targetId)) {
                if ("ADMIN".equals(targetId)) {
                    JOptionPane.showMessageDialog(this, "Error: Akun Admin Utama tidak bisa dihapus.");
                    return;
                }
                if (currentUserId.equals(targetId)) {
                    JOptionPane.showMessageDialog(this, "Error: Anda tidak dapat menghapus akun Anda sendiri!");
                    return;
                }
                dataPengguna.remove(targetId);
                dataRole.remove(targetId);
                dataNama.remove(targetId);
                dataGolDarah.remove(targetId);
                dataKota.remove(targetId);
                dataTelepon.remove(targetId);
                dataTanggalLahir.remove(targetId);

                simpanDataKeFile(); 
                JOptionPane.showMessageDialog(this, "ID " + targetId + " sukses dihapus!");
                rakitDashboardSesuaiRole(currentUser);
            } else if (targetId != null) {
                JOptionPane.showMessageDialog(this, "ID tidak ditemukan.");
            }
        });

        panelAksi.add(btnTambahUser);
        panelAksi.add(btnHapusUser);
        panel.add(panelAksi, BorderLayout.SOUTH);

        return panel;
    }

    JPanel buildInterfacePetugas(Petugas petugas) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);

        int countPendonor = 0;
        for (String r : dataRole.values()) if ("PENDONOR".equals(r)) countPendonor++;

        JLabel infoPetugas = new JLabel("Monitoring Validitas Pendonor Terdaftar (Total Pendonor: " + countPendonor + ")", SwingConstants.CENTER);
        infoPetugas.setFont(font("Arial", Font.BOLD, 13));
        infoPetugas.setForeground(MERAH_TUA);
        panel.add(infoPetugas, BorderLayout.NORTH);

        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (String userId : dataPengguna.keySet()) {
            if ("PENDONOR".equals(dataRole.get(userId))) {
                listModel.addElement(userId);
            }
        }
        
        JList<String> listPendonor = new JList<>(listModel);
        listPendonor.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                String id = value.toString();
                String status = getLabelStatusHb(id);
                label.setText(formatListEntryForPendonor(id));
                label.setOpaque(true);
                if (isSelected) {
                    label.setBackground(new Color(220, 235, 255));
                    label.setForeground(GELAP);
                } else if ("LAYAK".equals(status)) {
                    label.setBackground(new Color(220, 255, 220));
                    label.setForeground(HIJAU);
                } else if ("TERVERIFIKASI".equals(status)) {
                    label.setBackground(new Color(238, 238, 238));
                    label.setForeground(Color.DARK_GRAY);
                } else if ("TIDAK LAYAK".equals(status)) {
                    label.setBackground(new Color(255, 225, 225));
                    label.setForeground(new Color(140, 20, 20));
                } else {
                    label.setBackground(new Color(255, 250, 240));
                    label.setForeground(GELAP);
                }
                return label;
            }
        });

        JScrollPane jsp = new JScrollPane(listPendonor);
        panel.add(jsp, BorderLayout.CENTER);

        JPanel panelAksi = new JPanel(new GridLayout(1, 3, 10, 10));
        panelAksi.setOpaque(false);

        JButton btnCekHb = new JButton("Input Kelayakan & HB Darah");
        btnCekHb.addActionListener(e -> {
            String dipilih = listPendonor.getSelectedValue();
            if (dipilih != null) {
                String inputHB = JOptionPane.showInputDialog(this, "Masukkan kadar Hemoglobin (Hb) hasil lab:");
                if (inputHB == null) return;
                try {
                    double hb = Double.parseDouble(inputHB.replace(',', '.').trim());
                    String status = hitungStatusHb(hb);
                    simpanHbUntukPendonor(dipilih, hb, status);
                    listPendonor.repaint();
                    if ("LAYAK".equals(status)) {
                        JOptionPane.showMessageDialog(this, "Status: LAYAK DONOR.\nKadar Hb " + hb + " g/dL sesuai standar.");
                    } else {
                        JOptionPane.showMessageDialog(this, "Status: TIDAK LAYAK.\nKadar Hb " + hb + " g/dL tidak aman untuk donor.");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Harap masukkan angka valid untuk Hb.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Silakan pilih pendonor terlebih dahulu.");
            }
        });

        JButton btnEditHb = new JButton("Edit HB");
        btnEditHb.addActionListener(e -> {
            String dipilih = listPendonor.getSelectedValue();
            if (dipilih != null) {
                String existingHb = dataHb.containsKey(dipilih) ? String.valueOf(dataHb.get(dipilih)) : "";
                String inputHB = JOptionPane.showInputDialog(this, "Ubah nilai Hb untuk " + dipilih + ":", existingHb);
                if (inputHB == null) return;
                try {
                    double hb = Double.parseDouble(inputHB.replace(',', '.').trim());
                    String status = hitungStatusHb(hb);
                    simpanHbUntukPendonor(dipilih, hb, status);
                    listPendonor.repaint();
                    JOptionPane.showMessageDialog(this, "Nilai HB berhasil diperbarui. Status: " + status + ".");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Harap masukkan angka valid untuk Hb.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Silakan pilih pendonor terlebih dahulu.");
            }
        });

        JButton btnVerifikasi = new JButton("Verifikasi Kantung Darah Diterima");
        btnVerifikasi.addActionListener(e -> {
            String dipilih = listPendonor.getSelectedValue();
            if (dipilih == null) {
                JOptionPane.showMessageDialog(this, "Silakan pilih pendonor terlebih dahulu.");
                return;
            }
            if (!dataHb.containsKey(dipilih)) {
                JOptionPane.showMessageDialog(this, "HB belum diinput untuk pendonor ini.");
                return;
            }
            if ("TERVERIFIKASI".equals(dataHbStatus.get(dipilih))) {
                JOptionPane.showMessageDialog(this, "Pendonor ini sudah terverifikasi sebelumnya.");
                return;
            }
            if (isLayakUntukVerifikasi(dipilih)) {
                tandaiTerverifikasi(dipilih);
                listPendonor.repaint();
                JOptionPane.showMessageDialog(this, "Pendonor berhasil diverifikasi. Status layak berubah menjadi terverifikasi.");
            } else {
                JOptionPane.showMessageDialog(this, "Hanya pendonor dengan status LAYAK yang dapat diverifikasi.");
            }
        });

        panelAksi.add(btnCekHb);
        panelAksi.add(btnEditHb);
        panelAksi.add(btnVerifikasi);
        panel.add(panelAksi, BorderLayout.SOUTH);

        return panel;
    }

    JPanel buildInterfacePendonor(Pendonor pendonor) {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setOpaque(false);

        JPanel kartuDigital = new JPanel(new GridLayout(5, 1, 5, 5));
        kartuDigital.setBackground(new Color(255, 245, 242));
        kartuDigital.setBorder(new LineBorder(BORDER_WARNA, 1, true));

        kartuDigital.add(new JLabel("  Nomor ID Donor PMI : " + pendonor.getId()));
        kartuDigital.add(new JLabel("  Nama Lengkap       : " + pendonor.getName()));
        kartuDigital.add(new JLabel("  Tanggal Lahir      : " + pendonor.getDob()));
        kartuDigital.add(new JLabel("  Golongan Darah     : " + pendonor.getBloodType()));
        
        JButton btnUbahSandi = new JButton("Ubah Kata Sandi Akun Mandiri");
        btnUbahSandi.setFont(new Font("Arial", Font.ITALIC, 11));
        btnUbahSandi.addActionListener(e -> {
            String sandiBaru = JOptionPane.showInputDialog(this, "Masukkan Kata Sandi Baru Anda:");
            if (sandiBaru != null && !sandiBaru.trim().isEmpty()) {
                dataPengguna.put(pendonor.getId(), enkripsiPassword(sandiBaru.trim()));
                simpanDataKeFile(); 
                JOptionPane.showMessageDialog(this, "Kata sandi mandiri berhasil diubah!");
            }
        });
        kartuDigital.add(btnUbahSandi);

        panel.add(kartuDigital, BorderLayout.NORTH);

        JPanel gridFitur = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        gridFitur.setOpaque(false);
        gridFitur.add(tombolFitur("", "Daftar Jadwal Donor"));
        gridFitur.add(tombolFitur("", "Cari Lokasi Mobile Unit"));
        gridFitur.add(tombolFitur("", "Unduh Riwayat Piagam"));

        panel.add(gridFitur, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buatKartu() {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(KARTU_BG); g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(BORDER_WARNA); g2.setStroke(new BasicStroke(1.4f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
                g2.dispose();
            }
        };
        p.setOpaque(false); p.setBorder(new EmptyBorder(10, 10, 14, 10));
        return p;
    }

    private JButton tombolFitur(String ikon, String teks) {
        JButton b = new JButton("<html><center>" + ikon + "<br><small>" + teks + "</small></center></html>");
        b.setFont(font("Arial", Font.PLAIN, 12)); b.setForeground(MERAH_TUA);
        b.setBackground(new Color(255, 238, 235));
        b.setBorder(new CompoundBorder(new LineBorder(BORDER_WARNA, 1, true), new EmptyBorder(12, 20, 12, 20)));
        b.setFocusPainted(false); b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addActionListener(e -> JOptionPane.showMessageDialog(this, "Fitur \"" + teks + "\" teraktifkan."));
        return b;
    }

    private JTextField fieldStyled() {
        JTextField f = new JTextField(); f.setFont(font("Arial", Font.PLAIN, 14));
        f.setBackground(new Color(255, 249, 247)); f.setBorder(new CompoundBorder(new LineBorder(BORDER_WARNA, 1, true), new EmptyBorder(8, 12, 8, 12)));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        return f;
    }

    private JPasswordField passFieldStyled() {
        JPasswordField f = new JPasswordField(); f.setFont(font("Arial", Font.PLAIN, 14));
        f.setBackground(new Color(255, 249, 247)); f.setBorder(new CompoundBorder(new LineBorder(BORDER_WARNA, 1, true), new EmptyBorder(8, 12, 8, 12)));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        return f;
    }

    private void styleComboBox(JComboBox<String> cb) {
        cb.setFont(font("Arial", Font.PLAIN, 14)); cb.setBackground(new Color(255, 249, 247));
        cb.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
    }

    private JButton tombolUtama(String teks) {
        JButton b = new JButton(teks) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, MERAH_MUDA, 0, getHeight(), MERAH_TUA);
                g2.setPaint(gp); g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose(); super.paintComponent(g);
            }
        };
        b.setFont(font("Arial", Font.BOLD, 14)); b.setForeground(PUTIH);
        b.setOpaque(false); b.setContentAreaFilled(false); b.setBorderPainted(false); b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        return b;
    }

    private JButton tombolLink(String teks) {
        JButton b = new JButton(teks);
        b.setFont(font("Arial", Font.PLAIN, 12)); b.setForeground(MERAH_PMI);
        b.setContentAreaFilled(false); b.setBorderPainted(false); b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); b.setAlignmentX(Component.CENTER_ALIGNMENT);
        return b;
    }

    private JLabel labelField(String teks) {
        JLabel l = new JLabel(teks); l.setFont(font("Arial", Font.BOLD, 12)); l.setForeground(MERAH_TUA); return l;
    }

    private JLabel labelKecil(String teks) {
        JLabel l = new JLabel(teks); l.setFont(font("Arial", Font.ITALIC, 11)); l.setForeground(ABU_HANGAT); return l;
    }

    private JLabel labelPesan() {
        JLabel l = new JLabel(" "); l.setFont(font("Arial", Font.BOLD, 12)); l.setForeground(MERAH_PMI);
        l.setAlignmentX(Component.CENTER_ALIGNMENT); return l;
    }

    private JLabel labelTengah(String teks, Font f) { return labelTengah(teks, f, GELAP); }

    private JLabel labelTengah(String teks, Font f, Color warna) {
        JLabel l = new JLabel(teks, SwingConstants.CENTER); l.setFont(f); l.setForeground(warna);
        l.setAlignmentX(Component.CENTER_ALIGNMENT); return l;
    }

    private JPanel padded(JComponent c) {
        JPanel p = new JPanel(); p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.setOpaque(false); p.setBorder(new EmptyBorder(0, 24, 0, 24)); p.add(c); return p;
    }

    private JPanel paddedKiri(JComponent c) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        p.setOpaque(false); p.setBorder(new EmptyBorder(0, 24, 0, 24)); p.add(c); return p;
    }

    private JSeparator pemisah() {
        JSeparator s = new JSeparator(); s.setForeground(BORDER_WARNA); s.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1)); return s;
    }

    private JPanel garisDekoratif() {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(MERAH_PMI); g.fillRect(getWidth()/2 - 30, 0, 60, 3);
            }
        };
        p.setOpaque(false); p.setPreferredSize(new Dimension(1, 8)); p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 8)); return p;
    }

    private void tampilPesan(JLabel lbl, String pesan, boolean sukses) {
        lbl.setText(pesan); lbl.setForeground(sukses ? HIJAU : MERAH_PMI);
    }

    private void goyangKartu(JComponent c) {
        Point asli = c.getLocation();
        int[] geser = {-6, 6, -4, 4, -2, 2, 0};
        Timer t = new Timer(40, null);
        int[] i = {0};
        t.addActionListener(e -> {
            if (i[0] >= geser.length) { c.setLocation(asli); t.stop(); return; }
            c.setLocation(asli.x + geser[i[0]++], asli.y);
        });
        t.start();
    }

    private String enkripsiPassword(String plain) {
        return plain; 
    }

    private Font font(String nama, int gaya, int ukuran) { return new Font(nama, gaya, ukuran); }

    class PanelLatar extends JPanel {
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(GELAP); g2.fillRect(0, 0, getWidth(), getHeight());
            RadialGradientPaint rg1 = new RadialGradientPaint(
                new Point2D.Float(getWidth() * 0.22f, getHeight() * 0.28f), getWidth() * 0.42f,
                new float[]{0f, 1f}, new Color[]{new Color(196, 22, 28, 60), new Color(0, 0, 0, 0)}
            );
            g2.setPaint(rg1); g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setColor(new Color(255, 255, 255, 6));
            for (int x = 28; x < getWidth(); x += 28)
                for (int y = 28; y < getHeight(); y += 28)
                    g2.fillOval(x - 1, y - 1, 2, 2);
            g2.dispose();
        }
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> { new AplikasiPMI().setVisible(true); });
    }
}