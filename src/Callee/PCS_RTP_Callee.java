package Callee;



import auth.Shootmetest;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Port;
import javax.sound.sampled.Port.Info;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JOptionPane;


import jlibrtp.DataFrame;
import jlibrtp.Participant;
import jlibrtp.RTPAppIntf;
import jlibrtp.RTPSession;

public class PCS_RTP_Callee
  implements RTPAppIntf
{
 // private static BindingLifetimeTest getstun = new BindingLifetimeTest("163.17.21.90", 3478);
  private static String remoteIP = "";
  private static int remoteRtpPort = 0;
  private static int remoteRtcpPort = 0;
  private static int localRtpPort = 0;
  private static int localRtcpPort = 0;
  private static String localIP;
  
  
  
  private void UDPping(int srcPORT, int dstPORT)
    throws IOException
  {
    DatagramSocket clientSocket = new DatagramSocket(new InetSocketAddress(srcPORT));
    InetAddress IPAddress = InetAddress.getByName(remoteIP);
    
    byte[] sendData = new byte[1024];
    
    String sentence = "UDP ping";
    sendData = sentence.getBytes();
    
    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, dstPORT);
    clientSocket.send(sendPacket);
    
    clientSocket.close();
  }
  
  private Shootmetest test = new Shootmetest();
  
  public void Port()
  {
    remoteIP = this.test.getCallerIP();
    remoteRtpPort = Integer.valueOf(this.test.getCallerRTPport()).intValue();
    remoteRtcpPort = Integer.valueOf(this.test.getCallerRTCPport()).intValue();
    localRtpPort = this.test.getLocalRTPport();
    localRtcpPort = this.test.getLocalRTCPport();
    System.out.println(" Caller remote IP: " + remoteIP);
    System.out.println(" Caller remote Rtp Port: " + remoteRtpPort);
    System.out.println(" Caller remote Rtcp Port: " + remoteRtcpPort);
    System.out.println(" Callee local Rtp Port: " + localRtpPort);
    System.out.println(" Callee local Rtcp Port: " + localRtcpPort);
  }
  
  private final int BUFFER_SIZE = 1024;
  private AudioFormat format;
  private static TargetDataLine microphone;
  private static SourceDataLine speaker;
  private static RTPSession rtpSession;
  private DatagramSocket rtpSocket;
  private DatagramSocket rtcpSocket;
  private static boolean isRegistered = false;
  private boolean isReceived = false;
  public static PCS_UI ui;
  private static String Parti_Caller;
  
  public void checkDeviceIsOK()
  {
    if (!AudioSystem.isLineSupported(Port.Info.MICROPHONE))
    {
      System.out.println("Error! Please make sure that your microphone is available!");
      JOptionPane.showMessageDialog(null, "Error! Please make sure that your microphone is available!");
      System.exit(-1);
    }
    if ((!AudioSystem.isLineSupported(Port.Info.SPEAKER)) && (!AudioSystem.isLineSupported(Port.Info.HEADPHONE)))
    {
      System.out.println("Error! Please make sure that your speaker or headphone is available!");
      JOptionPane.showMessageDialog(null, "Error! Please make sure that your speaker or headphone is available!");
      System.exit(-1);
    }
  }
  
  public void setAudioFormat()
  {
    AudioFormat.Encoding encoding = AudioFormat.Encoding.PCM_SIGNED;
    float rate = 8000.0F;
    int channels = 1;
    int sampleSize = 16;
    boolean bigEndian = false;
    
    this.format = new AudioFormat(encoding, rate, sampleSize, channels, sampleSize / 8 * channels, rate, bigEndian);
  }
  
  public void initRecorder()
  {
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
  
  public void initPlayer()
  {
    try
    {
      speaker = AudioSystem.getSourceDataLine(this.format);
      speaker.open();
      speaker.start();
    }
    catch (LineUnavailableException e)
    {
      e.printStackTrace();
      System.exit(-1);
    }
  }
  
  public void setCalleeUI(String title)
  {
    ui = new PCS_UI(title);
    

    WindowAdapter adapter = new WindowAdapter()
    {
      public void windowClosing(WindowEvent e)
      {
        if (PCS_RTP_Callee.isRegistered)
        {
          Enumeration<Participant> list = PCS_RTP_Callee.rtpSession.getParticipants();
          while (list.hasMoreElements())
          {
            Participant p = (Participant)list.nextElement();
            PCS_RTP_Callee.rtpSession.removeParticipant(p);
          }
          PCS_RTP_Callee.rtpSession.endSession();
        }
        System.out.println("Window is closed!");
        System.exit(0);
      }
    };
    ActionListener listener = new ActionListener()
    {
      public void actionPerformed(ActionEvent arg0)
      {
        if (PCS_RTP_Callee.ui.getButtonText() == "Answer"){
        	
        	PCS_RTP_Callee.this.test.sendInviteOK();
        	PCS_RTP_Callee.ui.setButtonText("End");
        	PCS_RTP_Callee.ui.setStateText("Talking...");
        }else{
               PCS_RTP_Callee.this.test.SendBye();
               PCS_RTP_Callee.this.EndSession();
             }
        }

    };
    ActionListener listener_Transfer = new ActionListener()
    {
      public void actionPerformed(ActionEvent arg0)
      {
    	  PCS_RTP_Callee.this.test.SendRefer();
      }
    };
    ui.setWindowListener(adapter);
    ui.setButtonText("Answer");
    ui.setButtonActionListener(listener);
    ui.setButton_TransferActionListener(listener_Transfer);
  }
  
  public void EndSession()
  {
    if (isRegistered)
    {
      Enumeration<Participant> list = rtpSession.getParticipants();
      while (list.hasMoreElements())
      {
        Participant p = (Participant)list.nextElement();
        rtpSession.removeParticipant(p);
      }
      this.isReceived = false;
      isRegistered = false;
      rtpSession.endSession();
      rtpSession = null;
    }
    ui.setButtonText("Answer");
    ui.setStateText("Stopped");
  }
  
  public void addNewParticipant(String networkAddress, int dstRtpPort, int dstRtcpPort, int srcRtpPort, int srcRtcpPort)
  {
 
    try
    {
      this.rtpSocket = new DatagramSocket(srcRtpPort);
      this.rtcpSocket = new DatagramSocket(srcRtcpPort);
      this.rtpSocket.setReuseAddress(true);
      this.rtcpSocket.setReuseAddress(true);
    }
    catch (Exception e)
    {
      System.out.println("RTPSession failed to obtain port");
      JOptionPane.showMessageDialog(null, "RTPSession failed to obtain port");
      System.exit(-1);
    }
    rtpSession = new RTPSession(this.rtpSocket, this.rtcpSocket);
    Participant p = new Participant(networkAddress, dstRtpPort, dstRtcpPort);
    rtpSession.addParticipant(p);
    rtpSession.RTPSessionRegister(this, null, null);
    Parti_Caller = p.toString();
    isRegistered = true;
    try
    {
      Thread.sleep(1000L);
    }
    catch (Exception localException1) {}
  }
  
  public void startTalking()
  {
    Thread thread = new Thread(new Runnable()
    {
      public void run()
      {
        System.out.println("Callee start to talk");
        byte[] data = new byte[1024];
        int packetCount = 0;
        int nBytesRead = 0;
        while (nBytesRead != -1)
        {
          nBytesRead = PCS_RTP_Callee.microphone.read(data, 0, data.length);
          if (!PCS_RTP_Callee.isRegistered) {
            nBytesRead = -1;
          }
          if (nBytesRead >= 0)
          {
            PCS_RTP_Callee.rtpSession.sendData(data);
            packetCount++;
            if (packetCount == 100)
            {
              Enumeration<Participant> iter = PCS_RTP_Callee.rtpSession.getParticipants();
              Participant p = null;
              while (iter.hasMoreElements())
              {
                p = (Participant)iter.nextElement();
                
                String name = "TEST";
                byte[] nameBytes = name.getBytes();
                String str = "abcd";
                byte[] dataBytes = str.getBytes();
                
                int ret = PCS_RTP_Callee.rtpSession.sendRTCPAppPacket(p.getSSRC(), 0, nameBytes, dataBytes);
                System.out.println("!!!!!!!!!!!! ADDED APPLICATION SPECIFIC " + ret);
              }
              if (p == null) {
                System.out.println("No participant with SSRC available :(");
              }
            }
          }
        }
      }
    });
    thread.start();
  }
  
  public void receiveData(DataFrame frame, Participant participant)
  {
    if ((participant.toString().equals(Parti_Caller)) && (isRegistered))
    {
      byte[] data = frame.getConcatenatedData();
      
      speaker.write(data, 0, data.length);
      if (!this.isReceived)
      {
        System.out.println("Received caller's data");
        this.isReceived = true;
      }
    }
  }
  
  public void userEvent(int type, Participant[] participant) {}
  
  public int frameSize(int payloadType)
  {
    return 1;
  }
  
  public void Media()
  {
    if ((remoteIP.equals("0.0.0.0")) || (remoteRtpPort == 0) || (remoteRtcpPort == 0) || (localRtpPort == 0) || (localRtcpPort == 0))
    {
      ui.setStateText("Wrong IP and Port!");
      return;
    }
    addNewParticipant(remoteIP, remoteRtpPort, remoteRtcpPort, localRtpPort, localRtcpPort);
    startTalking();
  }
  
  public static void main(String[] args)
  {
	new Shootmetest().init();
    PCS_RTP_Callee obj = new PCS_RTP_Callee();
    
    obj.checkDeviceIsOK();
    obj.setAudioFormat();
    obj.initRecorder();
    obj.initPlayer();
    obj.setCalleeUI("Target");
  }
}
