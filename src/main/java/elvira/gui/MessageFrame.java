/* MessageFrame.java */

package elvira.gui;		

import java.awt.*;
import java.beans.PropertyVetoException;
import javax.swing.*;
import java.util.ResourceBundle;
import java.util.Enumeration;
import elvira.Elvira;

public class MessageFrame extends javax.swing.JInternalFrame
{
	public MessageFrame()
	{
	   
		// Languaje selection
		switch (Elvira.getLanguaje()) {
		   case Elvira.AMERICAN: menuBundle = ResourceBundle.getBundle ("localize/Menus");            
		                         break;
		   case Elvira.SPANISH: menuBundle = ResourceBundle.getBundle ("localize/Menus_sp");            
		                         break;		                         
		}	
	   
		// This code is automatically generated by Visual Cafe when you add
		// components to the visual environment. It instantiates and initializes
		// the components. To modify the code, only use code syntax that matches
		// what Visual Cafe can generate, or Visual Cafe may be unable to back
		// parse your Java file into its visual environment.
		//{{INIT_CONTROLS
		setIconifiable(true);
		setMaximizable(true);
		setTitle(Elvira.getElviraFrame().localize(menuBundle, "MessageWindow.label"));
		setResizable(true);
		setClosable(true);
		getContentPane().setLayout(new BorderLayout(0,0));
		setSize(393,274);
		getContentPane().add(messageScrollPane);
		messageScrollPane.setBounds(0,0,393,274);
		messageArea.setEditable(false);
		messageScrollPane.getViewport().add(messageArea);
		messageArea.setBounds(0,0,390,271);
				
		//}}
	
		//{{REGISTER_LISTENERS
		SymInternalFrame lSymInternalFrame = new SymInternalFrame();
		this.addInternalFrameListener(lSymInternalFrame);
		//}}
	}

	public MessageFrame(String sTitle)
	{
		this();
		setTitle(sTitle);
	}

	public void setVisible(boolean b)
	{
		if (b)
			setLocation(50, 50);
		super.setVisible(b);
	}

	static public void main(String args[])
	{
		(new MessageFrame()).setVisible(true);
	}

	public void addNotify()
	{
		// Record the size of the window prior to calling parents addNotify.
		Dimension size = getSize();

		super.addNotify();

		if (frameSizeAdjusted)
			return;
		frameSizeAdjusted = true;

		// Adjust size of frame according to the insets and menu bar
		Insets insets = getInsets();
		javax.swing.JMenuBar menuBar = getRootPane().getJMenuBar();
		int menuBarHeight = 0;
		if (menuBar != null)
			menuBarHeight = menuBar.getPreferredSize().height;
		int offset = 0;
		Component comp[] = getComponents();
		for(int i = 0; i < comp.length; ++i) {
			if (comp[i] instanceof javax.swing.JRootPane)
				continue;
			offset += comp[i].getPreferredSize().height;
		}
		setSize(insets.left + insets.right + size.width, insets.top + insets.bottom + size.height + menuBarHeight + offset);
	}

	// Used by addNotify
	boolean frameSizeAdjusted = false;

	//{{DECLARE_CONTROLS
	javax.swing.JScrollPane messageScrollPane = new javax.swing.JScrollPane();
	javax.swing.JTextArea messageArea = new javax.swing.JTextArea();
	//}}

   ResourceBundle menuBundle;
   
   public String localize() {
      return Elvira.getElviraFrame().localize(menuBundle,"MessageWindow.label");
   }
   
	class SymInternalFrame implements javax.swing.event.InternalFrameListener
	{
		public void internalFrameClosed(javax.swing.event.InternalFrameEvent event)
		{
			Object object = event.getSource();
			if (object == MessageFrame.this)
				MessageFrame_internalFrameClosed(event);
		}

		public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent event)
		{
		}

		public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent event)
		{
		}

		public void internalFrameActivated(javax.swing.event.InternalFrameEvent event)
		{
		   if (Elvira.getElviraFrame() == null)
		      return;
		   
		   Enumeration windowMenu = Elvira.getElviraFrame().getWindowGroup().getElements();
		   boolean exit = false;
		   		      
		   while (windowMenu.hasMoreElements() && !exit) {
		      JMenuItem windowItem = (JMenuItem) windowMenu.nextElement();
		      if (windowItem.getText().equals(localize())) {
		         if (!windowItem.isSelected())
		            windowItem.setSelected(true);
		         exit = true;
		      }
		   }
		   Elvira.getElviraFrame().enableMenusOpenNetworks(false,null);
		}

		public void internalFrameIconified(javax.swing.event.InternalFrameEvent event)
		{
		}

		public void internalFrameClosing(javax.swing.event.InternalFrameEvent event)
		{
		}

		public void internalFrameOpened(javax.swing.event.InternalFrameEvent event)
		{
		}
	}

	void MessageFrame_internalFrameClosed(javax.swing.event.InternalFrameEvent event)
	{	   
	   Elvira.getElviraFrame().getCurrentNetworkFrame();			
		Elvira.getElviraFrame().reestructWindowMenu(getTitle());		
		if (!Elvira.getElviraFrame().getWindowGroup().getElements().hasMoreElements())
		   Elvira.getElviraFrame().enableMenusOpenFrames(false); 
	}
}