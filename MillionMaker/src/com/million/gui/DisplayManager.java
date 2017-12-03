package com.million.gui;

import javax.swing.JOptionPane;;

public class DisplayManager {

	public static void display(String infoMessage) throws InterruptedException	{
		
		Thread t = new Thread(new Runnable(){
	        public void run(){
	        	 JOptionPane.showMessageDialog(null, infoMessage, "Winning Scrip" , JOptionPane.INFORMATION_MESSAGE);
	        }
	    });
		t.start();
		
    }
	public static void close()	{
		JOptionPane.getRootFrame().dispose(); 
	}
	
	public static void main(String[] args) throws InterruptedException {
		display("DLF");
		
		Thread.sleep(1000 * 5);
		
		close();
	}

}
