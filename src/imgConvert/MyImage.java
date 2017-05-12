package imgConvert;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import MyException.FileNotFoundException;

/**
 * This class converts imported image files. There are four conversion methods
 * as follows. · Binarization · Contour extraction · Transparent background ·
 * Foreground extraction Read the image file to be converted when instance is
 * created. One image file is linked to one generated MyImage instance. If you
 * create an instance without specifying an image file, you must call the
 * reading method.
 *
 * @author fukaya
 */
public class MyImage implements convertModel {

    private final static String IMAGE_FILE = "D:/dev/test.jpg";
    private static MainController maincontroller = null;
    private String image_name, threshold_name, contor_name, bgtr_name, grabcut_name;
    private Mat src, hierarchy, invsrc;

    /**
     * The main method loads the opencv module, and draws the initial screen.
     *
     * @param args
     */
    public static void main(String[] args) {

        // Load OpenCV Module
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        maincontroller = new MainController();

    }

    /**
     * The constructor reads the specified image file and creates a copy for
     * conversion. If reading of an image fails, an exception is returned.
     */
    public MyImage() {
        // Backup base image file
        image_name = IMAGE_FILE;
        try {
            Files.copy(
                    Paths.get(image_name),
                    Paths.get("./copy.tmp"),
                    StandardCopyOption.REPLACE_EXISTING
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * A constructor with a String argument reads an image file with the file
     * name specified by the argument. If reading of an image file fails, an
     * exception is returned.
     *
     * @param filename Image file path used for conversion source
     * @throws FileNotFoundException It is returned when reading of the image
     * file fails.
     */
    public MyImage(String filename) throws FileNotFoundException {
        // argumment chack
        if (!new File(filename).exists()) {
            throw new FileNotFoundException();
        }
        image_name = filename;
        try {
            Files.copy(
                    Paths.get(image_name),
                    Paths.get("./copy.tmp"),
                    StandardCopyOption.REPLACE_EXISTING
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method performs binarization conversion on the imported image file.
     * Conversion is performed based on the threshold value specified as an
     * argument, but when the threshold value is 0, the threshold is
     * automatically detected.
     *
     * @param threshold threshold value for binarization conversion
     * @return binarized image of Mat type
     */
    @Override
    public Mat binarization(int threshold) {

        // Load base image file
        src = Imgcodecs.imread(image_name, 0);
        invsrc = src.clone();
        Core.bitwise_not(src, invsrc);

        // Perform binary conversion with the specified threshold
        if (threshold == 0) {
            Imgproc.threshold(
                    src,
                    invsrc,
                    0,
                    255,
                    Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU
            );
        } else {
            Imgproc.threshold(
                    src,
                    invsrc,
                    threshold,
                    255,
                    Imgproc.THRESH_BINARY
            );
        }

        threshold_name = image_name.substring(0, image_name.length() - 4) + "_threshold.jpg";
        Imgcodecs.imwrite(threshold_name, invsrc);
        maincontroller.addImage(threshold_name);

        return invsrc;
    }

    
    
    @Override
    public Mat contourDetection() {
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

        // 2値化されていない場合は2値化を実施
        if (invsrc == null) {
            this.binarization(0);
        }

        // Contor detection
        hierarchy = Mat.zeros(new Size(5, 5), CvType.CV_8UC1);
        Imgproc.findContours(invsrc, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_TC89_L1);
        Mat dst = Mat.zeros(new Size(src.width(), src.height()), CvType.CV_8UC3);
        Scalar color = new Scalar(255, 255, 255);

        // drow contor line
        for (int i = 0; i < contours.size(); i++) {
            MatOfPoint ptmat = contours.get(i);

            // 頂点描画
            /*
			 * int k=0; for(k=0;k<ptmat.height();k++) { double[] m=ptmat.get(k,
			 * 0); vertex.x=m[0]; vertex.y=m[1]; Core.circle(dst, vertex, 2,
			 * color,-1); }
             */
            color = new Scalar(255, 0, 0);
            MatOfPoint2f ptmat2 = new MatOfPoint2f(ptmat.toArray());
            RotatedRect bbox = Imgproc.minAreaRect(ptmat2);
            Rect box = bbox.boundingRect();
            //Core.circle(dst, bbox.center, 5, color, -1);
            color = new Scalar(0, 255, 0);
            //Core.rectangle(dst, box.tl(), box.br(), color, 2);

        }

        // Drow image
        contor_name = image_name.substring(0, image_name.length() - 4) + "_contor.jpg";
        Imgcodecs.imwrite(contor_name, dst);
        maincontroller.addImage(contor_name);

        return dst;
    }

    @Override
    public Mat backgroundTransparency() {
        // binarization
        Mat bin_image = this.binarization(0);

        // create alpha matrix
        Mat base_image = Imgcodecs.imread(image_name, -1);
        Mat alpha_image = new Mat(bin_image.size(), CvType.CV_8UC3);
        Imgproc.cvtColor(base_image, alpha_image, 0);

        // black backgroud transparency
        double[] data;
        for (int i = 0; i < bin_image.rows(); ++i) {
            for (int j = 0; j < bin_image.cols(); ++j) {
                data = alpha_image.get(i, j);
                if (bin_image.get(i, j)[0] == 255) {
                    data[3] = 0;
                }
                alpha_image.put(i, j, data);
            }
        }

        // Overlay alpha matrix above base image
        // white backgroud transparency
        bgtr_name = image_name.substring(0, image_name.length() - 4) + "_bgtr.png";
        Imgcodecs.imwrite(bgtr_name, alpha_image);
        maincontroller.addImage(bgtr_name);

        return alpha_image;
    }

    @Override
    public void post_proc() {
        // Restore base image file
        try {
            Files.copy(Paths.get("./copy.tmp"), Paths.get(image_name), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Delete temporary file
        new File(threshold_name).delete();
        new File(contor_name).delete();
        new File(bgtr_name).delete();
        new File(grabcut_name).delete();
    }

    @Override
    public Mat grabCut() {
        Mat im = Imgcodecs.imread(image_name); // 入力画像の取得
        Mat mask = new Mat(); // マスク画像用
        Mat bgModel = new Mat(); // 背景モデル用
        Mat fgModel = new Mat(); // 前景モデル用
        Rect rect = new Rect(20, 140, 170, 220); // 大まかな前景と背景の境目(矩形)
        Mat source = new Mat(1, 1, CvType.CV_8U, new Scalar(3));
        Imgproc.grabCut(im, mask, rect, bgModel, fgModel, 1, 0); // グラフカットで前景と背景を分離
        Core.compare(mask, source, mask, Core.CMP_EQ);
        Mat fg = new Mat(im.size(), CvType.CV_8UC1, new Scalar(0, 0, 0));
        // 前景画像用
        im.copyTo(fg, mask); // 前景画像の作成

        grabcut_name = image_name.substring(0, image_name.length() - 4) + "_grabcut.jpg";
        Imgcodecs.imwrite(grabcut_name, fg);
        maincontroller.addImage(grabcut_name);
        return fg;
    }

}
