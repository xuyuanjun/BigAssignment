package test;

import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import windowsSelfTest.windowsSelfTest.ShapedJFrame;

public class test {

	public static void main(String[] args){
		JFrame window = new JFrame();
		Image image = Toolkit.getDefaultToolkit().getImage("未标题-2.jpg");
		ShapedJFrame SJF = new ShapedJFrame(window, image);
		JLabel label = new JLabel();
		Icon icon = new ImageIcon("未标题-2.jpg");
		label.setIcon(icon);
		SJF.add(label);
		SJF.setVisible(true);
		JPanel jp=new JPanel();
	    jp.setBackground(Color.BLACK);
	    SJF.setContentPane(jp);
	}

}
