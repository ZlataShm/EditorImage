package editor;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

public class WebcamCapture {

    public Mat captureFrame() {
        VideoCapture capture = new VideoCapture(0);
        if (!capture.isOpened()) {
            return null;
        }

        Mat frame = new Mat();
        capture.read(frame);
        capture.release();
        return frame;
    }
}
