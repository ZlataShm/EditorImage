package editor;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageEditor extends JFrame {

    private BufferedImage image;
    private final JLabel ImageLabel;
    private javax.swing.JFrame JFrame;

    public ImageEditor() {
        super("Image Editor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Создание кнопок меню
        JButton loadButton = new JButton("Load Image");
        JButton webcamButton = new JButton("Capture from Webcam");
        JButton redChannelButton = new JButton("Show Red Channel");
        JButton greenChannelButton = new JButton("Show Green Channel");
        JButton blueChannelButton = new JButton("Show Blue Channel");
        JButton cropButton = new JButton("Crop Image");
        JButton borderButton = new JButton("Add Border");
        JButton drawLineButton = new JButton("Draw Line");

        // Создание панели для кнопок и установка горизонтального расположения
        JPanel buttonPanel = new JPanel(new GridLayout(1, 0));
        buttonPanel.add(loadButton);
        buttonPanel.add(webcamButton);
        buttonPanel.add(redChannelButton);
        buttonPanel.add(greenChannelButton);
        buttonPanel.add(blueChannelButton);
        buttonPanel.add(cropButton);
        buttonPanel.add(borderButton);
        buttonPanel.add(drawLineButton);
        add(buttonPanel, BorderLayout.NORTH);

        // Создание JLabel для отображения изображения
        ImageLabel = new JLabel();
        ImageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Создание JScrollPane для отображения изображения с возможностью прокрутки
        JScrollPane scrollPane = new JScrollPane(ImageLabel);
        scrollPane.setPreferredSize(new Dimension(600, 400));
        add(scrollPane, BorderLayout.CENTER);

        // Добавление обработчиков событий для кнопок
        loadButton.addActionListener(e -> loadImage());
        webcamButton.addActionListener(e -> captureFromWebcam());
        redChannelButton.addActionListener(e -> showChannel(0));
        greenChannelButton.addActionListener(e -> showChannel(1));
        blueChannelButton.addActionListener(e -> showChannel(2));
        cropButton.addActionListener(e -> cropImage());
        borderButton.addActionListener(e -> addBorder());
        drawLineButton.addActionListener(e -> drawLine());

        // Установка размеров окна и его отображение
        setSize(800, 600);
        setLocationRelativeTo(null); // Центрирование окна на экране
        setVisible(true);
    }

    private void loadImage() {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                image = ImageIO.read(selectedFile);
                displayImage();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error loading image: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void captureFromWebcam() {
        // TODO: Implement webcam capture logic here
        JOptionPane.showMessageDialog(this, "Webcam capture feature is not implemented yet.",
                "Not Implemented", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showChannel(int channel) {
        if (image == null) {
            JOptionPane.showMessageDialog(this, "No image loaded.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        BufferedImage channelImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int rgb = image.getRGB(x, y);
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;

                switch (channel) {
                    case 0:
                        channelImage.setRGB(x, y, new Color(red, 0, 0).getRGB()); // Red channel
                        break;
                    case 1:
                        channelImage.setRGB(x, y, new Color(0, green, 0).getRGB()); // Green channel
                        break;
                    case 2:
                        channelImage.setRGB(x, y, new Color(0, 0, blue).getRGB()); // Blue channel
                        break;
                }
            }
        }
        ImageLabel.setIcon(new ImageIcon(channelImage));
    }

    private void cropImage() {
        if (image == null) {
            JOptionPane.showMessageDialog(this, "No image loaded.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String input = JOptionPane.showInputDialog(this, "Enter crop coordinates and size (x,y,width,height):");
        if (input != null && !input.isEmpty()) {
            try {
                String[] parts = input.split(",");
                int x = Integer.parseInt(parts[0]);
                int y = Integer.parseInt(parts[1]);
                int width = Integer.parseInt(parts[2]);
                int height = Integer.parseInt(parts[3]);

                image = image.getSubimage(x, y, width, height);
                displayImage();
            } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, "Invalid input. Please enter valid coordinates and size.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void addBorder() {
        if (image == null) {
            JOptionPane.showMessageDialog(this, "No image loaded.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String input = JOptionPane.showInputDialog(this, "Enter border size:");
        if (input != null && !input.isEmpty()) {
            try {
                int borderSize = Integer.parseInt(input);
                int newWidth = image.getWidth() + 2 * borderSize;
                int newHeight = image.getHeight() + 2 * borderSize;
                BufferedImage borderedImage = new BufferedImage(newWidth, newHeight, image.getType());

                Graphics2D g = borderedImage.createGraphics();
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, newWidth, newHeight);
                g.drawImage(image, borderSize, borderSize, null);
                g.dispose();

                image = borderedImage;
                displayImage();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid input. Please enter a valid border size.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void drawLine() {
        if (image == null) {
            JOptionPane.showMessageDialog(this, "No image loaded.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String input = JOptionPane.showInputDialog(this, "Enter line coordinates and thickness (x1,y1,x2,y2,thickness):");
        if (input != null && !input.isEmpty()) {
            try {
                String[] parts = input.split(",");
                int x1 = Integer.parseInt(parts[0]);
                int y1 = Integer.parseInt(parts[1]);
                int x2 = Integer.parseInt(parts[2]);
                int y2 = Integer.parseInt(parts[3]);
                int thickness = Integer.parseInt(parts[4]);
                Graphics2D g = image.createGraphics();
                g.setColor(Color.GREEN);
                g.setStroke(new BasicStroke(thickness));
                g.drawLine(x1, y1, x2, y2);
                g.dispose();
                displayImage();
            } catch ( ArrayIndexOutOfBoundsException | IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, "Invalid input. Please enter valid coordinates and thickness.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void displayImage() {
        ImageLabel.setIcon(new ImageIcon(image.getScaledInstance(ImageLabel.getWidth(), ImageLabel.getHeight(), Image.SCALE_SMOOTH)));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ImageEditor::new);
    }
}