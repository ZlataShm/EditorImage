package editor;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import javax.swing.*;

public class WebcamCapture {

    private VideoCapture capture;

    public WebcamCapture() {
        // Загрузка нативной библиотеки OpenCV
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        this.capture = new VideoCapture(0);
        if (!capture.isOpened()) {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null, "Не удалось подключиться к веб-камере.", "Ошибка", JOptionPane.ERROR_MESSAGE);
            });
        }
    }

    public Mat captureFrame() {
        Mat frame = new Mat();
        if (capture.isOpened()) {
            capture.read(frame);
        }
        return frame;
    }

    public boolean isOpened() {
        return capture.isOpened();
    }

    public void release() {
        capture.release();
    }
}