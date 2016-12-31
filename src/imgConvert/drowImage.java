package imgConvert;

import java.awt.FlowLayout;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class drowImage implements drowModel{
	private JFrame mainframe;
	private JPanel mainpanel;

	private final static int WIDTH = 700;
	private final static int HEIGHT = 420;


	public drowImage(){
		mainframe = new JFrame("ImageConvertion");
		mainframe.setSize(WIDTH, HEIGHT);
		mainframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainpanel = new JPanel();
		mainpanel.setLayout(new FlowLayout());
		mainframe.add(mainpanel);
	}

	@Override
	public void addImage(String imageFile) {
		ImageIcon icon = new ImageIcon(imageFile);
		JLabel label = new JLabel(icon);

		mainpanel.add(label);
	}

	@Override
	public void drow() {
		mainframe.setVisible(true);
	}


}
