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
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import MyException.FileNotFoundException;

public class ImgConv implements convertModel {

	private final static String IMAGE_FILE = "D:/dev/test.jpg";
	private static drowImage drowimage = null;
	private String image_name,threshold_name,contor_name,bgtr_name;
	private Mat src,hierarchy,invsrc;

	public static void main(String[] args) {

		ImgConv imgconv = new ImgConv();
		imgconv.binarization(0);
		imgconv.contourDetection();
		imgconv.backgroundTransparency();

		drowimage.drow();
		imgconv.post_proc();

	}

	public ImgConv() {
		image_name = IMAGE_FILE;
	}

	public ImgConv(String filename) throws FileNotFoundException{
		//argumment chack
		if(! new File(filename).exists()){
			throw new FileNotFoundException();
		}
		image_name = filename;
	}

	@Override
	public Mat binarization(int threshold) {

		//Backup base image file
		try {
			Files.copy(Paths.get(image_name), Paths.get("./copy.tmp"), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
		//Load OpenCV Module
		System.loadLibrary("opencv_java2413");
		//Create GUI
		drowimage = new drowImage();
		drowimage.addImage(image_name);
		//Load base image file
		src = Highgui.imread(image_name, 0);
		invsrc = src.clone();
		Core.bitwise_not(src, invsrc);

		if (threshold == 0) {
			Imgproc.threshold(src, invsrc, 0, 255, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);
		} else {
			Imgproc.threshold(src, invsrc, threshold, 255, Imgproc.THRESH_BINARY);
		}

		threshold_name = image_name.substring(0,image_name.length()-4) + "_threshold.jpg";
		Highgui.imwrite(threshold_name, invsrc);
		drowimage.addImage(threshold_name);

		return invsrc;
	}

	@Override
	public Mat contourDetection() {
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

		//2値化されていない場合は2値化を実施
		if(invsrc == null){
			this.binarization(0);
		}

		//Contor detection
		hierarchy = Mat.zeros(new Size(5, 5), CvType.CV_8UC1);
		Imgproc.findContours(invsrc, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_TC89_L1);
		Mat dst = Mat.zeros(new Size(src.width(), src.height()), CvType.CV_8UC3);
		Scalar color = new Scalar(255, 255, 255);

		//drow contor line
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
			Core.circle(dst, bbox.center, 5, color, -1);
			color = new Scalar(0, 255, 0);
			Core.rectangle(dst, box.tl(), box.br(), color, 2);

		}

		//Drow image
		contor_name = image_name.substring(0,image_name.length()-4) + "_contor.jpg";
		Highgui.imwrite(contor_name, dst);
		drowimage.addImage(contor_name);

		return dst;
	}

	@Override
	public Mat backgroundTransparency() {
		//binarization
		Mat bin_image = this.binarization(0);

		//create alpha matrix
		Mat base_image = Highgui.imread(image_name,-1);
		Mat alpha_image = new Mat(bin_image.size(),CvType.CV_8UC3);
		Imgproc.cvtColor(base_image, alpha_image, 0);

		//black backgroud transparency
		double[] data ;
		for(int i = 0; i < bin_image.rows(); ++i){
			for(int j = 0; j < bin_image.cols(); ++j){
				data = alpha_image.get(i, j);
				if( bin_image.get(i, j)[0] == 255 ){
					data[3] = 0;
				}
				alpha_image.put(i, j, data);
			}
		}

		//Overlay alpha matrix above base image

		//white backgroud transparency

		bgtr_name = image_name.substring(0,image_name.length()-4) + "_bgtr.png";
		Highgui.imwrite(bgtr_name, alpha_image);
		drowimage.addImage(bgtr_name);

		return alpha_image;
	}

	@Override
	public void post_proc() {
		//Restore base image file
		try {
			Files.copy(Paths.get("./copy.tmp"), Paths.get(image_name), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
		//Delete temporary file
		new File(threshold_name).delete();
		new File(contor_name).delete();
		//new File(bgtr_name).delete();
	}

}