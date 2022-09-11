package gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.BasicConfigurator;

import data.Person;
import helper.XMLConvert;

import javax.swing.JTextField;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.swing.JButton;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.Date;
import java.util.Properties;

import java.util.Properties;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import org.apache.log4j.BasicConfigurator;

import javax.swing.JLabel;
import helper.XMLConvert;

public class Sender_Gui extends JFrame  implements ActionListener {

	private JPanel contentPane;
	private JTextField txtMaSo;
	private JButton btnSen;
	private JTextField txtHoTen;
	private JTextField txtNgaySinh;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Sender_Gui frame = new Sender_Gui();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Sender_Gui() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 407);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		txtMaSo = new JTextField();
		txtMaSo.setFont(new Font("Times New Roman", Font.BOLD, 24));
		txtMaSo.setBounds(131, 10, 270, 38);
		contentPane.add(txtMaSo);
		txtMaSo.setColumns(10);
		
		btnSen = new JButton("Send");
		btnSen.setFont(new Font("Times New Roman", Font.BOLD, 24));
		btnSen.setBounds(160, 299, 162, 38);
		contentPane.add(btnSen);
		
		JLabel lblNewLabel = new JLabel("M\u00E3 s\u1ED1:");
		lblNewLabel.setFont(new Font("Times New Roman", Font.BOLD, 24));
		lblNewLabel.setBounds(20, 10, 88, 38);
		contentPane.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("H\u1ECD t\u00EAn: ");
		lblNewLabel_1.setFont(new Font("Times New Roman", Font.BOLD, 24));
		lblNewLabel_1.setBounds(20, 92, 88, 38);
		contentPane.add(lblNewLabel_1);
		
		txtHoTen = new JTextField();
		txtHoTen.setFont(new Font("Times New Roman", Font.BOLD, 24));
		txtHoTen.setBounds(131, 92, 270, 33);
		contentPane.add(txtHoTen);
		txtHoTen.setColumns(10);
		
		JLabel lblNewLabel_2 = new JLabel("Ng\u00E0y sinh\r\n:");
		lblNewLabel_2.setFont(new Font("Times New Roman", Font.BOLD, 24));
		lblNewLabel_2.setBounds(20, 184, 116, 38);
		contentPane.add(lblNewLabel_2);
		
		txtNgaySinh = new JTextField();
		txtNgaySinh.setFont(new Font("Times New Roman", Font.BOLD, 24));
		txtNgaySinh.setBounds(151, 184, 250, 38);
		contentPane.add(txtNgaySinh);
		txtNgaySinh.setColumns(10);
		
		btnSen.addActionListener(this);
	}

	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		Object object = e.getSource();
		if (object.equals(btnSen)) {
			try {
				send();
				
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				
				
			}
		}
		
	}
	
	private void send()  throws Exception {
		long maSo = Long.parseLong(txtMaSo.getText().trim());
		String name = txtHoTen.getText().trim();
		LocalDate ns = LocalDate.parse(txtNgaySinh.getText());
		
		
		
		//config environment for JMS
				BasicConfigurator.configure();
		//config environment for JNDI
				Properties settings = new Properties();
				settings.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
				settings.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");
		//create context
				Context ctx = new InitialContext(settings);
		//lookup JMS connection factory
				ConnectionFactory factory = (ConnectionFactory) ctx.lookup("ConnectionFactory");
		//lookup destination. (If not exist-->ActiveMQ create once)
				Destination destination = (Destination) ctx.lookup("dynamicQueues/thanthidet");
		//get connection using credential
				Connection con = factory.createConnection("admin", "admin");
		//connect to MOM
				con.start();
		//create session
				Session session = con.createSession(/* transaction */false, /* ACK */Session.AUTO_ACKNOWLEDGE);
		//create producer
				MessageProducer producer = session.createProducer(destination);
		//create text message
				Message msg = session.createTextMessage("hello mesage from ActiveMQ");
				producer.send(msg);
				
				Person perso = new Person(maSo, name, ns);
				String xml = new XMLConvert<Person>(perso).object2XML(perso);
				msg = session.createTextMessage(xml);
				producer.send(msg);
		//shutdown connection
				session.close();
				con.close();
				System.out.println("Finished...");
		
		
	}
	
	
}
