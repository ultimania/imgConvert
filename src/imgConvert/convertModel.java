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
	 * @return grubcut matrix
	 */
	public Mat grabCut();

	/**
	 *
	 */
	public void post_proc();
}
