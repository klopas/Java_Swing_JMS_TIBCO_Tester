package com.franciscogarrido;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.jms.Connection;
import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneLayout;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.Document;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import com.franciscogarrido.jms.JmsConfigurationDialog;
import com.franciscogarrido.jms.JmsService;
import com.franciscogarrido.xml.XmlFormatter;
import com.franciscogarrido.xml.XmlTextPane;

public class JMSTester implements Runnable, ActionListener
{

	private JMenuBar menuBar;

	private JMenu fileMenu;

	private JMenuItem openMenuItem;

	private JMenuItem aboutMenuItem;

	private XmlTextPane leftTextArea;

	private XmlTextPane rightTextArea;

	private JTextArea logTextArea;

	private String serverUrl = "";

	private String userName = "";

	private String password = "";

	private String queueNameIn = "";

	private String queueNameOut = "";

	final JmsConfigurationDialog configDialog = new JmsConfigurationDialog(serverUrl, userName, password, queueNameIn, queueNameOut);

	public static void main(String args[])
	{
		// the proper way to show a jframe (invokeLater)
		SwingUtilities.invokeLater(new JMSTester());
	}

	@Override
	public void run()
	{
		final String title = "JMS Tester";
		final JFrame frame = new JFrame(title);
		final Container globalContent = frame.getContentPane();
		globalContent.setLayout(new BoxLayout(globalContent, BoxLayout.Y_AXIS));

		menuBar = new JMenuBar();
		// build the File menu
		fileMenu = new JMenu("File");
		openMenuItem = new JMenuItem("Configuration");
		openMenuItem.setActionCommand("CONFIGURATION");
		openMenuItem.addActionListener(this);
		fileMenu.add(openMenuItem);

		aboutMenuItem = new JMenuItem("About");
		aboutMenuItem.setActionCommand("ABOUT");
		aboutMenuItem.addActionListener(this);
		aboutMenuItem.setPreferredSize(new Dimension(50, Integer.MAX_VALUE));
		aboutMenuItem.setMaximumSize(new Dimension(50, Integer.MAX_VALUE));

		// build the Edit menu
//		editMenu = new JMenu("Edit");
//		cutMenuItem = new JMenuItem("Cut");
//		copyMenuItem = new JMenuItem("Copy");
//		pasteMenuItem = new JMenuItem("Paste");
//		editMenu.add(cutMenuItem);
//		editMenu.add(copyMenuItem);
//		editMenu.add(pasteMenuItem);

		// add menus to menubar
		menuBar.add(fileMenu);
		menuBar.add(aboutMenuItem);
//		menuBar.add(editMenu);
		menuBar.setPreferredSize(new Dimension(Integer.MAX_VALUE, 25));
		menuBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));

		final JPanel content = new JPanel();
		content.setLayout(new BoxLayout(content, BoxLayout.X_AXIS));

		leftTextArea = new XmlTextPane();
		JScrollPane leftPane = new JScrollPane(leftTextArea);
		content.add(leftPane);
		leftPane.setBorder(new TitledBorder("Request"));

		//--
		final UndoManager undo = new UndoManager();
		Document doc = leftTextArea.getDocument();

		// Listen for undo and redo events
		doc.addUndoableEditListener(new UndoableEditListener()
		{
			public void undoableEditHappened(UndoableEditEvent evt)
			{
				undo.addEdit(evt.getEdit());
			}
		});

		// Create an undo action and add it to the text component
		leftTextArea.getActionMap().put("Undo", new AbstractAction("Undo")
		{
			public void actionPerformed(ActionEvent evt)
			{
				try
				{
					if (undo.canUndo())
					{
						undo.undo();
					}
				}
				catch (CannotUndoException e)
				{
				}
			}
		});

		// Bind the undo action to ctl-Z
		leftTextArea.getInputMap().put(KeyStroke.getKeyStroke("control Z"), "Undo");

		// Create a redo action and add it to the text component
		leftTextArea.getActionMap().put("Redo", new AbstractAction("Redo")
		{
			public void actionPerformed(ActionEvent evt)
			{
				try
				{
					if (undo.canRedo())
					{
						undo.redo();
					}
				}
				catch (CannotRedoException e)
				{
				}
			}
		});

		// Bind the redo action to ctl-Y
		leftTextArea.getInputMap().put(KeyStroke.getKeyStroke("control Y"), "Redo");
		//--

		rightTextArea = new XmlTextPane();
		rightTextArea.setEditable(false);
		JScrollPane rightPane = new JScrollPane(rightTextArea);
		content.add(rightPane);
		rightPane.setBorder(new TitledBorder("Response"));

		//--

		final JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		final JButton buttonSend = new JButton("Send");
		buttonPanel.add(buttonSend);

		buttonSend.addActionListener(this);

		//--
		logTextArea = new JTextArea();
		logTextArea.setEditable(false);
		final JScrollPane logPane = new JScrollPane(logTextArea);
		logPane.setLayout(new ScrollPaneLayout());
		logPane.setPreferredSize(new Dimension(Integer.MAX_VALUE, 150));
		logPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
		//--

		globalContent.add(menuBar);
		globalContent.add(content);
		globalContent.add(logPane);
		globalContent.add(buttonPanel);

		frame.setSize(800, 600);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if ("CONFIGURATION".equals(e.getActionCommand()))
		{
			configDialog.setVisible(true);
			if (configDialog.isOk())
			{
				// No sirve, se podría quitar.
				serverUrl = configDialog.getHost();
				userName = configDialog.getUsername();
				password = configDialog.getPassword();
				queueNameIn = configDialog.getQueueNameIn();
				queueNameOut = configDialog.getQueueNameOut();
			}
		}
		else if ("ABOUT".equals(e.getActionCommand()))
		{
			JOptionPane.showMessageDialog(null, "Developer: Francisco Garrido Carrasco\nVersion: 0.1\nCompany: Common Management Solutions\nBugs or improvements: francisco.garrido@commonms.com");
		}
		else
		{
			if (leftTextArea.getText() != null && !leftTextArea.getText().isEmpty())
			{

				try
				{
					final JmsService jmsService = new JmsService(configDialog.getHost(), configDialog.getUsername(), configDialog.getPassword(), configDialog.getQueueNameIn(), configDialog.getQueueNameOut());
					final Connection connection = jmsService.createJmsConnection();
					String response = "";
					if (connection != null)
					{
						final String id = jmsService.send(leftTextArea.getText());
						logTextArea.append("Message " + id + " - sent\n");
						response = jmsService.receive(id);
						jmsService.closeConnection();
					}

					rightTextArea.setText(XmlFormatter.format(response));
				}
				catch (Throwable ex)
				{
					ex.printStackTrace();
//				System.exit(0);
				}
			}
			else
			{
				JOptionPane.showMessageDialog(null, "You must write a request.", "ERROR", JOptionPane.WARNING_MESSAGE);
			}
		}
	}

}
