package editor;

import org.opencv.core.Mat;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Stack;

public class ImageEditor extends JFrame {

    private BufferedImage image;
    private final JLabel imageLabel;
    private final Stack<BufferedImage> imageHistory;

    public ImageEditor() {
        super("Редактор Изображений");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        imageHistory = new Stack<>();

        // Создание кнопок меню
        JButton loadButton = new JButton("Загрузить изображение");
        JButton webcamButton = new JButton("Захват с веб-камеры");
        JButton redChannelButton = new JButton("Показать красный канал");
        JButton greenChannelButton = new JButton("Показать зеленый канал");
        JButton blueChannelButton = new JButton("Показать синий канал");
        JButton cropButton = new JButton("Обрезать изображение");
        JButton borderButton = new JButton("Добавить рамку");
        JButton drawLineButton = new JButton("Нарисовать линию");
        JButton undoButton = new JButton("Отменить");

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
        buttonPanel.add(undoButton);
        add(buttonPanel, BorderLayout.NORTH);

        // Создание JLabel для отображения изображения
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Создание JScrollPane для отображения изображения с возможностью прокрутки
        JScrollPane scrollPane = new JScrollPane(imageLabel);
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
        undoButton.addActionListener(e -> undo());

        // Установка размеров окна и его отображение
        setSize(1200, 600);
        setLocationRelativeTo(null); // Центрирование окна на экране
    }

    private void loadImage() {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                image = ImageIO.read(selectedFile);
                saveImageState();
                displayImage();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Ошибка загрузки изображения: " + ex.getMessage(),
                        "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void captureFromWebcam() {
        WebcamCapture webcamCapture = new WebcamCapture();
        Mat frame = webcamCapture.captureFrame();
        if (frame == null) {
            JOptionPane.showMessageDialog(this, "Ошибка подключения к веб-камере.",
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
        } else {
            image = Utils.matToBufferedImage(frame);
            if (image == null) {
                JOptionPane.showMessageDialog(this, "Камера вернула пустой кадр.",
                        "Ошибка", JOptionPane.ERROR_MESSAGE);
            } else {
                saveImageState();
                displayImage();
            }
        }
    }

    private void showChannel(int channel) {
        if (image == null) {
            JOptionPane.showMessageDialog(this, "Изображение не загружено.", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        saveImageState();

        BufferedImage channelImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int rgb = image.getRGB(x, y);
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;

                switch (channel) {
                    case 0:
                        channelImage.setRGB(x, y, new Color(red, 0, 0).getRGB()); // Красный канал
                        break;
                    case 1:
                        channelImage.setRGB(x, y, new Color(0, green, 0).getRGB()); // Зеленый канал
                        break;
                    case 2:
                        channelImage.setRGB(x, y, new Color(0, 0, blue).getRGB()); // Синий канал
                        break;
                }
            }
        }
        image = channelImage;
        displayImage();
    }

    private void cropImage() {
        if (image == null) {
            JOptionPane.showMessageDialog(this, "Изображение не загружено.", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String input = JOptionPane.showInputDialog(this, "Введите координаты и размер обрезки (x,y,width,height):");
        if (input != null && !input.isEmpty()) {
            try {
                String[] parts = input.split(",");
                int x = Integer.parseInt(parts[0]);
                int y = Integer.parseInt(parts[1]);
                int width = Integer.parseInt(parts[2]);
                int height = Integer.parseInt(parts[3]);

                saveImageState();
                image = image.getSubimage(x, y, width, height);
                displayImage();
            } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, "Неверный ввод. Пожалуйста, введите корректные координаты и размер.",
                        "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void addBorder() {
        if (image == null) {
            JOptionPane.showMessageDialog(this, "Изображение не загружено.", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String input = JOptionPane.showInputDialog(this, "Введите размер рамки:");
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

                saveImageState();
                image = borderedImage;
                displayImage();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Неверный ввод. Пожалуйста, введите корректный размер рамки.",
                        "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void drawLine() {
        if (image == null) {
            JOptionPane.showMessageDialog(this, "Изображение не загружено.", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String input = JOptionPane.showInputDialog(this, "Введите координаты линии и толщину (x1,y1,x2,y2,толщина):");
        if (input != null && !input.isEmpty()) {
            try {
                String[] parts = input.split(",");
                int x1 = Integer.parseInt(parts[0]);
                int y1 = Integer.parseInt(parts[1]);
                int x2 = Integer.parseInt(parts[2]);
                int y2 = Integer.parseInt(parts[3]);
                int thickness = Integer.parseInt(parts[4]);

                saveImageState();
                Graphics2D g = image.createGraphics();
                g.setColor(Color.GREEN);
                g.setStroke(new BasicStroke(thickness));
                g.drawLine(x1, y1, x2, y2);
                g.dispose();
                displayImage();
            } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, "Неверный ввод. Пожалуйста, введите корректные координаты и толщину.",
                        "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void undo() {
        if (!imageHistory.isEmpty()) {
            image = imageHistory.pop();
            displayImage();
        } else {
            JOptionPane.showMessageDialog(this, "Нет действий для отмены.", "Информация", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void saveImageState() {
        if (image != null) {
            BufferedImage copy = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
            Graphics g = copy.getGraphics();
            g.drawImage(image, 0, 0, null);
            g.dispose();
            imageHistory.push(copy);
        }
    }

    private void displayImage() {
        if (image != null) {
            ImageIcon icon = new ImageIcon(image);
            imageLabel.setIcon(icon);
            imageLabel.revalidate();
            imageLabel.repaint();
        }
    }
}
