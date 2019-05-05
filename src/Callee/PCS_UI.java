package Callee;
/**
 * PCS RTP Project
 * Copyright (C) 2013 QQting ^_<b
 * Wireless Mobile Networking Laboratory
 * National Tsing Hua University, Taiwan
 */


import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class PCS_UI {
	
	// for Java UI
	public final int width = 400;
	public final int height = 300;
	
	private JFrame frmCallee;
	
	private JLabel remoteIpLabel;
	private JLabel remoteRtpLabel;
	private JLabel remoteRtcpLabel;
	private JLabel localRtpLabel;
	private JLabel localRtcpLabel;
	
	private JTextField remoteIpText;
	private JTextField remoteRtpText;
	private JTextField remoteRtcpText;
	private JTextField localRtpText;
	private JTextField localRtcpText;
	

	private JButton btnAnswer;
	private JButton btnCancel;
	private JButton btnTransfer;
	
	// PCS_UI constructor
	public PCS_UI(String title) {
		frmCallee = new JFrame();
		
		remoteIpLabel = new JLabel("Remote  IP");
		remoteRtpLabel = new JLabel("Remote  RTP  port");
		remoteRtcpLabel = new JLabel("Remote  RTCP  port");
		localRtpLabel = new JLabel("Local  RTP  port");;
		localRtcpLabel = new JLabel("Local  RTCP  port");;
		
		remoteIpText = new JTextField("0.0.0.0");
		remoteRtpText = new JTextField("0");
		remoteRtcpText = new JTextField("0");
		localRtpText = new JTextField("0");
		localRtcpText = new JTextField("0");
		
		btnAnswer = new JButton("Answer");
		btnCancel = new JButton("Cancel");
		btnTransfer = new JButton("Transfer");
		
		frmCallee.setLocationRelativeTo(null);
		frmCallee.setTitle(title);
		initUI();
	}
	
	public void initUI() {
		remoteIpLabel.setHorizontalAlignment(JLabel.CENTER);
		remoteRtpLabel.setHorizontalAlignment(JLabel.CENTER);
		remoteRtcpLabel.setHorizontalAlignment(JLabel.CENTER);
		localRtpLabel.setHorizontalAlignment(JLabel.CENTER);
		localRtcpLabel.setHorizontalAlignment(JLabel.CENTER);
		btnCancel.setHorizontalAlignment(JLabel.CENTER);
		remoteIpText.setHorizontalAlignment(JTextField.CENTER);
		remoteRtpText.setHorizontalAlignment(JTextField.CENTER);
		remoteRtcpText.setHorizontalAlignment(JTextField.CENTER);
		localRtpText.setHorizontalAlignment(JTextField.CENTER);
		localRtcpText.setHorizontalAlignment(JTextField.CENTER);
		
		frmCallee.getContentPane().setLayout(new GridLayout(7, 2));
		frmCallee.getContentPane().add(remoteIpLabel);
		frmCallee.getContentPane().add(remoteIpText);
		frmCallee.getContentPane().add(remoteRtpLabel);
		frmCallee.getContentPane().add(remoteRtpText);
		frmCallee.getContentPane().add(remoteRtcpLabel);
		frmCallee.getContentPane().add(remoteRtcpText);
		frmCallee.getContentPane().add(localRtpLabel);
		frmCallee.getContentPane().add(localRtpText);
		frmCallee.getContentPane().add(localRtcpLabel);
		frmCallee.getContentPane().add(localRtcpText);
		frmCallee.getContentPane().add(btnAnswer);
		frmCallee.getContentPane().add(btnCancel);
		frmCallee.getContentPane().add(btnTransfer);

		frmCallee.setSize(width, height);
		frmCallee.setVisible(true);
	} // end setUI()

	public void setWindowLocation(int x, int y) {
		frmCallee.setLocation(x, y);
	}
	public Point getWindowLocation() {
		return frmCallee.getLocation();
	}
	
	public void setWindowListener(WindowAdapter adapter) {
		frmCallee.addWindowListener(adapter);
	}

	public void setButtonActionListener(ActionListener listener) {
		btnAnswer.addActionListener(listener);
	}

	public String getButtonText() {
		return btnAnswer.getText();
	}
	
	public void setButtonText(String text) {
		btnAnswer.setText(text);
	}
	
	
	
	public String getRemoteIP() {
		return remoteIpText.getText();
	}
	
	public int getRemoteRtpPort() {
		return Integer.parseInt(remoteRtpText.getText());
	}
	
	public int getRemoteRtcpPort() {
		return Integer.parseInt(remoteRtcpText.getText());
	}
	
	public int getLocalRtpPort() {
		return Integer.parseInt(localRtpText.getText());
	}
	
	public int getLocalRtcpPort() {
		return Integer.parseInt(localRtcpText.getText());
	}
	
	public void setStateText(String text) {
		btnCancel.setText(text);
	}
	 public void setButton_TransferActionListener(ActionListener listener)
	  {
	    this.btnTransfer.addActionListener(listener);
	  }
	
}
