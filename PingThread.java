/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author ARVIN DANNY
 */
import java.io.*;
import java.util.logging.*;
import javax.swing.*;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;

public class PingThread extends Tool implements Runnable {


    String ip,pingResult = "",packets="",status;
    int request;
    double interval,iter=0;
    public javax.swing.JTable table;
    public javax.swing.JProgressBar progress;

    public PingThread(String ip,int request,String packets,double interval,javax.swing.JTable table,javax.swing.JProgressBar progress){
        this.ip = ip;
        this.request = request;
        this.packets = packets;
        this.interval = interval;
        this.table = table;
        this.progress = progress;
    }
    synchronized public void run() {
        log("IP address"+"Request:"+request+" Packets:"+packets);
        double totalSeconds = iter;
        int minutes = (int) totalSeconds / 60;
        int seconds = (int) totalSeconds % 60;

        String time_minutes="",time_seconds="";
        if(minutes<10)
            time_minutes = 0+"";
        if(seconds<10)
            time_seconds = 0+"";

        String time = time_minutes+minutes+" : "+time_seconds+seconds;

        pingResult="";
        Process p = null;
        try {
            p = Runtime.getRuntime().exec("ping -n "+packets+" -w 1000 "+ip);
            int returnVal=p.waitFor();            
            if (returnVal == 0) {
                status="Reachable";
            } else {
                status="Unreachable";
            }
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(null,"After "+time+" minutes! \nPing Failed for the IP : "+ip,"Ping Status for IP : "+ip+" | Request:"+request,JOptionPane.ERROR_MESSAGE);
        }
        finally {
            if (p != null) {
                p.destroy();
            }
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
           try{ 
            String inputLine;
            int totPackets,lostPackets,returnedPackets,percentage=0;
            while ((inputLine = br.readLine()) != null) {
                pingResult += inputLine +" \n ";
                String nextLine = inputLine;
                String sub="Lost = ";
                String lost="0";
                if (nextLine.contains(sub)) {
                    lost = nextLine.substring(nextLine.indexOf(sub) + sub.length());
                    lost = lost.substring(0, lost.indexOf(" ("));
                
                totPackets = Integer.parseInt(packets);
                lostPackets = Integer.parseInt(lost);
                returnedPackets = totPackets - lostPackets;
                percentage = (returnedPackets * 100) / totPackets;
                }
            } 
            iter+=interval;     
            Object[] row = {request,ip,status,percentage+"%",time,pingResult};
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.addRow(row);
            progress.setVisible(false);

        } 
        catch (Exception e) {
            System.out.println(e);
            JOptionPane.showMessageDialog(null,"Error while processing IP address"+"Request : "+request+" Packets : "+packets,"Validation",JOptionPane.WARNING_MESSAGE);
            log("Error while processing IP address"+"Request : "+request+" Packets:"+packets);
            return;

        } 
        finally {
            p.exitValue();
            try{
                br.close();
            }
            catch(Exception e){
                System.out.println(e);
                JOptionPane.showMessageDialog(null,"Error while Closing the Buffer","Validation",JOptionPane.ERROR_MESSAGE);
            }
        }
        JOptionPane.showMessageDialog(null, pingResult,"Message for Request : "+request,JOptionPane.PLAIN_MESSAGE);
        log(pingResult);
    }

    public void log(String operation)
    {
        try {
            Logger logger = Logger.getLogger("PingCheck");
            FileHandler fh;
            fh = new FileHandler("log.txt",true);
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
                logger.info("\t"+operation+"\n");
            fh.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            LogManager.getLogManager().reset();
        }
}
        
}
