package nl.helixsoft.gui;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

public class IndeterminateProgressDialog implements PropertyChangeListener
{
	private JDialog dialog;
	private JLabel note = new JLabel();
	
	public void propertyChange(PropertyChangeEvent event) 
	{
		if ("state".equals(event.getPropertyName())
				&& SwingWorker.StateValue.DONE == event.getNewValue()) {
			dialog.setVisible(false);
			dialog.dispose();
		}
		else if ("note".equals(event.getPropertyName()))
		{
			note.setText ("" + event.getNewValue());
		}
	}

	private IndeterminateProgressDialog(JDialog owner, String title, SwingWorker<?, ?> worker)
	{
		dialog = new JDialog(owner, true);
		dialog.setTitle (title);
		dialog.setSize(300, 100);
		dialog.setLocationRelativeTo(owner);
		doRest(worker);
	}

	private IndeterminateProgressDialog(JFrame owner, String title, SwingWorker<?, ?> worker)
	{
		dialog = new JDialog(owner, true);
		dialog.setTitle (title);
		dialog.setSize(300, 100);
		dialog.setLocationRelativeTo(owner);
		doRest(worker);
	}
	
	private void doRest(SwingWorker<?, ?> worker)
	{
		final JProgressBar jpb = new JProgressBar(0, 100);
		jpb.setIndeterminate(true);
		dialog.getContentPane().add(jpb, BorderLayout.NORTH);
		dialog.getContentPane().add(note, BorderLayout.SOUTH);

		//			JButton btnCancel = new JButton("Cancel");
		//
		//			ActionListener actionListener = new ActionListener() {
		//				public void actionPerformed(ActionEvent e) {
		//					//TODO send interrupt message
		//				}
		//			};

		//			btnCancel.addActionListener(actionListener);

		worker.addPropertyChangeListener(this);
		dialog.setVisible(true);
	}

	public static void createAndShow(JDialog owner, String title, SwingWorker<?, ?> worker)
	{
		new IndeterminateProgressDialog(owner, title, worker);
	}
	
	public static void createAndShow(JFrame owner, String title, SwingWorker<?, ?> worker)
	{
		new IndeterminateProgressDialog(owner, title, worker);
	}
	
}