package nl.helixsoft.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

/**
 * The basis for a dialog with ok / cancel buttons at the bottom
 * <p>
 * The central panel can have arbitrarily complex contents
 * <p>
 * Derived from PathVisio, with modifications.
 */
public class OkCancelDialog extends JDialog implements ActionListener 
{
	public static final String OK = "Ok";
	public static final String CANCEL = "Cancel";

	private String exitCode = CANCEL;
	JButton setButton;
	JPanel contentPanel;

	/**
	 * Create a dialog with ok/cancel buttons. A custom content component can
	 * be set using {@link #setDialogComponent(Component)}.
	 * @param frame The frame to base the dialogs location on
	 * @param title The title of the dialog
	 * @param locationComp The component to base the dialogs location on
	 * @param modal Whether the dialog should be modal
	 * @param cancellable Whether to add a cancel button
	 */
	public OkCancelDialog(Frame frame, String title, boolean modal, boolean cancellable) 
	{
		super(frame, title, modal);
		contentPanel = new JPanel(new BorderLayout());

		JButton cancelButton = new JButton(CANCEL);
		cancelButton.addActionListener(this);

		setButton = new JButton(OK);
		setButton.setActionCommand(OK);
		setButton.addActionListener(this);
		getRootPane().setDefaultButton(setButton);

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
		buttonPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		buttonPane.add(Box.createHorizontalGlue());
		if(cancellable) {
			buttonPane.add(cancelButton);
			buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
		}
		buttonPane.add(setButton);

		contentPanel.add(buttonPane, BorderLayout.PAGE_END);
		add(contentPanel);
		pack();
		setLocationRelativeTo(frame);

		//Make buttons respond to pressing 'Enter'
		UIManager.put("Button.defaultButtonFollowsFocus", Boolean.TRUE);
		KeyStroke esc = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0);
		contentPanel.registerKeyboardAction(this, CANCEL, esc,JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
	}

	public OkCancelDialog(Frame frame, String title, boolean modal) {
		this(frame, title, modal, true);
	}

	/**
	 * Set the component that contains the dialog contents. Subclasses must
	 * call this method to set custom dialog contents.
	 * @param dialogComponent
	 */
	public final void setDialogComponent(Component dialogComponent) {
		contentPanel.add(dialogComponent, BorderLayout.CENTER);
		pack();
		invalidate();
	}

	public String getExitCode() {
		return exitCode;
	}

	protected void okPressed()
	{
		// intended to be overridden
	}
	
	private final void okAction() {
		setButton.requestFocus(); //Fix for bug #228
								 //Request focus to allow possible open celleditors
								 //in this dialog to apply the current value
		okPressed();
		exitCode = OK;
		setVisible(false);
	}

	protected void cancelPressed()
	{
		// intended to be overridden
	}
	
	private final void cancelAction() {
		cancelPressed();
		exitCode = CANCEL;
		setVisible(false);
	}

	public void actionPerformed(ActionEvent e) {
		if (OK.equals(e.getActionCommand())) {
			okAction();
		}
		if(CANCEL.equals(e.getActionCommand())) {
			cancelAction();
		}
	}
}
