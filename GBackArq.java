
import java.applet.Applet;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
//GbackArq

public class GBackArq extends Applet implements ActionListener,Runnable{

    Globe globe = new Globe();
   
    Button newPack, stopPack, killPack, resetBtn;
    Thread GBackArq, RunThread;
    int bValue, nSeqValue, fps, onSelect=-1;
    int winLength, pckWidth, pckHeigth, hOffset,vOffset, vClearance, totalPck, tTimeOut;
    boolean flagTCheck,sleepTCheck;
    packet window[];
    String status, strCurrValues;
    Image imgSize;
    Graphics gSize;
    Dimension scrSize;
    
    
    public void init()
    {
        try
   {
    if(getParameter("winLength")!= null && getParameter("window_length").length()>0)
    {
      winLength = Integer.parseInt(getParameter("window_length").toString());
    }
    else
    {
      winLength = globe.winLengthd;
    }
    if(getParameter("packet_width")!=null && getParameter("packet_width").length()>0)
    {
      pckWidth = Integer.parseInt(getParameter("pckWidth").toString());
    }
    else
    {
      pckWidth = globe.pckWidthd;
    }
    if(getParameter("packet_height")!=null && getParameter("packet_height").length()>0)
    {
      pckHeigth = Integer.parseInt(getParameter("packet_height").toString());
    }
    else
    {
      pckHeigth = globe.pckHeigthd;
    }
    if(getParameter("horizontal_offset")!=null && getParameter("horizontal_offset").length()>0)
    {
      hOffset = Integer.parseInt(getParameter("horizontal_offset").toString());
    }
    else
    {
      hOffset = globe.hod;
    }
    if(getParameter("vertical_offset")!=null && getParameter("vertical_offset").length()>0)
    {
      vOffset = Integer.parseInt(getParameter("vertical_offset").toString());
    }
    else
    {
      vOffset = globe.vod;
    }
    
    if(getParameter("vertical_clearance")!=null && getParameter("vertical_clearance").length()>0)
    {
      vClearance = Integer.parseInt(getParameter("vertical_clearance").toString());
    }
    else
    {
      vClearance = globe.vcd;
    }
    if(getParameter("total_packets")!=null && getParameter("total_packets").length()>0)
    {
      totalPck = Integer.parseInt(getParameter("total_packets").toString());
    }
    else
    {
      totalPck = globe.tpd;
    }
    if(getParameter("timer_time_out")!=null && getParameter("timer_time_out").length()>0)
    {
      tTimeOut = Integer.parseInt(getParameter("timer_time_out").toString());
    }
    else
    {
      tTimeOut = globe.tosd;
    }
   }
   catch(Exception e) {System.out.println(e);}
  bValue = 0;     
  nSeqValue = 0;  
  fps = 5;  
  
  window = new packet[totalPck]; 
  status = "Press 'New packet' to send new packet."; 
  strCurrValues = "Base value = "+ bValue +".   Next Sequence No. = "+ nSeqValue;  
  
  newPack = new Button("New Packet"); 
  newPack.setActionCommand("rdt"); 
  newPack.addActionListener(this); 
  
  stopPack = new Button("Stop Sending"); 
  stopPack.setActionCommand("StopAnimation"); 
  stopPack.addActionListener(this); 
  
  killPack = new Button("Kill Packet"); 
  killPack.setActionCommand("kl"); 
  killPack.addActionListener(this); 
  killPack.setEnabled(false); 
  
  resetBtn = new Button("Reset"); 
  resetBtn.setActionCommand("ReSet"); 
  resetBtn.addActionListener(this); 
 
  add(newPack); 
  add(stopPack); 
  add(killPack); 
  add(resetBtn);
  setBackground(Color.ORANGE);
  JOptionPane.showMessageDialog(getParent(),"Simulation started. Press 'New Packet' to start.");
    }
    
    public void start() 
{ 
  if (GBackArq==null)
  {
   GBackArq = new Thread(this); 
   GBackArq.start();
  } 
} 
  
 public void run() 
  { 
  Thread currenthread = Thread.currentThread(); 
  while (currenthread==GBackArq)    
   if (isPacketActive(window)==true)  
    { 
    for (int i=0; i<totalPck; i++) 
     if (window[i]!= null) 
      if (window[i].packet_on_way==true) 
       if (window[i].position_of_packet < (vClearance-pckHeigth))
       {     
        window[i].position_of_packet+=5;
       } 
       else if (window[i].packet_ack=true)  
        { 
        window[i].packet_reached_dest = true; 
        if (isReachedDestination(i)==true)   
         {       
         window[i].position_of_packet = pckHeigth+5; 
         window[i].packet_ack = false; 
         status = "Packet "+i+" received. AcknowinLengthedge sent."; 
         } 
        else 
         { 
         window[i].packet_on_way = false; 
         status = "Packet "+i+" received. No acknowinLengthedge sent."; 
         if (i==onSelect) 
          { 
          onSelect = -1; 
          killPack.setEnabled(false); 
          } 
         } 
        } 
       else if (!window[i].packet_ack==true)   
        { 
        status = "Packet "+ i +" acknowinLengthedge received."; 
        window[i].packet_on_way = false; 
        for (int n=0; n<=i; n++) 
         window[n].packet_acknowinLengthedged = true; 
        if (i==onSelect) 
         { 
         onSelect = -1; 
         killPack.setEnabled(false); 
         } 
  
        RunThread = null;     
  
        if (i+winLength<totalPck) 
         bValue = i+1; 
        if (nSeqValue < bValue+winLength) newPack.setEnabled(true); 
  
        if (bValue != nSeqValue) 
         { 
         status += " Timer restarted."; 
         RunThread = new Thread(this); 
         sleepTCheck = true; 
         RunThread.start(); 
         } 
        else 
         status += " Timer stopped."; 
        } 
    strCurrValues = "Window Base = "+ bValue +".   Next Sequence No. = "+ nSeqValue; 
    repaint(); 
  
    try { 
     Thread.sleep(1000/fps); 
     } catch (InterruptedException e) 
      { 
      System.out.println("Error"+e); 
      } 
    } 
   else 
    GBackArq = null; 
  
  
  while (currenthread == RunThread)   
    if (sleepTCheck==true) 
    { 
      sleepTCheck=false; 
      try { 
        Thread.sleep(tTimeOut*1000); 
      } 
      catch (InterruptedException e) 
      { 
        System.out.println ("Timer interrupted."); 
      } 
    } 
    else 
    { 
      for (int n=bValue; n<bValue+winLength; n++) 
        if (window[n] != null) 
          if (!window[n].packet_acknowinLengthedged) 
          { 
            window[n].packet_on_way = true; 
            window[n].packet_ack = true; 
            window[n].position_of_packet = pckHeigth+5; 
          } 
        sleepTCheck = true; 
        if (GBackArq == null) 
        { 
          GBackArq = new Thread (this); 
          GBackArq.start(); 
        } 
        status = "Packets resent by timer. Timer restarted."; 
    } 
  } 
  
  
 public void actionPerformed(ActionEvent e) 
  { 
  String cmd = e.getActionCommand(); 
  
  if (cmd == "rdt" && nSeqValue < bValue+winLength)  
   { 
   window[nSeqValue] = new packet(true,pckHeigth+5); 
  
   status = "Packet "+ nSeqValue +" sent."; 
  
   if (bValue == nSeqValue) 
    {     
    status += " Timer set for packet "+ bValue +"."; 
    if (RunThread == null) 
     RunThread = new Thread(this); 
    sleepTCheck = true; 
    RunThread.start(); 
    } 
  
   repaint(); 
   nSeqValue++; 
   if (nSeqValue == bValue+winLength) 
     newPack.setEnabled(false); 
   start(); 
   }  
  
  else if (cmd == "StopAnimation") 
   { 
   GBackArq = null; 
   if (RunThread != null) 
    { 
    flagTCheck = true; 
    RunThread = null;    
    } 
   stopPack.setLabel("Resume"); 
   stopPack.setActionCommand("startanim"); 
  
 
   newPack.setEnabled(false); 
   killPack.setEnabled(false); 
  
   status = "Simulation paused."; 
   repaint(); 
   } 
  else if (cmd == "kl") 
  { 
  if (window[onSelect].packet_ack)
  {    
   status = "Packet "+ onSelect +" destroyed. Timer running for packet "+bValue+".";
   window[onSelect].packet_on_way = false; 
   killPack.setEnabled(false); 
   onSelect = -1;
  } 
  else 
  {
   status = "AcknowinLengthedgement of packet "+ onSelect +" destroyed. Timer running for packet "+bValue+".";
   window[onSelect].packet_on_way = false; 
   killPack.setEnabled(false); 
   onSelect = -1;
  } 
  repaint(); 
  } 
  else if (cmd == "startanim") 
   { 
   status = "Simulation resumed."; 
   stopPack.setLabel("Stop Animation"); 
   stopPack.setActionCommand("StopAnimation"); 
   if (flagTCheck) 
    { 
    status += " Timer running."; 
    RunThread = new Thread(this); 
    sleepTCheck = true; 
    RunThread.start(); 
    } 
   
   newPack.setEnabled(true); 
   killPack.setEnabled(true); 
   repaint(); 
  
   start(); 
   } 
  
  else if (cmd == "ReSet") 
   reset_app(); 
  }

  
 public boolean mouseDown(Event e, int x, int y) 
  { 
try
{
  int i, xpos, ypos; 
  i = (y-2*vOffset)/(pckWidth+7); 
  if (window[i]!= null) 
   { 
   ypos = hOffset+(pckWidth+7)*i; 
   xpos = window[i].position_of_packet; 
  
   if (y>=ypos && y<= ypos+pckWidth && window[i].packet_on_way) 
    { 
    if ((window[i].packet_ack && x>=vOffset+xpos && x<=vOffset+xpos+pckHeigth) || ((!window[i].packet_ack) && x>=vOffset+vClearance-xpos && x<=vOffset+vClearance-xpos+pckHeigth)) 
     { 
     status = "Packet "+ i +" selected."; 
     window[i].selected_packet = true; 
     onSelect = i; 
     killPack.setEnabled(true); 
     } 
    else 
     status = "Click on a moving packet to select."; 
    } 
   else 
    status = "Click on a moving packet to select."; 
   } 
  return true;
}
catch(Exception e1){ return false;}
  } 
 

 public void paint(Graphics graphics)    
  {
   update(graphics); 
  } 
  
  
 public void update(Graphics graphics) 
  { 

  Dimension d = getSize();
  Globe globe1 = new Globe();
        if ((gSize == null) || (d.width != scrSize.width) || (d.height != scrSize.height)) 
   { 
            scrSize = d; 
            imgSize = createImage(d.width, d.height); 
            gSize = imgSize.getGraphics(); 
   }  
        gSize.setColor(Color.LIGHT_GRAY); 
        gSize.fillRect(0, 0, d.width, d.height);  
  
  
  gSize.setColor(Color.black); 
  gSize.draw3DRect(vOffset-3, hOffset+bValue*(pckWidth+7)-4, pckHeigth-2, (winLength)*(pckWidth+7)+7 ,true); 
  
  for (int i=0; i<totalPck; i++) 
   {  
  
   if (window[i]==null) 
    { 
    gSize.setColor(Color.black); 
    gSize.draw3DRect(vOffset, hOffset+(pckWidth+7)*i, pckWidth,pckHeigth,true); 
    gSize.draw3DRect(vOffset+vClearance,hOffset+(pckWidth+7)*i, pckWidth,pckHeigth,true); 
    } 
   else 
    { 
    if (window[i].packet_acknowinLengthedged) 
     gSize.setColor(globe1.ac); 
    else 
     gSize.setColor(globe1.uac); 
    gSize.fill3DRect (vOffset,hOffset+(pckWidth+7)*i,pckWidth,pckHeigth,true);  
  
    gSize.setColor (globe1.dc); 
    if (window[i].packet_reached_dest) 
     gSize.fill3DRect (vOffset+vClearance,hOffset+(pckWidth+7)*i,pckWidth,pckHeigth,true); 
    else 
     gSize.draw3DRect (vOffset+vClearance,hOffset+(pckWidth+7)*i,pckWidth,pckHeigth,true); 
   
  
    if (window[i].packet_on_way) 
     { 
     if (i==onSelect) 
      gSize.setColor (globe1.sc); 
     else if (window[i].packet_ack) 
      gSize.setColor (globe1.rpc); 
     else 
      gSize.setColor (globe1.rac); 
  
     if (window[i].packet_ack) 
      gSize.fill3DRect (vOffset+window[i].position_of_packet,hOffset+(pckWidth+7)*i,pckWidth,pckHeigth,true); 
     else 
      gSize.fill3DRect (vOffset+vClearance-window[i].position_of_packet,hOffset+(pckWidth+7)*i,pckWidth,pckHeigth,true); 
     } 
    } 
   } 
  
   gSize.setColor(Color.black); 
   int newvOffset = vOffset+vClearance+pckHeigth; 
   int newHOffset = hOffset; 
  
   gSize.drawString(status,newHOffset,newvOffset+25); 
   gSize.drawString(strCurrValues,newHOffset,newvOffset+40); 
  
   gSize.drawString("Packet",newHOffset+15,newvOffset+80); 
   gSize.drawString("AcknowinLengthedgement",newHOffset+75,newvOffset+80); 
   gSize.drawString("Received Packet",newHOffset+195,newvOffset+80); 
   gSize.drawString("Selected",newHOffset+305,newvOffset+80); 
  
   gSize.drawString("Base = "+bValue,hOffset+(pckWidth+7)*totalPck+10,vOffset+vClearance/2); 
   gSize.drawString("NextSeq = "+nSeqValue,hOffset+(pckWidth+7)*totalPck+10,vOffset+vClearance/2+20); 
  
   gSize.setColor(Color.blue); 
   gSize.drawString("Sender",vOffset+12,hOffset+(pckWidth+7)*totalPck+20); 
   gSize.drawString("Receiver",vOffset+vClearance+12,hOffset+(pckWidth+7)*totalPck+20); 
  
   gSize.setColor(globe1.dc); 
   gSize.fill3DRect(newHOffset+225, newvOffset+50,10,10,true); 
  
   gSize.setColor(globe1.rac); 
   gSize.fill3DRect(newHOffset+125, newvOffset+50,10,10,true); 
  
   gSize.setColor(globe1.rpc); 
   gSize.fill3DRect(newHOffset+35, newvOffset+50,10,10,true); 
  
   gSize.setColor(globe1.sc); 
   gSize.fill3DRect(newHOffset+315, newvOffset+50,10,10,true); 
  
  
  graphics.drawImage(imgSize, 0, 0, this); 
  } 
  
 public void reset_app() 
  { 
  for (int i=0; i<totalPck; i++) 
  {   
   if (window[i] != null) window[i] = null;
  } 
  bValue = 0; 
  nSeqValue = 0; 
  onSelect = -1; 
  fps = 5; 
  flagTCheck = false; 
  sleepTCheck = false; 
  GBackArq = null; 
  RunThread = null;  
  newPack.setEnabled(true); 
  killPack.setEnabled(false); 
  
  stopPack.setLabel("Stop Animation"); 
  stopPack.setActionCommand("StopAnimation"); 
  status = "Simulation restarted. Press 'New Packet' to start.";
  JOptionPane.showMessageDialog(getParent(),"Simulation restarted. Press 'New Packet' to start.");
  repaint(); 
  } 
 public boolean isPacketActive(packet pac[]) 
 { 
 for (int i=0; i<pac.length; i++)
 {
  if (pac[i] == null)
  {
   return false;
  }
  else if (pac[i].packet_on_way) return true;
 }
 return false; 
 } 
 
public boolean isReachedDestination(int packno) 
 { 
 for (int i=0; i<packno; i++) 
 {   
  if (!window[i].packet_reached_dest)
  {
   return false;
  }
 } 
 return true; 
 }

    
    
}

class packet {
boolean packet_on_way, packet_reached_dest, packet_acknowinLengthedged, packet_ack, selected_packet; 
int position_of_packet; 
 
packet() 
 { 
 packet_on_way = false; 
 selected_packet = false; 
 packet_reached_dest = false; 
 packet_acknowinLengthedged = false; 
 packet_ack = true; 
 position_of_packet = 0; 
 } 
 
packet(boolean onway, int packetpos) 
 { 
 packet_on_way = onway; 
 selected_packet = false; 
 packet_reached_dest = false; 
 packet_acknowinLengthedged = false; 
 packet_ack = true; 
 position_of_packet = packetpos; 
 } 
    }

class Globe
{
    int winLengthd, pckWidthd,pckHeigthd ,hod ,vod ,vcd ,tpd ,tosd;
   Color uac = Color.blue; 
   Color ac = Color.white; 
   Color sc = Color.BLACK; 
   Color rpc = Color.blue; 
   Color rac = Color.white; 
   Color dc = Color.RED;
   Globe()
   {
     winLengthd = 3;     
     pckWidthd = 20; 
     pckHeigthd = 30; 
     hod = 100; 
     vod = 50; 
     vcd = 300; 
     tpd = 10; 
     tosd = 30;
     uac = Color.blue; 
     ac = Color.white; 
     sc = Color.BLACK; 
     rpc = Color.blue; 
     rac = Color.white; 
     dc = Color.red;
   }
}
