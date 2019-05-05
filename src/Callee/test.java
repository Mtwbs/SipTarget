package Callee;
import auth.Shootmetest;



import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.net.DatagramSocket;
import java.util.Enumeration;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Port;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JOptionPane;


import jlibrtp.DataFrame;
import jlibrtp.Participant;
import jlibrtp.RTPAppIntf;
import jlibrtp.RTPSession;

public class test implements RTPAppIntf {
	
	private final int BUFFER_SIZE = 1024;
	private AudioFormat format;
	private static TargetDataLine microphone;
	private SourceDataLine speaker; // also used as headphone
	
	// for RTP
	private static RTPSession rtpSession;
	private DatagramSocket rtpSocket;
	private DatagramSocket rtcpSocket;
	private static boolean isRegistered = false;
	private boolean isReceived = false;
	
	 
	// for Callee UI
	public static PCS_UI ui;
	private static String Parti_Caller;
	//private Shootme sipstart=new Shootme();
	//private static BindingLifetimeTest getstun = new BindingLifetimeTest("163.17.21.188", 3478);
	  private static String remoteIP = "";
	  private static int remoteRtpPort = 0;
	  private static int remoteRtcpPort = 0;
	  private static int localRtpPort = 0;
	  private static int localRtcpPort = 0;
	  //private static String localIP;
	  
			  
	  public void Port()
	  {
		
		
		remoteIP = new Shootmetest().getCallerIP();
	    remoteRtpPort = Integer.valueOf(new Shootmetest().getCallerRTPport()).intValue();
	    remoteRtcpPort = Integer.valueOf(new Shootmetest().getCallerRTCPport()).intValue();
	    localRtpPort = 	new Shootmetest().getLocalRTPport();
	    localRtcpPort = new Shootmetest().getLocalRTCPport();
	    System.out.println(" Caller remote IP: " + remoteIP);
	    System.out.println(" Caller remote Rtp Port: " + remoteRtpPort);
	    System.out.println(" Caller remote Rtcp Port: " + remoteRtcpPort);
	    System.out.println(" Callee local Rtp Port: " + localRtpPort);
	    System.out.println(" Callee local Rtcp Port: " + localRtcpPort);
	  }
	  
	public void checkDeviceIsOK() {
		if(!AudioSystem.isLineSupported(Port.Info.MICROPHONE)) {
			System.out.println("Error! Please make sure that your microphone is available!");
			JOptionPane.showMessageDialog(null, "Error! Please make sure that your microphone is available!");
			System.exit(-1);
		}
		if(!AudioSystem.isLineSupported(Port.Info.SPEAKER) && !AudioSystem.isLineSupported(Port.Info.HEADPHONE)) {
			System.out.println("Error! Please make sure that your speaker or headphone is available!");
			JOptionPane.showMessageDialog(null, "Error! Please make sure that your speaker or headphone is available!");
			System.exit(-1);
		}
	}
	
	
	
	public void setAudioFormat() {
		AudioFormat.Encoding encoding = AudioFormat.Encoding.PCM_SIGNED;
        float rate = 8000.0f;
        int channels = 1;
        int sampleSize = 16;
        boolean bigEndian = false;
		
		this.format = new AudioFormat(encoding, rate, sampleSize, channels, (sampleSize / 8) * channels, rate, bigEndian);
	}
	
	public void initRecorder() {
		//TODO 3. initialize your microphone
		  try
		    {
		      microphone = AudioSystem.getTargetDataLine(this.format);
		      microphone.open();
		      microphone.start();
		    }
		    catch (LineUnavailableException e)
		    {
		      e.printStackTrace();
		      System.exit(-1);
		    }		
	}
	
	public void initPlayer() {
		try {
			speaker = AudioSystem.getSourceDataLine(this.format);
			speaker.open();
			speaker.start();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public void setCalleeUI(String title) {
		ui = new PCS_UI(title);
		
		//set the action when the window is closing
		WindowAdapter adapter = new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if(isRegistered) {
					Enumeration<Participant> list = rtpSession.getParticipants();
					while(list.hasMoreElements()) {
						Participant p = list.nextElement(); 
						rtpSession.removeParticipant(p);
					}
					rtpSession.endSession();
				}
				System.out.println("Window is closed!");
				System.exit(0);
			}
		};
		
		//set the action of button
		ActionListener listener = new ActionListener() {
			
//?
		//	private boolean isReceived;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(ui.getButtonText() == "Answer") {
									
					//new Shootme().sendInviteOK();
					
					new Shootmetest().sendInviteOK();
					
			//	new Timer().schedule(new MyTimerTask(this), 1000);
					//new MyTimerTask().run();
				/*
					String remoteIP = ui.getRemoteIP();
					int remoteRtpPort = ui.getRemoteRtpPort();
					int remoteRtcpPort = ui.getRemoteRtcpPort();
					int localRtpPort = ui.getLocalRtpPort();
					int localRtcpPort = ui.getLocalRtcpPort();
					if(remoteIP.equals("0.0.0.0") || remoteRtpPort == 0 || remoteRtcpPort == 0 || localRtpPort == 0 || localRtcpPort == 0) {
						ui.setStateText("Wrong IP and Port!");
						return;
					}
					addNewParticipant(remoteIP, remoteRtpPort, remoteRtcpPort, localRtpPort, localRtcpPort);
					startTalking();
					*/
					ui.setButtonText("End");
					ui.setStateText("Running");
				} else {
					if(isRegistered) {						
						Enumeration<Participant> list = rtpSession.getParticipants();
						while(list.hasMoreElements()) {
							Participant p = list.nextElement(); 
							rtpSession.removeParticipant(p);
						}
					//	this.isReceived = false;
						isRegistered = false;
						rtpSession.endSession();
						rtpSession = null;
					}
					ui.setButtonText("Answer");
					ui.setStateText("Stopped");
				}
			}
		};
		
		ui.setWindowListener(adapter);
		ui.setButtonText("Answer");
		ui.setButtonActionListener(listener);
	} //end setCalleeUI()
	 public void Media()
	  {
	    if ((remoteIP.equals("0.0.0.0")) || (remoteRtpPort == 0) || (remoteRtcpPort == 0) || (localRtpPort == 0) || (localRtcpPort == 0))
	    {
	      ui.setStateText("Wrong IP and Port!");
	      return;
	    }else{
	    	ui.setButtonText(remoteIP);
	    	
	    }
	    addNewParticipant(remoteIP, remoteRtpPort, remoteRtcpPort, localRtpPort, localRtcpPort);
	    startTalking();
	  }
		public void addNewParticipant(String networkAddress, int dstRtpPort, int dstRtcpPort, int srcRtpPort, int srcRtcpPort) {
			try {
				rtpSocket = new DatagramSocket(srcRtpPort);
				rtcpSocket = new DatagramSocket(srcRtcpPort);
				rtpSocket.setReuseAddress(true);
				rtcpSocket.setReuseAddress(true);
			} catch (Exception e) {
				System.out.println("RTPSession failed to obtain port");
				JOptionPane.showMessageDialog(null, "RTPSession failed to obtain port");
				System.exit(-1);
			}
			
			rtpSession = new RTPSession(rtpSocket, rtcpSocket);
			Participant p = new Participant(networkAddress, dstRtpPort, dstRtcpPort);
			rtpSession.addParticipant(p);
			rtpSession.RTPSessionRegister(this, null, null);		
			isRegistered = true;
			
			// Wait 1000 ms, because of the initial RTCP wait
			try{ Thread.sleep(1000); } catch(Exception e) {}
		}
	public void startTalking() {
		
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.println("Callee start to talk");
				byte[] data = new byte[BUFFER_SIZE];
				int packetCount = 0;
				int nBytesRead = 0;
				while (nBytesRead != -1) {
					nBytesRead = microphone.read(data, 0, data.length);
					if(!isRegistered)
						nBytesRead = -1;
					if (nBytesRead >= 0) {
						rtpSession.sendData(data);
						packetCount++;
						
						if (packetCount == 100) {
							Enumeration<Participant> iter = rtpSession.getParticipants();
							Participant p = null;
							while (iter.hasMoreElements()) {
								p = iter.nextElement();

								String name = "TEST";
								byte[] nameBytes = name.getBytes();
								String str = "abcd";
								byte[] dataBytes = str.getBytes();

								int ret = rtpSession.sendRTCPAppPacket(p.getSSRC(), 0, nameBytes, dataBytes);
								System.out.println("!!!!!!!!!!!! ADDED APPLICATION SPECIFIC "+ ret);
								continue;
							}
							if (p == null)
								System.out.println("No participant with SSRC available :(");
						}
					}
				} //end while
			}
		});
		
		thread.start();
	} //end startTalking()
	@Override
	public void receiveData(DataFrame frame, Participant participant) {
		if(speaker != null) {
			byte[] data = frame.getConcatenatedData();
			speaker.write(data, 0, data.length);
			if(!isReceived) {
				System.out.println("Received caller's data");
				isReceived = true;
			}
		}
	}
	  

	public void userEvent(int type, Participant[] participant) {
		//do nothing
	}

	public int frameSize(int payloadType) {
		return 1;
	}
	
	
	public static void main(String[] args){
		//new Shootme().sipstart();
		new Shootmetest().init();
		
		PCS_RTP_Callee obj = new PCS_RTP_Callee();

		obj.checkDeviceIsOK();	
		obj.setAudioFormat();
		obj.initRecorder();
		obj.initPlayer();
		obj.setCalleeUI("This is Callee!");
		
	}

 

} 
