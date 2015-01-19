package nl.helixsoft.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;

import nl.helixsoft.docking.DockingDesktop;
import nl.helixsoft.gui.preferences.PreferenceManager;

/** 
 * A re-usable main application window. 
 * The window has a menu, toolbar and status bar.
 * The center is divided by a vertical and horizontal split bar.
 * The window position is remembered automatically.
 */
public class MainFrame extends JFrame
{
	private final PreferenceManager preferenceManager;
	protected final PreferencesDlg preferencesDlg;
	private Map<String, JMenu> menuMap = new HashMap<String, JMenu>();
	private Map<Action, JMenuItem> menuActionMap = new HashMap<Action, JMenuItem>();
	private JLabel statusBar;
	private DockingDesktop desk = new DockingDesktop();

	public void addMenuItem(String menu, Action a)
	{
		JMenu m = menuMap.get(menu);
		if (m == null) throw new IllegalArgumentException("Menu with name '" + menu + "' doesn't exist. Possible values are " + menuMap.keySet());
		JMenuItem i = m.add (a);
		menuActionMap.put (a, i);
	}

	public void addMenuItem(String menu, JMenuItem a)
	{
		menuMap.get(menu).add (a);
	}

	public void addMenu(String key, JMenu menu)
	{
		getJMenuBar().add (menu);
		menuMap.put(key, menu);
	}
	
	public void setStatusText(String text)
	{
		statusBar.setText(text);
	}
	
	private JMenuBar createMenuBar()
	{
		JMenuBar result = new JMenuBar();
		JMenu file = new JMenu ("File");
		file.setMnemonic('F');
		JMenu edit = new JMenu ("Edit");
		edit.setMnemonic('E');
		JMenu help = new JMenu ("Help");
		file.setMnemonic('H');
		result.add (file);
		result.add (edit);
		result.add (help);
		menuMap.put ("file", file);
		menuMap.put ("edit", edit);
		menuMap.put ("help", help);
		return result;
	}

	public MainFrame(PreferenceManager _preferenceManager)
	{				
		this.preferenceManager = _preferenceManager;
       
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);		
		
        getContentPane().add (desk, BorderLayout.CENTER);
		
        statusBar = new JLabel();
        statusBar.setPreferredSize(new Dimension(100, 20));
		getContentPane().add (statusBar, BorderLayout.SOUTH);

	    setJMenuBar(createMenuBar());
	    getContentPane().add(createToolBar(), BorderLayout.NORTH);
	    
		preferencesDlg = new PreferencesDlg(preferenceManager);

		pack();
		
		setSize(preferenceManager.getInt(AppPreference.WIN_W), preferenceManager.getInt(AppPreference.WIN_H));
		int x = preferenceManager.getInt(AppPreference.WIN_X);
		int y = preferenceManager.getInt(AppPreference.WIN_Y);
		if(x >= 0 && y >= 0) setLocation(x, y);		
	}
	
	public enum PanelPosition
	{
		MAIN, SIDEBAR, BOTTOMBAR
	}

	private JToolBar toolbar;
	
	private JToolBar createToolBar()
	{
		toolbar = new JToolBar();
		toolbar.setFloatable(false);
		return toolbar;
	}
	
	public void addToolbarButton(Action a)
	{
		toolbar.add (a);
	}

	public void addToolbarComponent(Component a)
	{
		toolbar.add (a);
	}

	public void clearToolbar() 
	{
		List<Component> toRemove = new ArrayList<Component>();
		for (int i = 0; i < toolbar.getComponentCount(); ++i)
		{
			Component c = toolbar.getComponent(i);
			if (!(c instanceof JComboBox)) toRemove.add(c);
		}
		for (Component c : toRemove) toolbar.remove(c);
	}

	/**
	 * Utility function for getting icons
	 */
	public static ImageIcon getImageIcon(String location)
	{
		return new ImageIcon (MainFrame.class.getClassLoader().getResource(location));
	}

	public PreferencesDlg getPreferencesDlg() 
	{
		return preferencesDlg;
	}

	private static class PreferencesAction extends AbstractAction
	{
		MainFrame parent;
		
		public PreferencesAction(MainFrame parent)
		{
			super ("Preferences");
			putValue(MNEMONIC_KEY, KeyEvent.VK_P);
			this.parent = parent;
		}
		
		public void actionPerformed(ActionEvent ae) 
		{
			PreferencesDlg dlg = parent.getPreferencesDlg(); 
			dlg.createAndShowGUI(parent);
		}
	}
	
	public final PreferencesAction preferencesAction = new PreferencesAction(this);

	public void shutdown() 
	{
		System.out.println ("Shutting down");

		Dimension size = MainFrame.this.getSize();
		Point p = MainFrame.this.getLocationOnScreen();
		preferenceManager.setInt(AppPreference.WIN_W, size.width);
		preferenceManager.setInt(AppPreference.WIN_H, size.height);
		preferenceManager.setInt(AppPreference.WIN_X, p.x);
		preferenceManager.setInt(AppPreference.WIN_Y, p.y);
		
		preferenceManager.store();

		dispose();
		System.exit(0);		
	}

	public void removeMenuItem(String key, Action action) 
	{
		JMenu menu = menuMap.get(key);
		JMenuItem item = menuActionMap.get(action);
		menu.remove(item);
	}

	public void removeMenuItem(String key, JMenuItem item) 
	{
		JMenu menu = menuMap.get(key);
		menu.remove(item);
	}

	public DockingDesktop getDockingDesktop() {
		return desk;
	}


}
