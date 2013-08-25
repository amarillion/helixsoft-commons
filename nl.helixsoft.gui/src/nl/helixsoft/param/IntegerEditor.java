package nl.helixsoft.param;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.jgoodies.forms.builder.DefaultFormBuilder;

public class IntegerEditor implements Editor, DocumentListener
{
	private JTextField txtField;
	final ParameterModel model;
	final int index;
	final ParameterPanel parent;	

	public IntegerEditor(ParameterModel model, int index, ParameterPanel parent,
			DefaultFormBuilder builder)
	{
		this.index = index;
		this.parent = parent;
		this.model = model;

//		Object meta = model.getMetaData(index);
	
		txtField = new JTextField();
		txtField.setText("" + model.getValue(index));
		txtField.getDocument().addDocumentListener(this);
		txtField.setToolTipText(model.getHint(index));
		
        builder.append(model.getLabel(index), txtField, 2);				
        builder.nextLine();
	}
	
	public Object getValue()
	{
		return txtField.getText();
	}

	public void setValue(Object val)
	{
		if (ignoreEvent) return;
		txtField.setText("" + val);		
	}
	
	// to prevent duplicate changes
	boolean ignoreEvent = false;
	
	private void handleDocumentEvent(DocumentEvent arg0)
	{
		ignoreEvent = true;
		Integer val = null;
		try
		{
			val = Integer.parseInt(txtField.getText());			
		}
		catch (NumberFormatException ex) { /* igore, val remains null */ }
		model.setValue(index, val);
		ignoreEvent = false;
	}

	public void changedUpdate(DocumentEvent arg0)
	{
		handleDocumentEvent(arg0);		
	}

	public void insertUpdate(DocumentEvent arg0)
	{		
		handleDocumentEvent(arg0);		
	}

	public void removeUpdate(DocumentEvent arg0)
	{
		handleDocumentEvent(arg0);		
	}

}
