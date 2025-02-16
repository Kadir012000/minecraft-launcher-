import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

class FullMinecraftLauncher {
    // Use APPDATA on Windows; fallback to user's home if not available.
    private static final String APPDATA = (System.getenv("APPDATA") != null)
            ? System.getenv("APPDATA")
            : System.getProperty("user.home");
    private static final File LAUNCHER_DIR = new File(APPDATA, "FullMinecraftLauncher");
    private static final File MODS_DIR = new File(LAUNCHER_DIR, "mods");

    // Keys for CardLayout panels.
    private static final String LOGIN_PANEL = "loginPanel";
    private static final String LAUNCHER_PANEL = "launcherPanel";

    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public static void main(String[] args) {
        // Set dark futuristic theme.
        setDarkTheme();

        System.out.println("Starting FullMinecraftLauncher with Centered Tabs and No Underline...");
        createDirectories();
        SwingUtilities.invokeLater(() -> {
            FullMinecraftLauncher launcher = new FullMinecraftLauncher();
            launcher.createAndShowGUI();
        });
    }

    // Set Nimbus Look & Feel with dark overrides.
    private static void setDarkTheme() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Nimbus L&F not found, using default.");
        }
        // Dark theme colors.
        UIManager.put("control", new Color(40, 40, 40));
        UIManager.put("info", new Color(40, 40, 40));
        UIManager.put("nimbusBase", new Color(25, 25, 25));
        UIManager.put("nimbusAlertYellow", new Color(248, 187, 0));
        UIManager.put("nimbusDisabledText", new Color(150, 150, 150));
        UIManager.put("nimbusFocus", new Color(115, 164, 209));
        UIManager.put("nimbusGreen", new Color(76, 187, 23));
        UIManager.put("nimbusInfoBlue", new Color(66, 139, 221));
        UIManager.put("nimbusLightBackground", new Color(50, 50, 50));
        UIManager.put("nimbusOrange", new Color(191, 98, 4));
        UIManager.put("nimbusRed", new Color(169, 46, 34));
        UIManager.put("nimbusSelectedText", new Color(230, 230, 230));
        UIManager.put("nimbusSelectionBackground", new Color(104, 93, 156));
        UIManager.put("text", new Color(230, 230, 230));

        // Adjust tab area insets to center tabs.
        UIManager.put("TabbedPane.tabAreaInsets", new Insets(10, 0, 10, 0));
        UIManager.put("TabbedPane.contentBorderInsets", new Insets(0, 0, 0, 0));
    }

    // Create directories for the launcher and mods.
    private static void createDirectories() {
        System.out.println("Creating launcher directories...");
        if (!LAUNCHER_DIR.exists()) {
            if (LAUNCHER_DIR.mkdirs()) {
                System.out.println("Created launcher directory: " + LAUNCHER_DIR.getAbsolutePath());
            } else {
                JOptionPane.showMessageDialog(null, "Failed to create launcher directory at: "
                        + LAUNCHER_DIR.getAbsolutePath(), "Error", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        }
        if (!MODS_DIR.exists()) {
            if (MODS_DIR.mkdirs()) {
                System.out.println("Created mods directory: " + MODS_DIR.getAbsolutePath());
            } else {
                JOptionPane.showMessageDialog(null, "Failed to create mods directory at: "
                        + MODS_DIR.getAbsolutePath(), "Error", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        }
    }

    // Create and display the main GUI window.
    private void createAndShowGUI() {
        frame = new JFrame("Full Minecraft Launcher");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(950, 650);
        frame.setLocationRelativeTo(null);

        // Use a custom panel with gradient background for the frame.
        GradientPanel background = new GradientPanel();
        background.setLayout(new BorderLayout());
        frame.setContentPane(background);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setOpaque(false);

        // Add login and launcher panels.
        mainPanel.add(new LoginPanel(), LOGIN_PANEL);
        mainPanel.add(new LauncherPanel(), LAUNCHER_PANEL);

        background.add(mainPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    // Utility method to switch panels.
    private void switchToPanel(String panelName) {
        System.out.println("Switching to panel: " + panelName);
        cardLayout.show(mainPanel, panelName);
    }

    // ----- CUSTOM LOGIN PANEL -----
    class LoginPanel extends RoundedPanel {
        public LoginPanel() {
            super(new BorderLayout(), 25, new Color(55, 55, 55));
            setBorder(new EmptyBorder(20, 20, 20, 20));

            JLabel title = new JLabel("Welcome to Full Minecraft Launcher", SwingConstants.CENTER);
            title.setFont(new Font("Segoe UI", Font.BOLD, 24));
            title.setForeground(Color.WHITE);
            add(title, BorderLayout.NORTH);

            // Center form.
            JPanel formPanel = new JPanel(new GridBagLayout());
            formPanel.setOpaque(false);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridx = 0;
            gbc.weightx = 1;

            // Username
            gbc.gridy = 0;
            JLabel usernameLabel = new JLabel("Username");
            usernameLabel.setForeground(Color.WHITE);
            usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            formPanel.add(usernameLabel, gbc);

            gbc.gridy = 1;
            RoundedTextField usernameField = new RoundedTextField(20);
            usernameField.setPreferredSize(new Dimension(300, 40));
            formPanel.add(usernameField, gbc);

            // Password
            gbc.gridy = 2;
            JLabel passwordLabel = new JLabel("Password");
            passwordLabel.setForeground(Color.WHITE);
            passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            formPanel.add(passwordLabel, gbc);

            gbc.gridy = 3;
            RoundedPasswordField passwordField = new RoundedPasswordField(20);
            passwordField.setPreferredSize(new Dimension(300, 40));
            formPanel.add(passwordField, gbc);

            add(formPanel, BorderLayout.CENTER);

            // Bottom: Sign In Later button.
            JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            bottomPanel.setOpaque(false);
            RoundedButton signInLaterButton = new RoundedButton("Sign In Later");
            signInLaterButton.setPreferredSize(new Dimension(220, 60));
            bottomPanel.add(signInLaterButton);
            add(bottomPanel, BorderLayout.SOUTH);

            signInLaterButton.addActionListener(e -> {
                System.out.println("Bypassing login...");
                switchToPanel(LAUNCHER_PANEL);
            });
        }
    }
    // ------------------------------------------------------------------------

    // ----- SIMULATED LAUNCHER PANEL WITH SIDEBAR, MODERN TABS, AND SETTINGS -----
    class LauncherPanel extends RoundedPanel {
        private JTabbedPane tabbedPane;
        private JRadioButton forgeButton;
        private JRadioButton fabricButton;
        private JComboBox<String> versionComboBox;
        private RoundedButton installButton;
        private RoundedButton playButton;
        private JProgressBar progressBar;
        private JTextArea messageArea;
        private boolean modLoaderInstalled = false;

        public LauncherPanel() {
            super(new BorderLayout(10, 10), 25, new Color(55, 55, 55));
            setBorder(new EmptyBorder(20, 20, 20, 20));

            // Top header
            JLabel header = new JLabel("Minecraft Launcher", SwingConstants.CENTER);
            header.setFont(new Font("Segoe UI", Font.BOLD, 32));
            header.setForeground(Color.WHITE);
            add(header, BorderLayout.NORTH);

            // Create a new main content panel with sidebar and main area.
            JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
            contentPanel.setOpaque(false);

            // Sidebar panel (for logo/news)
            JPanel sidebar = new JPanel();
            sidebar.setOpaque(false);
            sidebar.setPreferredSize(new Dimension(200, 0));
            sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
            // Logo area
            JLabel logoLabel = new JLabel("LOGO", SwingConstants.CENTER);
            logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
            logoLabel.setForeground(Color.WHITE);
            logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            logoLabel.setBorder(new EmptyBorder(10, 0, 20, 0));
            sidebar.add(logoLabel);
            // News area placeholder
            JTextArea newsArea = new JTextArea("Latest News:\n- New updates coming soon!");
            newsArea.setEditable(false);
            newsArea.setLineWrap(true);
            newsArea.setWrapStyleWord(true);
            newsArea.setOpaque(false);
            newsArea.setForeground(Color.LIGHT_GRAY);
            newsArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            sidebar.add(newsArea);

            contentPanel.add(sidebar, BorderLayout.WEST);

            // Main tabbed area.
            tabbedPane = new JTabbedPane();
            tabbedPane.setBackground(new Color(60, 60, 60));
            tabbedPane.setForeground(Color.WHITE);
            // Disable the underline by overriding the UI.
            tabbedPane.setUI(new BasicTabbedPaneUI() {
                @Override
                protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
                    // Do nothing to disable the underline.
                }
                @Override
                protected Insets getContentBorderInsets(int tabPlacement) {
                    return new Insets(0, 0, 0, 0);
                }
            });

            // Home Tab.
            FadePanel homePanel = new FadePanel();
            homePanel.setLayout(new BorderLayout());
            JLabel welcomeLabel = new JLabel("Welcome to the Launcher!", SwingConstants.CENTER);
            welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
            welcomeLabel.setForeground(Color.WHITE);
            homePanel.add(welcomeLabel, BorderLayout.CENTER);
            tabbedPane.addTab("Home", homePanel);

            // Version Tab.
            FadePanel versionPanel = new FadePanel();
            versionPanel.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(8, 8, 8, 8);
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.WEST;
            JLabel modLoaderLabel = new JLabel("Select Mod Loader:");
            modLoaderLabel.setForeground(Color.WHITE);
            modLoaderLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            versionPanel.add(modLoaderLabel, gbc);

            gbc.gridy++;
            forgeButton = new JRadioButton("Forge");
            forgeButton.setBackground(new Color(50, 50, 50));
            forgeButton.setForeground(Color.WHITE);
            fabricButton = new JRadioButton("Fabric");
            fabricButton.setBackground(new Color(50, 50, 50));
            fabricButton.setForeground(Color.WHITE);
            ButtonGroup loaderGroup = new ButtonGroup();
            loaderGroup.add(forgeButton);
            loaderGroup.add(fabricButton);
            JPanel loaderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            loaderPanel.setOpaque(false);
            loaderPanel.add(forgeButton);
            loaderPanel.add(fabricButton);
            versionPanel.add(loaderPanel, gbc);

            gbc.gridy++;
            JLabel versionLabel = new JLabel("Select Minecraft Version:");
            versionLabel.setForeground(Color.WHITE);
            versionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            versionPanel.add(versionLabel, gbc);

            gbc.gridy++;
            // Updated version list with 1.21.4 added.
            versionComboBox = new JComboBox<>(new String[]{"1.16.5", "1.17.1", "1.18.2", "1.19.4", "1.20.1", "1.21.4"});
            versionComboBox.setBackground(new Color(60, 60, 60));
            versionComboBox.setForeground(Color.WHITE);
            versionComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            versionPanel.add(versionComboBox, gbc);

            tabbedPane.addTab("Version", versionPanel);

            // Mods Tab.
            FadePanel modsPanel = new FadePanel();
            modsPanel.setLayout(new BorderLayout());
            JLabel modsLabel = new JLabel("Manage your mods", SwingConstants.CENTER);
            modsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            modsLabel.setForeground(Color.WHITE);
            modsPanel.add(modsLabel, BorderLayout.CENTER);
            tabbedPane.addTab("Mods", modsPanel);

            // Settings Tab with additional settings.
            FadePanel settingsPanel = new FadePanel();
            settingsPanel.setLayout(new BorderLayout(10, 10));
            JPanel settingsMain = new JPanel(new GridBagLayout());
            settingsMain.setOpaque(false);
            GridBagConstraints sgbc = new GridBagConstraints();
            sgbc.insets = new Insets(10, 10, 10, 10);
            sgbc.fill = GridBagConstraints.HORIZONTAL;
            sgbc.gridx = 0;
            sgbc.gridy = 0;

            JLabel settingsLabel = new JLabel("Settings");
            settingsLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
            settingsLabel.setForeground(Color.WHITE);
            settingsMain.add(settingsLabel, sgbc);

            // Enable Auto-Update
            sgbc.gridy++;
            JCheckBox autoUpdateCheck = new JCheckBox("Enable Auto-Update");
            autoUpdateCheck.setOpaque(false);
            autoUpdateCheck.setForeground(Color.WHITE);
            autoUpdateCheck.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            settingsMain.add(autoUpdateCheck, sgbc);

            // Memory Allocation slider.
            sgbc.gridy++;
            JLabel memoryLabel = new JLabel("Memory Allocation (MB):");
            memoryLabel.setForeground(Color.WHITE);
            memoryLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            settingsMain.add(memoryLabel, sgbc);

            sgbc.gridy++;
            JSlider memorySlider = new JSlider(512, 4096, 2048);
            memorySlider.setMajorTickSpacing(512);
            memorySlider.setMinorTickSpacing(128);
            memorySlider.setPaintTicks(true);
            memorySlider.setPaintLabels(true);
            memorySlider.setBackground(new Color(60, 60, 60));
            memorySlider.setForeground(Color.WHITE);
            settingsMain.add(memorySlider, sgbc);

            // Custom Java Path text field.
            sgbc.gridy++;
            JLabel javaPathLabel = new JLabel("Custom Java Path:");
            javaPathLabel.setForeground(Color.WHITE);
            javaPathLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            settingsMain.add(javaPathLabel, sgbc);

            sgbc.gridy++;
            JTextField javaPathField = new JTextField();
            javaPathField.setPreferredSize(new Dimension(300, 30));
            javaPathField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            settingsMain.add(javaPathField, sgbc);

            // Save Settings Button.
            sgbc.gridy++;
            RoundedButton saveSettingsButton = new RoundedButton("Save Settings");
            saveSettingsButton.addActionListener(e -> {
                // For now, just print the settings to the console.
                System.out.println("Auto-Update: " + autoUpdateCheck.isSelected());
                System.out.println("Memory Allocation: " + memorySlider.getValue() + " MB");
                System.out.println("Custom Java Path: " + javaPathField.getText());
                JOptionPane.showMessageDialog(settingsPanel, "Settings saved!", "Settings", JOptionPane.INFORMATION_MESSAGE);
            });
            settingsMain.add(saveSettingsButton, sgbc);

            settingsPanel.add(settingsMain, BorderLayout.CENTER);
            tabbedPane.addTab("Settings", settingsPanel);

            // Set custom tab headers (larger and centered)
            for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                tabbedPane.setTabComponentAt(i, new TabHeader(tabbedPane.getTitleAt(i)));
            }

            // Animate fade-in on tab change.
            tabbedPane.getModel().addChangeListener(e -> {
                int index = tabbedPane.getSelectedIndex();
                Component comp = tabbedPane.getComponentAt(index);
                if (comp instanceof FadePanel) {
                    animateFade((FadePanel) comp);
                }
            });

            contentPanel.add(tabbedPane, BorderLayout.CENTER);
            add(contentPanel, BorderLayout.CENTER);

            // Bottom panel for install/play controls.
            JPanel bottomPanel = new JPanel(new BorderLayout());
            bottomPanel.setOpaque(false);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
            buttonPanel.setOpaque(false);
            installButton = new RoundedButton("Install Mod Loader");
            installButton.setPreferredSize(new Dimension(220, 60));
            playButton = new RoundedButton("Play Minecraft");
            playButton.setPreferredSize(new Dimension(220, 60));
            buttonPanel.add(installButton);
            buttonPanel.add(playButton);
            bottomPanel.add(buttonPanel, BorderLayout.NORTH);

            progressBar = new JProgressBar(0, 100);
            progressBar.setStringPainted(true);
            messageArea = new JTextArea(4, 40);
            messageArea.setEditable(false);
            messageArea.setLineWrap(true);
            messageArea.setWrapStyleWord(true);
            messageArea.setBackground(new Color(60, 60, 60));
            messageArea.setForeground(Color.WHITE);
            JScrollPane scrollPane = new JScrollPane(messageArea);
            scrollPane.setBorder(BorderFactory.createTitledBorder("Status"));
            bottomPanel.add(progressBar, BorderLayout.CENTER);
            bottomPanel.add(scrollPane, BorderLayout.SOUTH);

            add(bottomPanel, BorderLayout.SOUTH);

            // Button actions.
            installButton.addActionListener(e -> installModLoader());
            playButton.addActionListener(e -> launchMinecraft());
        }

        // Animate fade-in for a FadePanel.
        private void animateFade(FadePanel panel) {
            panel.setAlpha(0f);
            Timer timer = new Timer(30, null);
            timer.addActionListener(new ActionListener() {
                float alpha = 0f;
                @Override
                public void actionPerformed(ActionEvent e) {
                    alpha += 0.05f;
                    if (alpha >= 1f) {
                        alpha = 1f;
                        panel.setAlpha(alpha);
                        timer.stop();
                    } else {
                        panel.setAlpha(alpha);
                    }
                }
            });
            timer.start();
        }

        // Simulate mod loader installation.
        private void installModLoader() {
            System.out.println("Installing mod loader...");
            if (!forgeButton.isSelected() && !fabricButton.isSelected()) {
                JOptionPane.showMessageDialog(this, "Please select a mod loader to install.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            String modLoader = forgeButton.isSelected() ? "Forge" : "Fabric";
            messageArea.setText("Installing " + modLoader + "...\n");
            installButton.setEnabled(false);
            playButton.setEnabled(false);
            SwingWorker<Void, Integer> installer = new SwingWorker<Void, Integer>() {
                @Override
                protected Void doInBackground() throws Exception {
                    for (int i = 0; i <= 100; i += 5) {
                        TimeUnit.MILLISECONDS.sleep(100);
                        publish(i);
                    }
                    return null;
                }
                @Override
                protected void process(java.util.List<Integer> chunks) {
                    int latest = chunks.get(chunks.size() - 1);
                    progressBar.setValue(latest);
                    messageArea.append("Installation progress: " + latest + "%\n");
                }
                @Override
                protected void done() {
                    modLoaderInstalled = true;
                    messageArea.append("Installation complete!\n");
                    installButton.setEnabled(true);
                    playButton.setEnabled(true);
                }
            };
            installer.execute();
        }

        // Simulate launching Minecraft.
        private void launchMinecraft() {
            System.out.println("Launching Minecraft...");
            if (!forgeButton.isSelected() && !fabricButton.isSelected()) {
                JOptionPane.showMessageDialog(this, "Please select a mod loader.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!modLoaderInstalled) {
                JOptionPane.showMessageDialog(this, "Mod loader is not installed. Please install it first.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            String modLoader = forgeButton.isSelected() ? "Forge" : "Fabric";
            String version = (String) versionComboBox.getSelectedItem();
            messageArea.append("Launching Minecraft " + version + " with " + modLoader + "...\n");

            // Determine jar file name based on selected version.
            String jarName = "minecraft-" + version + ".jar";
            File jarFile = new File(LAUNCHER_DIR, jarName);

            if (!jarFile.exists()) {
                messageArea.append("Minecraft " + version + " not found locally. Downloading...\n");
                SwingWorker<Void, Integer> downloader = new SwingWorker<Void, Integer>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        for (int i = 0; i <= 100; i += 10) {
                            TimeUnit.MILLISECONDS.sleep(100);
                            publish(i);
                        }
                        return null;
                    }
                    @Override
                    protected void process(java.util.List<Integer> chunks) {
                        int latest = chunks.get(chunks.size() - 1);
                        progressBar.setValue(latest);
                        messageArea.append("Download progress: " + latest + "%\n");
                    }
                    @Override
                    protected void done() {
                        try {
                            jarFile.createNewFile();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        messageArea.append("Download complete!\n");
                        launchJar(jarName, version, modLoader);
                    }
                };
                downloader.execute();
            } else {
                launchJar(jarName, version, modLoader);
            }
        }

        private void launchJar(String jarName, String version, String modLoader) {
            ProcessBuilder pb = new ProcessBuilder(
                    "java",
                    "-jar",
                    jarName,
                    "--version", version,
                    "--modloader", modLoader
            );
            pb.directory(LAUNCHER_DIR);
            try {
                pb.start();
                messageArea.append("Minecraft launched (simulated)!\n");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error launching Minecraft: " + ex.getMessage(), "Launch Error",
                        JOptionPane.ERROR_MESSAGE);
                messageArea.append("Error launching Minecraft.\n");
            }
        }

        // Custom tab header component for the tabbed pane.
        class TabHeader extends JPanel {
            public TabHeader(String title) {
                setOpaque(false);
                setLayout(new BorderLayout());
                JLabel label = new JLabel(title, SwingConstants.CENTER);
                label.setFont(new Font("Segoe UI", Font.BOLD, 20));
                label.setForeground(Color.WHITE);
                add(label, BorderLayout.CENTER);
                // Set a preferred size for the tab header.
                setPreferredSize(new Dimension(150, 40));
            }
        }
    }
    // ------------------------------------------------------------------------

    // ------------------- CUSTOM COMPONENTS -------------------

    // Gradient background panel.
    static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            int width = getWidth();
            int height = getHeight();
            Color color1 = new Color(30, 30, 30);
            Color color2 = new Color(60, 60, 60);
            GradientPaint gp = new GradientPaint(0, 0, color1, width, height, color2);
            g2.setPaint(gp);
            g2.fillRect(0, 0, width, height);
            super.paintComponent(g);
        }
    }

    // A custom rounded panel.
    static class RoundedPanel extends JPanel {
        private int cornerRadius;
        private Color backgroundColor;

        public RoundedPanel(LayoutManager layout, int radius, Color bgColor) {
            super(layout);
            this.cornerRadius = radius;
            this.backgroundColor = bgColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Dimension arcs = new Dimension(cornerRadius, cornerRadius);
            int width = getWidth();
            int height = getHeight();
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(backgroundColor);
            g2.fillRoundRect(0, 0, width, height, arcs.width, arcs.height);
            g2.setColor(new Color(80, 80, 80));
            g2.drawRoundRect(0, 0, width - 1, height - 1, arcs.width, arcs.height);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // A custom rounded button with hover effect (pop-out disabled).
    static class RoundedButton extends JButton {
        private boolean hover = false;

        public RoundedButton(String label) {
            super(label);
            setOpaque(false);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 20));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hover = true;
                    repaint();
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    hover = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (getModel().isPressed()) {
                g2.setColor(new Color(70, 70, 70));
            } else if (hover) {
                g2.setColor(new Color(60, 60, 60));
            } else {
                g2.setColor(new Color(50, 50, 50));
            }
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
            g2.setColor(getForeground());
            FontMetrics fm = g2.getFontMetrics();
            Rectangle textBounds = fm.getStringBounds(getText(), g2).getBounds();
            int textX = (getWidth() - textBounds.width) / 2;
            int textY = (getHeight() - textBounds.height) / 2 + fm.getAscent();
            g2.drawString(getText(), textX, textY);
            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        public void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(80, 80, 80));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 25, 25);
            g2.dispose();
        }
    }

    // A custom rounded text field.
    static class RoundedTextField extends JTextField {
        private int arcWidth = 25;
        private int arcHeight = 25;

        public RoundedTextField(int columns) {
            super(columns);
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
            setForeground(Color.WHITE);
            setBackground(new Color(60, 60, 60));
            setFont(new Font("Segoe UI", Font.PLAIN, 16));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arcWidth, arcHeight);
            super.paintComponent(g2);
            g2.dispose();
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(80, 80, 80));
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, arcWidth, arcHeight);
            g2.dispose();
        }
    }

    // A custom rounded password field.
    static class RoundedPasswordField extends JPasswordField {
        private int arcWidth = 25;
        private int arcHeight = 25;

        public RoundedPasswordField(int columns) {
            super(columns);
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
            setForeground(Color.WHITE);
            setBackground(new Color(60, 60, 60));
            setFont(new Font("Segoe UI", Font.PLAIN, 16));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arcWidth, arcHeight);
            super.paintComponent(g2);
            g2.dispose();
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(80, 80, 80));
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, arcWidth, arcHeight);
            g2.dispose();
        }
    }

    // A custom fadeable panel.
    static class FadePanel extends JPanel {
        private float alpha = 1.0f;

        public FadePanel() {
            super();
            setOpaque(false);
        }

        public void setAlpha(float a) {
            alpha = a;
            repaint();
        }

        public float getAlpha() {
            return alpha;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            super.paintComponent(g2);
            g2.dispose();
        }
    }
}
