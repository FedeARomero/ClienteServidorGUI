package asistente;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ClienteGUI {

	private JFrame frame;
	private JTextPane mensajesChat;
	private JScrollPane barraScroll;
	private JTextField escribir;
	private Cliente motor;
	
	/*
	/**
	 * Launch the application.
	 *
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClienteGUI window = new ClienteGUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	*/
	
	/**
	 * Create the application.
	 * 
	 */
	public ClienteGUI(Cliente motor) {
		this.motor = motor;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 *
	 */
	private void initialize() {
		
		frame = new JFrame();
		frame.setBounds(100, 100, 1000, 700);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setResizable(false); 							//desactivo el maximizar
		
		mensajesChat = new JTextPane();
		mensajesChat.setEditable(false);
		mensajesChat.setFont(new Font("Tahoma", Font.PLAIN, 15));
		mensajesChat.setBounds(12, 13, 970, 604);
		mensajesChat.insertIcon(new ImageIcon("C:\\Users\\Daniel\\Desktop\\ff.png"));
		
		barraScroll = new JScrollPane(mensajesChat);
		barraScroll.setBounds(12, 13, 970, 604);
		
		frame.getContentPane().add(barraScroll);
		
		escribir = new JTextField();
		escribir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				motor.notificar();
			}
		});
		escribir.setBounds(12, 630, 868, 22);
		frame.getContentPane().add(escribir);
		escribir.setColumns(10);
		
		JButton btnEnviar = new JButton("Enviar");
		btnEnviar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				motor.notificar();
			}
		});
		btnEnviar.setBounds(892, 629, 90, 25);
		frame.getContentPane().add(btnEnviar);
		
		frame.setVisible(true);
	}
	
	public void imprimir(String str) {
		if(!str.equals("")) {
			Document doc = mensajesChat.getDocument();
			try {
				doc.insertString(doc.getLength(), ("\n" + str), null);
			} catch (BadLocationException e) {
				//e.printStackTrace();
			}
		}
	}
	
	public String getMensaje() {
		String msj = escribir.getText();
		escribir.setText("");
		
		return msj;
	}
}
