package mafioso.so.so.android_robot.bll;

import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvException;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import mafioso.so.so.android_robot.shared.Circle;

public class ImgProcessing {


    public Circle getCircle(Mat rgba, Mat hsv, Mat thresholded, Mat thresholded2, Mat array255, Mat distance) {

        array255.setTo(new Scalar(255));

        //HSV
        List<Mat> lhsv = new ArrayList<>(3);
        Mat circles = new Mat();

        // The default color format in OpenCV is often referred to as RGB but it is actually BGR (the bytes are reversed)
        // Scalar(B,G,R,A)
        Scalar hsv_min = new Scalar(0, 50, 50, 0);
        Scalar hsv_max = new Scalar(6, 255, 255, 0);
        Scalar hsv_min2 = new Scalar(175, 50, 50, 0);
        Scalar hsv_max2 = new Scalar(179, 255, 255, 0);

        // Converts RGBA to HSV
        // cvtColor(InputArray src, OutputArray dst, int code, int dstCn=0 )
        // dstCn = number of channels in the destination image; if the parameter is 0, the number of the channels is derived automatically from src and code
        Imgproc.cvtColor(rgba, hsv, Imgproc.COLOR_RGB2HSV, 0);

        // Checks if array elements lie between the elements of two other arrays
        Core.inRange(hsv, hsv_min, hsv_max, thresholded);
        Core.inRange(hsv, hsv_min2, hsv_max2, thresholded2);
        Core.bitwise_or(thresholded, thresholded2, thresholded);

        // 3 2D one channel Mats
        Core.split(hsv, lhsv);
        Mat S = lhsv.get(1);
        Mat V = lhsv.get(2);

        //Gets the inverse
        Core.subtract(array255, S, S);
        Core.subtract(array255, V, V);

        //Converts to 32 bytes
        S.convertTo(S, CvType.CV_32F);
        V.convertTo(V, CvType.CV_32F);

        //Calculates the magnitude of 2D vectors.
        // magnitude(InputArray x, InputArray y, OutputArray magnitude)
        Core.magnitude(S, V, distance);
        Core.inRange(distance, new Scalar(0.0), new Scalar(200.0), thresholded2);
        Core.bitwise_and(thresholded, thresholded2, thresholded);

        // Reduce the noise to avoid false circle detection
        // GaussianBlur(InputArray src, OutputArray dst, Size ksize, double sigmaX, double sigmaY=0, int borderType=BORDER_DEFAULT )
        Imgproc.GaussianBlur(thresholded, thresholded, new Size(9, 9), 0, 0);

        // Apply Hough Transform to find the circles
        // thresholded = input image
        // circles = a vector that stores sets of 3 values xc, yc and r for each detected circle
        // thresholded.height() / 4 = minimum distance between detected centers
        Imgproc.HoughCircles(thresholded, circles, Imgproc.CV_HOUGH_GRADIENT, 2, thresholded.height() / 4, 500, 50, 0, 0);
        int rows = circles.rows();

        // Returns 12 (3 * 4bytes in a float)
        int elemSize = (int) circles.elemSize();
        float[] data2 = new float[rows * elemSize / 4];

        if (data2.length > 0) {
            // Points to the first element and reads the whole thing into data2
            circles.get(0, 0, data2);

            Point center = new Point(data2[0], data2[0 + 1]);
            double mainAxis = (double) data2[0 + 2] * 2;
            return new Circle(center, mainAxis);
        }

        //Releasing all, to fix problem with heap space
        System.gc();
        lhsv.clear();
        S.release();
        V.release();
        circles.release();

        return null;
    }
    public Bitmap convertMatToBitmap(Mat img){
        Bitmap bmp = null;
        try {
            bmp = Bitmap.createBitmap(img.cols(), img.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(img, bmp);
        }
        catch (CvException e){
            Log.d("Exception",e.getMessage());
        }
        return bmp;
    }
}
