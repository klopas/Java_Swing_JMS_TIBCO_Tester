package com.franciscogarrido.jms;

import java.awt.Frame;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

@SuppressWarnings({ "serial" })
public class JmsConfigurationDialog extends JDialog implements PropertyChangeListener
{

//	private static final Logger LOGGER = Logger.getLogger(EagleMifid.class);

	private final static String WINDOW_TITLE = "Configuration";

	private final JPanel globalPanel = new JPanel();

	private JTextField jTextFieldHost = new JTextField();

	private JTextField jTextFieldUsername = new JTextField();

	private JTextField jTextFieldPassword = new JTextField();

	private JTextField jTextFieldQueueNameIn = new JTextField();

	private JTextField jTextFieldQueueNameOut = new JTextField();

	private boolean ok;

	private JOptionPane optionPane;

	private final String btnString1 = "Accept";

	private final String btnString2 = "Cancel";

	public JmsConfigurationDialog()
	{
		this(null, null, null, null, null);
	}

	public JmsConfigurationDialog(String serverUrl, String userName, String password, String queueNameIn, String queueNameOut)
	{
		super((Frame) null, WINDOW_TITLE, true);
		setResizable(false);
		setLocationRelativeTo(null);
		createComponents();

		jTextFieldHost.setText(serverUrl);
		jTextFieldUsername.setText(userName);
		jTextFieldPassword.setText(password);
		jTextFieldQueueNameIn.setText(queueNameIn);
		jTextFieldQueueNameOut.setText(queueNameOut);
	}

	private void createComponents()
	{
		final BoxLayout verticalBoxLayout = new BoxLayout(globalPanel, BoxLayout.Y_AXIS);
		globalPanel.setLayout(verticalBoxLayout);

		final JPanel hostPanel = new JPanel();
		final BoxLayout hostBoxLayout = new BoxLayout(hostPanel, BoxLayout.X_AXIS);
		hostPanel.setLayout(hostBoxLayout);
		final JLabel hostLabel = new JLabel("Host:");
		hostPanel.add(hostLabel);
		hostPanel.add(jTextFieldHost);

		globalPanel.add(hostPanel);

		final JPanel usernamePanel = new JPanel();
		final BoxLayout usernameBoxLayout = new BoxLayout(usernamePanel, BoxLayout.X_AXIS);
		usernamePanel.setLayout(usernameBoxLayout);
		final JLabel usernameLabel = new JLabel("Username:");
		usernamePanel.add(usernameLabel);
		usernamePanel.add(jTextFieldUsername);

		globalPanel.add(usernamePanel);

		final JPanel passwordPanel = new JPanel();
		final BoxLayout passwordBoxLayout = new BoxLayout(passwordPanel, BoxLayout.X_AXIS);
		passwordPanel.setLayout(passwordBoxLayout);
		final JLabel passwordLabel = new JLabel("Password:");
		passwordPanel.add(passwordLabel);
		passwordPanel.add(jTextFieldPassword);

		globalPanel.add(passwordPanel);

		final JPanel queueNameInPanel = new JPanel();
		final BoxLayout queueNameInBoxLayout = new BoxLayout(queueNameInPanel, BoxLayout.X_AXIS);
		queueNameInPanel.setLayout(queueNameInBoxLayout);
		final JLabel queueNameInLabel = new JLabel("Queue Name In:");
		queueNameInPanel.add(queueNameInLabel);
		queueNameInPanel.add(jTextFieldQueueNameIn);

		globalPanel.add(queueNameInPanel);

		final JPanel queueNameOutPanel = new JPanel();
		final BoxLayout queueNameOutBoxLayout = new BoxLayout(queueNameOutPanel, BoxLayout.X_AXIS);
		queueNameOutPanel.setLayout(queueNameOutBoxLayout);
		final JLabel queueNameOutLabel = new JLabel("Queue Name Out:");
		queueNameOutPanel.add(queueNameOutLabel);
		queueNameOutPanel.add(jTextFieldQueueNameOut);

		globalPanel.add(queueNameOutPanel);

		final Object[] options = { btnString1, btnString2 };
		optionPane = new JOptionPane(globalPanel, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null, options, options[0]);

		setContentPane(optionPane);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		optionPane.addPropertyChangeListener(this);
		pack();
	}

	public void propertyChange(PropertyChangeEvent e)
	{
		String prop = e.getPropertyName();

		if (isVisible() && (e.getSource() == optionPane) && (JOptionPane.VALUE_PROPERTY.equals(prop) || JOptionPane.INPUT_VALUE_PROPERTY.equals(prop)))
		{
			Object value = optionPane.getValue();

			if (value == JOptionPane.UNINITIALIZED_VALUE)
			{
				//ignore reset
				return;
			}

			//Reset the JOptionPane's value.
			//If you don't do this, then if the user
			//presses the same button next time, no
			//property change event will be fired.
			optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);

			if (btnString1.equals(value))
			{
				if (jTextFieldHost.getText() != null && !jTextFieldHost.getText().isEmpty() && jTextFieldUsername.getText() != null && !jTextFieldUsername.getText().isEmpty() && jTextFieldPassword.getText() != null && !jTextFieldPassword.getText().isEmpty() && jTextFieldQueueNameIn.getText() != null && !jTextFieldQueueNameIn.getText().isEmpty() && jTextFieldQueueNameOut.getText() != null && !jTextFieldQueueNameOut.getText().isEmpty())
				{
					ok = true;
					exit();
				}
				else
				{
					//text was invalid
					ok = false;
					JOptionPane.showMessageDialog(this, "You must fill in all fields.", "ERROR", JOptionPane.ERROR_MESSAGE);
				}
			}
			else
			{ //user closed dialog or clicked cancel
				ok = false;
				exit();
			}
		}
	}

	public void exit()
	{
		dispose();
	}

	public boolean isOk()
	{
		return ok;
	}

	public String getHost()
	{
		return jTextFieldHost.getText();
	}

	public String getUsername()
	{
		return jTextFieldUsername.getText();
	}

	public String getPassword()
	{
		return jTextFieldPassword.getText();
	}

	public String getQueueNameIn()
	{
		return jTextFieldQueueNameIn.getText();
	}

	public String getQueueNameOut()
	{
		return jTextFieldQueueNameOut.getText();
	}

}
