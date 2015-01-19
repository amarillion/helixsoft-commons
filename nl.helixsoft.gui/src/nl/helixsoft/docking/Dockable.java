package nl.helixsoft.docking;

import java.awt.Component;

/**
 * 
 * Docking framework. 
 * 
 * Similar to VLDocking, but I couldn't get Workspace switching to work well with VLDocking.
 * 
 */
public interface Dockable {
	
	public Component getComponent();
	public String getKey();

}
