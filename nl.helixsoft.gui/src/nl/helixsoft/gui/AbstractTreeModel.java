package nl.helixsoft.gui;

import java.util.HashSet;
import java.util.Set;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;

/**
 * In swing, there is an AbstractTableModel to get you started with implementing a TableModel,
 * but there is no AbstractTreeModel. So this is it.
 */
public abstract class AbstractTreeModel implements TreeModel
{
	private Set<TreeModelListener> listeners = new HashSet<TreeModelListener>();
	
	@Override
	public void addTreeModelListener(TreeModelListener arg0) {
		listeners.add(arg0);
	}

	protected void fireTreeNodesChanged(TreeModelEvent e)
	{
		for (TreeModelListener l : listeners)
		{
			l.treeNodesChanged(e);
		}
	}

	protected void fireTreeNodesInserted(TreeModelEvent e)
	{
		for (TreeModelListener l : listeners)
		{
			l.treeNodesInserted(e);
		}
	}

	protected void fireTreeNodesRemoved(TreeModelEvent e)
	{
		for (TreeModelListener l : listeners)
		{
			l.treeNodesRemoved(e);
		}
	}

	protected void fireTreeStructureChanged(TreeModelEvent e)
	{
		for (TreeModelListener l : listeners)
		{
			l.treeStructureChanged(e);
		}
	}

	@Override
	public void removeTreeModelListener(TreeModelListener arg0) 
	{
		listeners.remove(arg0);
	}
	
}