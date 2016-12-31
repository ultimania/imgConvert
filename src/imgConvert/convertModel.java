package imgConvert;

import org.opencv.core.Mat;

public interface convertModel {

	/**
	 *
	 * @param threshold for binarization , if 0 auto binarization
	 * @return binarization Matrix
	 */
	public Mat binarization(int threshold);

	/**
	 *
	 * @return contourDetection Matrix
	 */
	public Mat contourDetection();

	/**
	 *
	 * @return background transparency matrix
	 */
	public Mat backgroundTransparency();

	/**
	 *
	 */
	public void post_proc();
}
