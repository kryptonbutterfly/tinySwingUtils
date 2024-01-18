package kryptonbutterfly.util.swing;

import java.awt.FlowLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

public class ApplyAbortPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	public JButton btnButton1;
	public JButton btnButton2;
	
	public ApplyAbortPanel(String button1, ActionListener listener1, String button2, ActionListener listener2)
	{
		setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
		
		btnButton1 = new JButton(button1);
		add(btnButton1);
		btnButton1.addActionListener(listener1);
		
		btnButton2 = new JButton(button2);
		add(btnButton2);
		btnButton2.addActionListener(listener2);
	}
}