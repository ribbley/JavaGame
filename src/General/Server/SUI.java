package General.Server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

public class SUI extends JFrame{
	Gameserver server;
	String cmd;
	
	/**
	 * Server User Interface - pops up a window with a textfield, in which user can enter server commands.
	 * @param server the running gameserver
	 * @throws IOException
	 */
	public SUI(final Gameserver server) throws IOException{
		this.server = server;
		JPanel mainpanel = new JPanel();
		JTextField tf = new JTextField(30);
		tf.setToolTipText("Enter a servercommand");
		tf.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent e) {
				cmd = ((JTextField) e.getSource()).getText();
				}
		});
		mainpanel.add(tf);
		JButton but = new JButton("SEND");
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					if(cmd.charAt(0)=='/'){
						server.servercommand(cmd);
					}
					server.broadcast(cmd,"SERVER");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		mainpanel.add(but);
		this.add(mainpanel);
		this.setLocation(700,500);
		this.pack();
		this.setVisible(true);
		
		
	
	}
	
}
