package imgConvert;

/**
 * This interface is for drawing images to GUI parts. 
 * Please implement with GUI class .
 *
 * @author fukaya
 */
public interface DrowImp {

    /**
     * This method is a method to paste an image on a JLabel part.
     * For the argument, please specify the path of the image file to be pasted.
     * 
     * @param imageFile the path of the image file to be pasted
     */
    public void addImage(String imageFile);

    /**
     * This method performs screen redrawing.
     */    
    public void drow();

}
