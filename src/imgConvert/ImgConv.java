package imgConvert;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

public class ImgConv {
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	public static void main(String[] args) {
		String path_in = "D:/dev/Lenna.png";
		String path_out = "D:/dev/Lenna_gray.png";

		Mat mat_src = new Mat();
		Mat mat_dst = new Mat();

		mat_src = Highgui.imread(path_in); // 入力画像の読み込み
		Imgproc.cvtColor(mat_src, mat_dst, Imgproc.COLOR_BGR2GRAY); // カラー画像をグレー画像に変換
		Highgui.imwrite(path_out, mat_dst); // 出力画像を保存
	}

}
