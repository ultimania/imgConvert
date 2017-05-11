package imgConvert;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;

import MyException.FileNotFoundException;

public class drowImage implements drowModel {
	// Frame Layer
	private JFrame mainFrame;

	// base Layer
	private JPanel headerPanel;
	private JPanel mainPanel;
	private JPanel footerPanel;

	// parts Layer
	private JList convList;
	private JLabel imagePanel;
	private JTextField filepathField;
	private JButton filechooser;
	private ImageIcon icon;

	// ImgConv
	MyImage imgconv;

	private final static int WIDTH = 800;
	private final static int HEIGHT = 600;

	public drowImage() {
		this.createGUI();
		this.drow();
	}

	@Override
	public void createGUI() {
		// convList
		String initData[] = { "通常画像", "2値化", "輪郭検出", "背景透過", "前景抽出" };
		convList = new JList(initData);
		convList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int action = convList.getSelectedIndex();
				switch (action) {
				case 0:
					imgconv.binarization(0);
					break;
				}
			}
		});

		// imagePanel
		imagePanel = new JLabel();
		imagePanel.setHorizontalAlignment(JLabel.CENTER);
		// filechooser
		filechooser = new JButton("参照");
		filechooser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser filechooser = new JFileChooser();
				int selected = filechooser.showOpenDialog(mainFrame);
				if (selected == JFileChooser.APPROVE_OPTION) {
					File file = filechooser.getSelectedFile();
					String filename = file.getPath();
					filepathField.setText(filename);
					addImage(filename);

					try {
						imgconv = new MyImage(filename);
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		// filepathField
		filepathField = new JTextField();
		// mainPanel
		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(BorderLayout.CENTER, imagePanel);
		// headerPanel
		headerPanel = new JPanel();
		headerPanel.setLayout(new GridLayout(1, 2));
		headerPanel.add(filepathField);
		headerPanel.add(filechooser);
		// footerPanel
		footerPanel = new JPanel();
		footerPanel.setLayout(new GridLayout(1, 1));

		// Frame
		mainFrame = new JFrame("ImageConvertion");
		mainFrame.setSize(WIDTH, HEIGHT);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setLayout(new BorderLayout());
		mainFrame.add(BorderLayout.NORTH, headerPanel);
		mainFrame.add(BorderLayout.CENTER, mainPanel);
		mainFrame.add(BorderLayout.SOUTH, footerPanel);
		mainFrame.add(BorderLayout.WEST, convList);

	}

	@Override
	public void addImage(String imageFile) {
		ImageIcon imageicon = new ImageIcon(imageFile);
		imagePanel.setIcon(imageicon);

		this.drow();
	}

	@Override
	public void drow() {
		mainFrame.setVisible(true);
	}

}
