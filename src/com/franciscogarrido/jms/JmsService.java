package com.franciscogarrido.jms;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.UUID;

import com.tibco.tibjms.TibjmsConnectionFactory;

public class JmsService implements ExceptionListener
{
	private String server;

	private String user;

	private String password;

	private Connection connection;

	private Session session;

	private Queue queueIn, queueOut;

	private String queueNameIn, queueNameOut;

	private MessageProducer messageProducer;

	private MessageConsumer messageConsumer;
  
  private int timeout = 90000;

	@SuppressWarnings("unused")
	private JmsService()
	{
	}

	public JmsService(String serverIn, String userIn, String passwordIn, String queueNameIn, String queueNameOut)
	{
		super();
		server = serverIn;
		user = userIn;
		password = passwordIn;
		this.queueNameIn = queueNameIn;
		this.queueNameOut = queueNameOut;
	}

	public Connection createJmsConnection() throws Exception
	{
		connection = null;
		try
		{
			final ConnectionFactory factory = new TibjmsConnectionFactory(server);
			connection = factory.createConnection(user, password);
			connection.setExceptionListener(this);
			connection.start();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			queueIn = session.createQueue(queueNameIn);
			queueOut = session.createQueue(queueNameOut);

			messageProducer = session.createProducer(queueIn);
			messageProducer.setDeliveryMode(DeliveryMode.PERSISTENT);
			messageProducer.setTimeToLive(timeout);

		}
		catch (JMSException ex)
		{

			throw new JMSException("Failed to create Jms connection:" + ex.getMessage());
		}
		return connection;

	}

	public Connection createJmsConnection(String jmsServer, String jmsUser, String jmsPass) throws Exception
	{
		if (jmsServer != null && jmsUser != null && jmsPass != null)
		{

			try
			{
				final ConnectionFactory factory = new TibjmsConnectionFactory(jmsServer);
				connection = factory.createConnection(jmsUser, jmsPass);
				connection.setExceptionListener(this);
				connection.start();
				session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
				queueIn = session.createQueue(queueNameIn);
				queueOut = session.createQueue(queueNameOut);

				messageProducer = session.createProducer(queueIn);
				messageProducer.setDeliveryMode(DeliveryMode.PERSISTENT);
				messageProducer.setTimeToLive(timeout);
			}
			catch (JMSException ex)
			{

				throw new JMSException("Failed to create Jms connection:" + ex.getMessage());
			}
		}
		return connection;
	}

	public void closeConnection() throws JMSException
	{

		if (connection != null)
			try
			{
				messageProducer.close();
				messageConsumer.close();
				session.close();
				connection.close();
			}
			catch (JMSException ex)
			{
				throw new JMSException("Failed to close Jms connection:" + ex.getMessage());
			}
	}

	@Override
	public void onException(JMSException exception)
	{
		System.err.println("something bad happended: " + exception);
	}

	public String send(String text) throws JMSException
	{
		final TextMessage txtMessageReq = session.createTextMessage(text);
		txtMessageReq.setJMSDestination(queueIn);
		txtMessageReq.setJMSReplyTo(queueOut);
		final String jmsCorrelationID = generateJMSCorrelationID();
		final String messageSelector = String.format("JMSCorrelationID = '%1s'", jmsCorrelationID);
		txtMessageReq.setJMSCorrelationID(messageSelector);
		messageProducer.send(txtMessageReq);
		return messageSelector;
	}
	
	private String generateJMSCorrelationID()
	{
		return UUID.randomUUID().toString();
	}

	public String receive(String sendId) throws JMSException
	{
		messageConsumer = session.createConsumer(queueOut, sendId);
		final TextMessage txtMessageResp = (TextMessage) messageConsumer.receive(timeout);

		if (txtMessageResp == null)
		{
			return "<?xml version = \"1.0\" encoding = \"UTF-8\"?>"
					+"<errorResponse>"
					    +"<message>Response message is null</message>"
					+"</errorResponse>";
		}
		else
		{
			return txtMessageResp.getText();
		}
	}

	public String getServer()
	{
		return server;
	}

	public void setServer(String server)
	{
		this.server = server;
	}

	public String getUser()
	{
		return user;
	}

	public void setUser(String user)
	{
		this.user = user;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}
  
  public int getTimeout()
	{
		return timeout;
	}

	public void setTimeout(int timeout)
	{
		this.timeout = timeout;
	}

}
