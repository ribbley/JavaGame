package General.UI;

import General.WindowClosingAdapter;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import General.Game;

public class LoginForm{

	String Usernickname ="Player";
	String serverip = "localhost";
	JFrame ConnectionGUI;
	JCheckBox checkOpenGL;
	boolean openGL=false;
	boolean restartrequired=false;
	
	public LoginForm(String us,String ip,boolean openGL){
		this.openGL=openGL;
		ConnectionGUI = new JFrame();
		ConnectionGUI.addWindowListener(new WindowClosingAdapter(null));
		ConnectionGUI.setLayout(new GridLayout(4, 1));
		ActionListener al = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
					storeInformation();
			}	
		};
		JLabel nameLabel = new JLabel("Nickname:",SwingConstants.LEFT);
		JLabel serverLabel = new JLabel("Server-IP:");
		ConnectionGUI.add(nameLabel);
		JTextField tf = new JTextField(30);
		tf.setToolTipText("Enter your nickname.");
		if(us==null || us.equalsIgnoreCase(""))
		tf.setText("Player");
		else
			tf.setText(us);
		tf.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent e) {
				Usernickname = ((JTextField) e.getSource()).getText();
				}
		});
		tf.addActionListener(al);
		ConnectionGUI.add(tf);
		ConnectionGUI.add(serverLabel);
		JTextField tf2 = new JTextField(30);
		if(ip==null || ip.equalsIgnoreCase(""))
		tf2.setText("localhost"); //TODO erleichtert Programmstart, später entfernen!
		else
			tf2.setText(ip);
		tf2.setToolTipText("Enter the server-ip you wish to connect to.");
		tf2.addCaretListener(new CaretListener(){
			public void caretUpdate(CaretEvent e){
				serverip = ((JTextField) e.getSource()).getText();
			}
			
		});
		tf2.addActionListener(al);
		ConnectionGUI.add(tf2);
		//ConnectionGUI.setBorder(BorderFactory.createEtchedBorder());
		JButton ok = new JButton("Connect");
		ok.addActionListener(al);
		checkOpenGL = new JCheckBox("Force OpenGL hardware acceleration.");
		checkOpenGL.setMnemonic(KeyEvent.VK_G);
		if(openGL)
			checkOpenGL.setSelected(true);
		else
			checkOpenGL.setSelected(false);
		ConnectionGUI.add(checkOpenGL);
		ConnectionGUI.add(ok);
		ConnectionGUI.setLocation(300,300);
		ConnectionGUI.pack();
		ConnectionGUI.setVisible(true);
	}
	private void storeInformation(){
		if(checkOpenGL.isSelected()!=openGL){
			this.restartrequired=true;
		}
		if(checkOpenGL.isSelected())
			this.openGL=true;
		else 
			this.openGL=false;
		ConnectionGUI.setVisible(false);
		ConnectionGUI.dispose();
		Game.next();
	}
	public boolean getRestartRequired(){ return this.restartrequired;}
	public String getUsernickname(){ return this.Usernickname;}
	public String getServerIP(){ return this.serverip;}
	public boolean getOpenGL(){return this.openGL;}
}
