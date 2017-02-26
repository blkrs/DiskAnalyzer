package com.diskscanner;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class RightClickMenu {

	private JPopupMenu popup = null;
	
	String filePath = null;
	
	public RightClickMenu(final String filePath)
	{
		this.filePath = filePath;
		popup = new JPopupMenu();
		JMenuItem menuItemOpen = new JMenuItem("Open " + filePath);
		popup.add(menuItemOpen);
		
		menuItemOpen.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					Desktop.getDesktop().open(new File(filePath));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}});
		
		JMenuItem menuItemDelete = new JMenuItem("Remove " + filePath);
		popup.add(menuItemDelete);
		
		menuItemDelete.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				new File(filePath).delete();
			}});
		
	}
	
	void show(Component c, int x, int y)
	{
		popup.show(c,x,y);
	}
}
