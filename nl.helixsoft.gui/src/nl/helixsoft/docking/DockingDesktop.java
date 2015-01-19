package nl.helixsoft.docking;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

public class DockingDesktop extends JPanel
{
	private List<Dockable> childDockables = new ArrayList<Dockable>();
	
	public DockingDesktop()
	{
		setLayout (new BorderLayout());
	}
	
	/** locations like BorderLayout */
	public enum Split { LEFT, RIGHT, TOP, BOTTOM };

	public void addDockable(Dockable view)
	{
		addDockable (view, Split.BOTTOM);
	}
	
	public void addDockable(Dockable view, Split loc)
	{
		assert (view != null);
		assert (!childDockables.contains(view));
		
		if (childDockables.size() == 0)
		{
			add (view.getComponent(), BorderLayout.CENTER);
		}
		else
		{
			// find a split pane to add to
			Dockable oldDockable = childDockables.get(0);
			Container parent = oldDockable.getComponent().getParent();
			parent.remove (oldDockable.getComponent());
//			parent.setLayout(new BorderLayout());
			
			int splitDir = -1;
			boolean newLeft = false;
			
			switch (loc)
			{
			case LEFT:
				splitDir = JSplitPane.HORIZONTAL_SPLIT;
				break;
			case RIGHT:
				splitDir = JSplitPane.HORIZONTAL_SPLIT;
				break;
			case TOP:
				splitDir = JSplitPane.VERTICAL_SPLIT;
				break;
			case BOTTOM:
				splitDir = JSplitPane.VERTICAL_SPLIT;
				break;
			default:
				assert (false);
			}
			
			JSplitPane splitPane = new JSplitPane(splitDir);  //TODO: maybe store the tree of splitpanes?
			parent.add (splitPane);

			switch (loc)
			{
			case LEFT:
				splitPane.setLeftComponent(view.getComponent());
				splitPane.setRightComponent(oldDockable.getComponent());
				break;
			case RIGHT:
				splitPane.setLeftComponent(oldDockable.getComponent());
				splitPane.setRightComponent(view.getComponent());
				break;
			case TOP:
				splitPane.setTopComponent(view.getComponent());
				splitPane.setBottomComponent(oldDockable.getComponent());
				break;
			case BOTTOM:
				splitPane.setTopComponent(oldDockable.getComponent());
				splitPane.setBottomComponent(view.getComponent());
				break;
			default:
				assert (false);
			}

		}
		childDockables.add (view);
	}
	
	/**
	 * Put a view in the location of another view
	 */
	public void replaceDockable(Dockable oldView, Dockable newView) 
	{
		assert (childDockables.contains(oldView));
		assert (oldView != null);
		assert (newView != null);
		
		Container parent = oldView.getComponent().getParent();
		
		if (parent instanceof JSplitPane)
		{
			JSplitPane splitPane = (JSplitPane)parent;
			
			Component left = splitPane.getLeftComponent();
			Component right = splitPane.getRightComponent();			
			splitPane.remove(oldView.getComponent());
			if (oldView.getComponent() == left)
			{
				splitPane.setLeftComponent(newView.getComponent());
			}
			else if (oldView.getComponent() == right)
			{
				splitPane.setRightComponent(newView.getComponent());				
			}
			else
			{
				assert (false);
			}
			
		}
		else
		{
			parent.remove(oldView.getComponent());
			parent.add (newView.getComponent());
		}
		
		childDockables.remove (oldView);
		childDockables.add(newView);
		
		validate();
	}

	public void addViewAsTab(Dockable view, Split loc)
	{
		throw new UnsupportedOperationException();
	}
	
	public void removeDockable(Dockable view)
	{
		assert (view != null);
		assert (childDockables.contains(view));
		
		Container parent = view.getComponent().getParent();
		
		if (parent instanceof JSplitPane)
		{
			JSplitPane splitPane = (JSplitPane)parent;
			
			Component one = splitPane.getLeftComponent();
			Component other = splitPane.getRightComponent();
			
			if (other == view.getComponent())
			{
				// swap
				Component temp = one;
				one = other;
				other = temp;
			}
			
			assert (one == view.getComponent());			
			splitPane.remove(one);
			
			Container splitParent = splitPane.getParent();
			splitParent.remove (splitPane);
			
			splitParent.add (other);
			
			validate();
		}
		else
		{
			parent.remove (view.getComponent());
		}
		
		childDockables.remove(view);
	}
	
	public void removeView(String key)
	{
		throw new UnsupportedOperationException();
	}

	
}
