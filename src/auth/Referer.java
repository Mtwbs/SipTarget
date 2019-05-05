package auth;

import gov.nist.javax.sip.header.HeaderFactoryImpl;
import gov.nist.javax.sip.header.ims.PAccessNetworkInfoHeader;
import gov.nist.javax.sip.header.ims.PAssertedServiceHeader;
import gov.nist.javax.sip.header.ims.PPreferredIdentityHeader;
import gov.nist.javax.sip.header.ims.PPreferredServiceHeader;
import gov.nist.javax.sip.header.ims.PrivacyHeader;
import gov.nist.javax.sip.header.ims.SecurityClientHeader;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Properties;
import javax.sip.ClientTransaction;
import javax.sip.Dialog;
import javax.sip.ListeningPoint;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.ServerTransaction;
import javax.sip.SipFactory;
import javax.sip.SipProvider;
import javax.sip.Transaction;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.address.URI;
import javax.sip.header.AllowHeader;
import javax.sip.header.CSeqHeader;
import javax.sip.header.CallIdHeader;
import javax.sip.header.ContactHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.Header;
import javax.sip.header.HeaderFactory;
import javax.sip.header.MaxForwardsHeader;
import javax.sip.header.ReferToHeader;
import javax.sip.header.RequireHeader;
import javax.sip.header.RouteHeader;
import javax.sip.header.SubscriptionStateHeader;
import javax.sip.header.SupportedHeader;
import javax.sip.header.ToHeader;
import javax.sip.header.ViaHeader;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;

public class Referer
{
	
	
  private static String Myaddress="163.17.21.85";
  private static int MyPort=5020;
 // private static String Transferor;
 // private static String Transferee;
  private static SipProvider sipProvider;
  private static AddressFactory addressFactory;
  private static MessageFactory messageFactory;
  private static HeaderFactory headerFactory;
  private ContactHeader contactHeader;
  private static String transport;
  private static int count;
  private ClientTransaction subscribeTid;
  private ListeningPoint listeningPoint;
 
  public void setReferer(String Transferor)
  {
    Transferor = Transferor;
    System.out.println("System.out.println(Transferor)+++++++++;"+Transferor);
    
  }
  
  public void setReferee(String Transferee)
  {
    Transferee = Transferee;
    
    System.out.println("System.out.println(Transferee)++++++++++++++;"+Transferee);
  }
  private static void usage()
  {
    System.exit(0);
  }
  
  public void processRequestForReferer(RequestEvent requestReceivedEvent)
  {
    Request request = requestReceivedEvent.getRequest();
    ServerTransaction serverTransactionId = requestReceivedEvent
      .getServerTransaction();
    String viaBranch = ((ViaHeader)request.getHeaders("Via").next()).getParameter("branch");
    if (request.getMethod().equals("NOTIFY")) {
      processNotify(requestReceivedEvent, serverTransactionId);
    }
  }
  
  public void processNotify(RequestEvent requestEvent, ServerTransaction serverTransactionId)
  {
    SipProvider provider = (SipProvider)requestEvent.getSource();
    Request notify = requestEvent.getRequest();
    try
    {
      if (serverTransactionId == null) {
        serverTransactionId = provider.getNewServerTransaction(notify);
      }
      Dialog dialog = serverTransactionId.getDialog();
      




      Response response = messageFactory.createResponse(200, notify);
      
      ContactHeader contact = (ContactHeader)this.contactHeader.clone();
      ((SipURI)contact.getAddress().getURI()).setParameter("id", "sub");
      response.addHeader(contact);
      
      serverTransactionId.sendResponse(response);
      


      SubscriptionStateHeader subscriptionState = (SubscriptionStateHeader)notify
        .getHeader("Subscription-State");
      

      String state = subscriptionState.getState();
      if (state.equalsIgnoreCase("terminated")) {
        dialog.delete();
      }
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
      
      System.exit(0);
    }
  }
  
  public void processResponseForReferer(ResponseEvent responseReceivedEvent)
  {
    Response response = responseReceivedEvent.getResponse();
    Transaction tid = responseReceivedEvent.getClientTransaction();
    if (tid == null) {}
  }
  
  public void RefererEngineStart(String Myaddress, int MyPort, SipProvider udpProvider)
  {
    try
    {
      
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    Myaddress = Myaddress;
    MyPort = MyPort;
    System.out.println("+++++++++++++"+Myaddress);
    System.out.println("----------"+MyPort);
    sipProvider = udpProvider;
  }
  
  public void sendSubscribe(String Transferor, String Transferee, String TransferTarget, String targetDialog)
  {
    try
    {
      String fromName = Transferor;
      String fromSipAddress = "open-ims.test";
      String fromDisplayName = Transferor;
      
      String toSipAddress = "open-ims.test";
      String toUser = Transferee;
      String toDisplayName = Transferee;
      
      System.out.println("System.out.println(Transferor);"+Transferor);
      System.out.println("System.out.println(Transferee);"+Transferee);
      System.out.println("System.out.pxcxcsferee);"+TransferTarget);
      System.out.println("System.out.prxcxcsa;"+targetDialog);
      SipURI fromAddress = addressFactory.createSipURI(fromName, fromSipAddress);
      
      Address fromNameAddress = addressFactory.createAddress(fromAddress);
      fromNameAddress.setDisplayName(fromDisplayName);
      FromHeader fromHeader = headerFactory.createFromHeader(
        fromNameAddress, "12345");
      

      SipURI toAddress = addressFactory.createSipURI(toUser, toSipAddress);
      Address toNameAddress = addressFactory.createAddress(toAddress);
      toNameAddress.setDisplayName(toDisplayName);
      ToHeader toHeader = headerFactory.createToHeader(toNameAddress, 
        null);
      



      URI requestURI = addressFactory.createURI("sip:" + Transferee + "@open-ims.test");
      
      System.out.println(Myaddress);
      System.out.println(MyPort);

      ArrayList viaHeaders = new ArrayList();
      ViaHeader viaHeader = headerFactory.createViaHeader(Myaddress, 
        MyPort, 
        transport, null);
      

      viaHeaders.add(viaHeader);
      

      CallIdHeader callIdHeader = sipProvider.getNewCallId();
      

      CSeqHeader cSeqHeader = headerFactory.createCSeqHeader(1L, 
        "REFER");
      

      MaxForwardsHeader maxForwards = headerFactory
        .createMaxForwardsHeader(70);
      

      Request request = messageFactory.createRequest(requestURI, 
        "REFER", callIdHeader, cSeqHeader, fromHeader, 
        toHeader, viaHeaders, maxForwards);
      
      Header contactH = headerFactory.createHeader("Contact", "<sip:" + Transferor + "@" + Myaddress + ":" + MyPort + ";transport=udp>;expires=60;+g.oma.sip-im;language=\"en,fr\";+g.3gpp.smsip;+g.oma.sip-im.large-message;audio;+g.3gpp.icsi-ref=\"urn%3Aurn-7%3A3gpp-application.ims.iari.gsma-vs\";+g.3gpp.cs-voice");
      request.addHeader(contactH);
      
      Address routeaddress = addressFactory.createAddress("sip:orig@163.17.21.88:5060;lr");
      RouteHeader routeHeader = headerFactory.createRouteHeader(routeaddress);
      request.addHeader(routeHeader);
      
      Header targetdialog = headerFactory.createHeader("Target-Dialog", targetDialog);
      request.addHeader(targetdialog);
      






      HeaderFactoryImpl headerFactoryImpl = new HeaderFactoryImpl();
      

      AllowHeader allow1 = 
        headerFactory.createAllowHeader("INVITE");
      request.addHeader(allow1);
      AllowHeader allow2 = 
        headerFactory.createAllowHeader("ACK");
      request.addHeader(allow2);
      AllowHeader allow3 = 
        headerFactory.createAllowHeader("CANCEL");
      request.addHeader(allow3);
      AllowHeader allow4 = 
        headerFactory.createAllowHeader("OPTIONS");
      request.addHeader(allow4);
      AllowHeader allow5 = 
        headerFactory.createAllowHeader("BYE");
      request.addHeader(allow5);
      AllowHeader allow6 = 
        headerFactory.createAllowHeader("REFER");
      request.addHeader(allow6);
      AllowHeader allow7 = 
        headerFactory.createAllowHeader("NOTIFY");
      request.addHeader(allow7);
      
      SupportedHeader supported1 = 
        headerFactory.createSupportedHeader("100rel");
      request.addHeader(supported1);
      SupportedHeader supported2 = 
        headerFactory.createSupportedHeader("preconditions");
      request.addHeader(supported2);
      SupportedHeader supported3 = 
        headerFactory.createSupportedHeader("path");
      request.addHeader(supported3);
      
      RequireHeader require1 = 
        headerFactory.createRequireHeader("sec-agree");
      request.addHeader(require1);
      RequireHeader require2 = 
        headerFactory.createRequireHeader("preconditions");
      request.addHeader(require2);
      RequireHeader require3 = 
        headerFactory.createRequireHeader("tdialog");
      request.addHeader(require3);
      
      SecurityClientHeader secClient = 
        headerFactoryImpl.createSecurityClientHeader();
      secClient.setSecurityMechanism("ipsec-3gpp");
      secClient.setAlgorithm("hmac-md5-96");
      secClient.setEncryptionAlgorithm("des-cbc");
      secClient.setSPIClient(10000);
      secClient.setSPIServer(10001);
      secClient.setPortClient(5063);
      secClient.setPortServer(4166);
      request.addHeader(secClient);
      
      PAccessNetworkInfoHeader accessInfo = 
        headerFactoryImpl.createPAccessNetworkInfoHeader();
      accessInfo.setAccessType("3GPP-UTRAN-TDD");
      accessInfo.setUtranCellID3GPP("0123456789ABCDEF");
      request.addHeader(accessInfo);
      
      PrivacyHeader privacy = headerFactoryImpl.createPrivacyHeader("header");
      request.addHeader(privacy);
      PrivacyHeader privacy2 = headerFactoryImpl.createPrivacyHeader("user");
      request.addHeader(privacy2);
      

      PPreferredIdentityHeader preferredID = 
        headerFactoryImpl.createPPreferredIdentityHeader(fromNameAddress);
      request.addHeader(preferredID);
      

      PPreferredServiceHeader preferredService = 
        headerFactoryImpl.createPPreferredServiceHeader();
      preferredService.setApplicationIdentifiers("3gpp-service-ims.icis.mmtel");
      request.addHeader(preferredService);
      

      PAssertedServiceHeader assertedService = 
        headerFactoryImpl.createPAssertedServiceHeader();
      assertedService.setApplicationIdentifiers("3gpp-service-ims.icis.mmtel");
      request.addHeader(assertedService);
      






      this.subscribeTid = sipProvider.getNewClientTransaction(request);
      






      ReferToHeader referTo = headerFactory.createReferToHeader(
        addressFactory.createAddress("<sip:" + TransferTarget + "@open-ims.test>"));
      
      request.addHeader(referTo);
      



      this.subscribeTid.sendRequest();
    }
    catch (Throwable ex)
    {
      ex.printStackTrace();
      usage();
    }
  }
  
  static void initFactories()
    throws Exception
  {
    transport = "udp";
    
    SipFactory sipFactory = SipFactory.getInstance();
    sipFactory.setPathName("gov.nist");
    Properties properties = new Properties();
    
    properties.setProperty("javax.sip.USE_ROUTER_FOR_ALL_URIS", "false");
    
    properties.setProperty("javax.sip.STACK_NAME", "Transferor");
    
    properties.setProperty("javax.sip.FORKABLE_EVENTS", "foo");
    



    properties.setProperty("gov.nist.javax.sip.TRACE_LEVEL", "32");
    


    headerFactory = sipFactory.createHeaderFactory();
    addressFactory = sipFactory.createAddressFactory();
    messageFactory = sipFactory.createMessageFactory();
    
    Referer subscriber = new Referer();
  }
}
