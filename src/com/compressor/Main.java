package com.compressor;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.swing.*;
import javax.swing.border.*;
import javax.imageio.ImageIO;

/**
 * Main UI class for the File Compressor Tool.
 * Features a modern design with rounded buttons, dark mode support,
 * and integrated compression/decompression logic.
 */
public class Main extends JFrame {
    private static final long serialVersionUID = 1L;

    // Modern Color Palette
    private static class Theme {
        Color bg = new Color(240, 244, 248);
        Color panel = Color.WHITE;
        Color text = new Color(33, 37, 41);
        Color accent = new Color(13, 110, 253);
        Color success = new Color(25, 135, 84);
        Color warning = new Color(255, 193, 7);
        Color danger = new Color(220, 53, 69);
        Color border = new Color(222, 226, 230);
    }

    private static final Theme LIGHT_THEME = new Theme();
    private static final Theme DARK_THEME = new Theme();
    static {
        DARK_THEME.bg = new Color(33, 37, 41);
        DARK_THEME.panel = new Color(52, 58, 64);
        DARK_THEME.text = new Color(248, 249, 250);
        DARK_THEME.accent = new Color(102, 178, 255);
        DARK_THEME.border = new Color(73, 80, 87);
    }

    private Theme currentTheme = LIGHT_THEME;

    // Components
    private JPanel mainContent;
    private JLabel originalPreview, outputPreview;
    private JLabel originalInfo, outputInfo;
    private JLabel statusLabel;
    private JProgressBar progressBar;
    private ModernButton selectBtn, compressBtn, decompressBtn, saveBtn, resetBtn, themeToggleBtn;
    private JSlider qualitySlider;
    private JSpinner qualitySpinner;
    private JLabel qualityLabel;

    private File[] selectedFiles;
    private File tempOutputFile;
    private BufferedImage originalImage, outputImage;
    private boolean isDarkMode = false;

    public Main() {
        setupFrame();
        initComponents();
        layoutComponents();
        updateTheme();
        setVisible(true);
    }

    private void setupFrame() {
        setTitle("File Compressor & Decompressor");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 750);
        setMinimumSize(new Dimension(900, 650));
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        // Preview Labels
        originalPreview = createPreviewLabel("Original File Preview");
        outputPreview = createPreviewLabel("Output Preview");

        originalInfo = new JLabel("No file selected", SwingConstants.CENTER);
        outputInfo = new JLabel("Waiting for action...", SwingConstants.CENTER);

        statusLabel = new JLabel("", SwingConstants.LEFT);
        progressBar = new JProgressBar(0, 100);
        progressBar.setVisible(false);
        progressBar.setStringPainted(true);

        // Buttons
        selectBtn = new ModernButton("Select Files", "");
        compressBtn = new ModernButton("Compress", "");
        decompressBtn = new ModernButton("Decompress", "");
        saveBtn = new ModernButton("Save Output", "");
        resetBtn = new ModernButton("Reset", "");
        themeToggleBtn = new ModernButton("Dark Mode", "");

        qualitySlider = new JSlider(0, 100, 75);
        qualitySlider.setOpaque(false);
        qualityLabel = new JLabel("Quality:");

        // Editable spinner synced with slider
        qualitySpinner = new JSpinner(new SpinnerNumberModel(75, 0, 100, 1));
        qualitySpinner.setEditor(new JSpinner.NumberEditor(qualitySpinner, "#"));
        qualitySpinner.setPreferredSize(new Dimension(60, 32));

        // Initially disabled
        compressBtn.setEnabled(false);
        decompressBtn.setEnabled(false);
        saveBtn.setEnabled(false);

        // Add Listeners
        selectBtn.addActionListener(e -> handleFileSelection());
        compressBtn.addActionListener(e -> handleCompression());
        decompressBtn.addActionListener(e -> handleDecompression());
        saveBtn.addActionListener(e -> handleSave());
        resetBtn.addActionListener(e -> resetApp());
        themeToggleBtn.addActionListener(e -> toggleTheme());
        // Sync slider -> spinner
        qualitySlider.addChangeListener(e -> {
            int val = qualitySlider.getValue();
            if (!qualitySpinner.getValue().equals(val))
                qualitySpinner.setValue(val);
        });
        // Sync spinner -> slider
        qualitySpinner.addChangeListener(e -> {
            int val = (Integer) qualitySpinner.getValue();
            if (qualitySlider.getValue() != val)
                qualitySlider.setValue(val);
        });
    }

    private JLabel createPreviewLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setBorder(new DashedBorder(Color.GRAY));
        label.setFont(new Font("Inter", Font.ITALIC, 14));
        return label;
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());
        mainContent = new JPanel(new BorderLayout(20, 20));
        mainContent.setBorder(new EmptyBorder(25, 25, 25, 25));
        add(mainContent, BorderLayout.CENTER);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("File Compressor", SwingConstants.LEFT);
        title.setFont(new Font("Inter", Font.BOLD, 32));
        header.add(title, BorderLayout.WEST);
        header.add(themeToggleBtn, BorderLayout.EAST);
        mainContent.add(header, BorderLayout.NORTH);

        // Center Panels (Split View)
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        centerPanel.setOpaque(false);

        JPanel leftPanel = createSection("Original File", originalPreview, originalInfo);
        JPanel rightPanel = createSection("Output Result", outputPreview, outputInfo);

        centerPanel.add(leftPanel);
        centerPanel.add(rightPanel);
        mainContent.add(centerPanel, BorderLayout.CENTER);

        // Bottom Controls
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 15));
        bottomPanel.setOpaque(false);

        // Quality Control
        JPanel qualityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        qualityPanel.setOpaque(false);
        qualityPanel.add(qualityLabel);
        qualityPanel.add(qualitySpinner);
        qualityPanel.add(new JLabel("%"));
        qualitySlider.setPreferredSize(new Dimension(200, 40));
        qualityPanel.add(qualitySlider);

        // Buttons Bar
        JPanel buttonsBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonsBar.setOpaque(false);
        buttonsBar.add(selectBtn);
        buttonsBar.add(compressBtn);
        buttonsBar.add(decompressBtn);
        buttonsBar.add(saveBtn);
        buttonsBar.add(resetBtn);

        // Status Bar
        JPanel statusBar = new JPanel(new BorderLayout(10, 0));
        statusBar.setOpaque(false);
        statusBar.add(statusLabel, BorderLayout.WEST);
        statusBar.add(progressBar, BorderLayout.CENTER);

        bottomPanel.add(qualityPanel, BorderLayout.NORTH);
        bottomPanel.add(buttonsBar, BorderLayout.CENTER);
        bottomPanel.add(statusBar, BorderLayout.SOUTH);

        mainContent.add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createSection(String title, JLabel preview, JLabel info) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);

        JLabel sectionTitle = new JLabel(title);
        sectionTitle.setFont(new Font("Inter", Font.BOLD, 18));
        panel.add(sectionTitle, BorderLayout.NORTH);

        JPanel contentCard = new JPanel(new BorderLayout(10, 10));
        contentCard.setBackground(Color.WHITE);
        contentCard.setBorder(new CompoundBorder(
                new LineBorder(new Color(230, 230, 230), 1, true),
                new EmptyBorder(15, 15, 15, 15)));

        contentCard.add(preview, BorderLayout.CENTER);
        contentCard.add(info, BorderLayout.SOUTH);

        panel.add(contentCard, BorderLayout.CENTER);
        return panel;
    }

    private void handleFileSelection() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        chooser.setMultiSelectionEnabled(true);
        chooser.setDialogTitle("Select Files  (Ctrl+click / Shift+click for multiple)");
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedFiles = chooser.getSelectedFiles();
            if (selectedFiles == null || selectedFiles.length == 0)
                return;

            // Reset output info & preview for the new selection
            tempOutputFile = null;
            outputImage = null;
            outputPreview.setIcon(null);
            outputPreview.setText("Output Preview");
            outputInfo.setText("Waiting for action...");
            saveBtn.setEnabled(false);

            updateOriginalFileDetails();
            String statusMsg = selectedFiles.length == 1
                    ? "Selected: " + selectedFiles[0].getName()
                    : "Selected: " + selectedFiles.length + " files";
            statusLabel.setText(statusMsg);
            statusLabel.setForeground(currentTheme.accent);

            // Decompress enabled if ALL selected files are .zip files
            boolean allZips = selectedFiles.length > 0
                    && java.util.Arrays.stream(selectedFiles)
                            .allMatch(f -> f.isFile() && f.getName().toLowerCase().endsWith(".zip"));
            compressBtn.setEnabled(true);
            decompressBtn.setEnabled(allZips);

            // Quality controls always enabled for all file types
            qualitySlider.setEnabled(true);
            qualitySpinner.setEnabled(true);
        }
    }

    private long getFolderSize(File folder) {
        long length = 0;
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    length += file.length();
                } else {
                    length += getFolderSize(file);
                }
            }
        }
        return length;
    }

    private void updateOriginalFileDetails() {
        if (selectedFiles.length == 1) {
            File f = selectedFiles[0];
            String name = f.getName();
            long size = f.isDirectory() ? getFolderSize(f) : f.length();
            originalInfo.setText(String.format("<html><b>Name:</b> %s<br><b>Size:</b> %.2f KB</html>",
                    name, size / 1024.0));

            if (!f.isDirectory() && name.toLowerCase().matches(".*\\.(jpg|jpeg|png)$")) {
                try {
                    originalImage = ImageIO.read(f);
                    originalPreview.setIcon(new ImageIcon(
                            getScaledImage(originalImage, originalPreview.getWidth(), originalPreview.getHeight())));
                    originalPreview.setText("");
                } catch (IOException e) {
                    originalPreview.setText("Preview unavailable");
                }
            } else {
                originalPreview.setIcon(null);
                originalPreview.setText(f.isDirectory() ? "📁 Directory Selected" : "No image preview for this type");
            }
        } else {
            // Multiple files selected
            long totalSize = 0;
            for (File f : selectedFiles)
                totalSize += f.isDirectory() ? getFolderSize(f) : f.length();
            originalInfo.setText(String.format(
                    "<html><b>Files:</b> %d selected<br><b>Total Size:</b> %.2f KB</html>",
                    selectedFiles.length, totalSize / 1024.0));
            originalPreview.setIcon(null);
            originalPreview.setText("📦 " + selectedFiles.length + " files selected → will be zipped");
        }
    }

    private void handleCompression() {
        if (selectedFiles == null || selectedFiles.length == 0)
            return;

        startTask("Compressing...");
        outputImage = null;
        new Thread(() -> {
            try {
                int q = (Integer) qualitySpinner.getValue();

                if (selectedFiles.length > 1) {
                    // Multiple files → always bundle into one ZIP
                    tempOutputFile = File.createTempFile("compressor_out_", ".zip");
                    Compressor.compressMultipleToZip(selectedFiles, tempOutputFile, q);
                } else {
                    // Single file — pick best format
                    File f = selectedFiles[0];
                    String name = f.getName().toLowerCase();
                    String outExt;
                    if (name.endsWith(".zip")) {
                        outExt = ".zip";
                    } else if (!f.isDirectory() && name.matches(".*\\.(jpg|jpeg|png)$")) {
                        outExt = ".jpg";
                    } else if (!f.isDirectory() && name.endsWith(".wav")) {
                        outExt = ".wav";
                    } else {
                        outExt = ".zip";
                    }
                    tempOutputFile = File.createTempFile("compressor_out_", outExt);

                    if (!f.isDirectory() && name.matches(".*\\.(jpg|jpeg|png)$")) {
                        BufferedImage img = ImageIO.read(f);
                        BufferedImage compressed = Compressor.compressImage(img, q);
                        Compressor.saveImage(compressed, "jpg", tempOutputFile, q);
                        outputImage = compressed;
                    } else if (!f.isDirectory() && name.endsWith(".wav")) {
                        Compressor.compressAudio(f, tempOutputFile, q);
                    } else {
                        Compressor.compressToZip(f, tempOutputFile, q);
                    }
                }

                SwingUtilities.invokeLater(() -> {
                    finishTask("Compression complete!", true);
                    updateOutputDetails();
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> finishTask("Error: " + e.getMessage(), false));
            }
        }).start();
    }

    private void handleDecompression() {
        if (selectedFiles == null || selectedFiles.length == 0)
            return;
        // Validate: every selected file must be a .zip
        for (File f : selectedFiles) {
            if (!f.isFile() || !f.getName().toLowerCase().endsWith(".zip")) {
                JOptionPane.showMessageDialog(this,
                        "All selected files must be .zip archives to decompress.",
                        "Invalid Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        JFileChooser dirChooser = new JFileChooser();
        dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        dirChooser.setDialogTitle("Select Destination Directory");

        if (dirChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File dest = dirChooser.getSelectedFile();
            startTask("Decompressing " + selectedFiles.length + " file(s)...");
            File[] zipsToExtract = selectedFiles.clone();
            new Thread(() -> {
                try {
                    for (File zipFile : zipsToExtract) {
                        Decompressor.decompress(zipFile, dest);
                    }
                    SwingUtilities.invokeLater(() -> {
                        finishTask("Decompression successful!", true);
                        int count = countFiles(dest);
                        String label = zipsToExtract.length == 1
                                ? zipsToExtract[0].getName()
                                : zipsToExtract.length + " ZIP files";
                        outputInfo.setText(String.format(
                                "<html><b>Extracted:</b> %s<br><b>To:</b><br>%s<br><b>Files:</b> %d file(s)</html>",
                                label, dest.getAbsolutePath(), count));
                        outputPreview.setIcon(null);
                        outputPreview.setText("\u2705 Extracted Successfully");
                    });
                } catch (SecurityException e) {
                    SwingUtilities.invokeLater(
                            () -> finishTask("Security error (Zip Slip blocked): " + e.getMessage(), false));
                } catch (IOException e) {
                    SwingUtilities.invokeLater(() -> finishTask("Decompression failed: " + e.getMessage(), false));
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> finishTask("Unexpected error: " + e.getMessage(), false));
                }
            }).start();
        }
    }

    private int countFiles(File dir) {
        int count = 0;
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isFile())
                    count++;
                else if (f.isDirectory())
                    count += countFiles(f);
            }
        }
        return count;
    }

    private void updateOutputDetails() {
        if (tempOutputFile == null)
            return;
        long size = tempOutputFile.length();
        long origSize = 0;
        for (File f : selectedFiles)
            origSize += f.isDirectory() ? getFolderSize(f) : f.length();
        double ratio = origSize > 0 ? (origSize - size) * 100.0 / origSize : 0;

        outputInfo.setText(String.format("<html><b>Compressed Size:</b> %.2f KB<br><b>Reduction:</b> %.1f%%</html>",
                size / 1024.0, ratio));

        if (outputImage != null) {
            outputPreview.setIcon(
                    new ImageIcon(getScaledImage(outputImage, outputPreview.getWidth(), outputPreview.getHeight())));
            outputPreview.setText("");
        } else {
            outputPreview.setText("Compression Complete");
        }
        saveBtn.setEnabled(true);
    }

    private void handleSave() {
        if (tempOutputFile == null)
            return;
        JFileChooser chooser = new JFileChooser();
        String suggestedName = (selectedFiles != null && selectedFiles.length == 1)
                ? "compressed_" + selectedFiles[0].getName()
                : "compressed_files.zip";
        chooser.setSelectedFile(new File(suggestedName));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                java.nio.file.Files.copy(tempOutputFile.toPath(), chooser.getSelectedFile().toPath(),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                JOptionPane.showMessageDialog(this, "File saved successfully!");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Save failed: " + e.getMessage());
            }
        }
    }

    private void resetApp() {
        selectedFiles = null;
        tempOutputFile = null;
        originalImage = null;
        outputImage = null;
        originalPreview.setIcon(null);
        originalPreview.setText("Original File Preview");
        outputPreview.setIcon(null);
        outputPreview.setText("Output Preview");
        originalInfo.setText("No file selected");
        outputInfo.setText("Waiting for action...");
        statusLabel.setText("");
        statusLabel.setForeground(currentTheme.text);
        compressBtn.setEnabled(false);
        decompressBtn.setEnabled(false);
        saveBtn.setEnabled(false);
        progressBar.setVisible(false);
    }

    private void toggleTheme() {
        isDarkMode = !isDarkMode;
        currentTheme = isDarkMode ? DARK_THEME : LIGHT_THEME;
        themeToggleBtn.setText(isDarkMode ? "Light Mode" : "Dark Mode");
        themeToggleBtn.setIconString(isDarkMode ? "☀️" : "🌙");
        updateTheme();
    }

    private void updateTheme() {
        getContentPane().setBackground(currentTheme.bg);
        mainContent.setBackground(currentTheme.bg);

        // Update labels
        for (Component c : getAllComponents(this)) {
            if (c instanceof JLabel) {
                c.setForeground(currentTheme.text);
            }
            if (c instanceof JPanel && c != mainContent && c.getParent() instanceof JPanel) {
                // Style content cards
                if (((JPanel) c).getBackground().equals(Color.WHITE)
                        || ((JPanel) c).getBackground().equals(DARK_THEME.panel)) {
                    c.setBackground(currentTheme.panel);
                    ((JPanel) c).setBorder(new CompoundBorder(
                            new LineBorder(currentTheme.border, 1, true),
                            new EmptyBorder(15, 15, 15, 15)));
                }
            }
        }

        statusLabel.setForeground(currentTheme.text);
        qualityLabel.setForeground(currentTheme.text);

        SwingUtilities.updateComponentTreeUI(this);
    }

    private java.util.List<Component> getAllComponents(Container c) {
        java.util.List<Component> comps = new java.util.ArrayList<>();
        for (Component comp : c.getComponents()) {
            comps.add(comp);
            if (comp instanceof Container)
                comps.addAll(getAllComponents((Container) comp));
        }
        return comps;
    }

    private void startTask(String msg) {
        statusLabel.setText(msg);
        statusLabel.setForeground(currentTheme.accent);
        progressBar.setIndeterminate(true);
        progressBar.setVisible(true);
        toggleControls(false);
    }

    private void finishTask(String msg, boolean success) {
        statusLabel.setText(msg);
        statusLabel.setForeground(success ? currentTheme.success : currentTheme.danger);
        progressBar.setIndeterminate(false);
        progressBar.setValue(100);
        toggleControls(true);
    }

    private void toggleControls(boolean enabled) {
        selectBtn.setEnabled(enabled);
        boolean hasFiles = selectedFiles != null && selectedFiles.length > 0;
        boolean allZips = hasFiles && java.util.Arrays.stream(selectedFiles)
                .allMatch(f -> f.isFile() && f.getName().toLowerCase().endsWith(".zip"));
        compressBtn.setEnabled(enabled && hasFiles);
        decompressBtn.setEnabled(enabled && allZips);
        resetBtn.setEnabled(enabled);
        saveBtn.setEnabled(enabled && tempOutputFile != null);
    }

    private Image getScaledImage(BufferedImage srcImg, int w, int h) {
        if (w <= 0 || h <= 0)
            return srcImg;
        double ratio = Math.min((double) w / srcImg.getWidth(), (double) h / srcImg.getHeight());
        int newWidth = Math.max(1, (int) (srcImg.getWidth() * ratio));
        int newHeight = Math.max(1, (int) (srcImg.getHeight() * ratio));
        return srcImg.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
    }

    // --- Custom Components ---

    class ModernButton extends JButton {
        private String iconStr;
        private Color color = currentTheme.accent;

        public ModernButton(String text, String iconStr) {
            super(text);
            this.iconStr = iconStr;
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setFont(new Font("Inter", Font.BOLD, 14));
            setForeground(Color.WHITE);
        }

        public void setIconString(String icon) {
            this.iconStr = icon;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color bg = isEnabled()
                    ? (getModel().isPressed() ? color.darker() : (getModel().isRollover() ? color.brighter() : color))
                    : Color.GRAY;

            g2.setColor(bg);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15));

            g2.setColor(getForeground());
            FontMetrics fm = g2.getFontMetrics();
            String fullText = iconStr + " " + getText();
            int x = (getWidth() - fm.stringWidth(fullText)) / 2;
            int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
            g2.drawString(fullText, x, y);
            g2.dispose();
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(160, 45);
        }
    }

    class DashedBorder extends AbstractBorder {
        private Color color;

        public DashedBorder(Color color) {
            this.color = color;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(color);
            float[] dash = { 5.0f };
            g2.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
            g2.drawRect(x + 1, y + 1, width - 3, height - 3);
            g2.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}
