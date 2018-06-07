package asistente;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {
	
	private Socket socketCliente;
	private int puerto;
	private ClienteGUI ventana;
	private boolean mensajesALeer;
	
	public Cliente(int puerto) {
		this.puerto = puerto;
		this.ventana = new ClienteGUI(this);
		this.mensajesALeer = false;
	}
	
	public void notificar() {
		System.out.println("notificado");
		this.mensajesALeer = true;
	}
	
	public boolean mensajeDisponible() {
		return this.mensajesALeer;
	}
	
	public class Conectar implements Runnable {

		@Override
		public void run() {
			try {
			//System.out.println("Cliente creado");
			//Me conecto al cliente, usando el localhost:puertoSV
			socketCliente = new Socket("127.0.0.1", puerto);
			
			//En cada cliente creo e inicio un thread para escribir al sv
			Scanner sc = new Scanner(System.in);
			Escribir esc = new Escribir(socketCliente, Thread.currentThread().getName(), sc);
			Thread esc_thread = new Thread(esc);
			esc_thread.start();
			
			//En cada cliente creo e inicio un thread para leer lo que viene del sv
			Leer leer = new Leer(socketCliente);
			Thread leer_thread = new Thread(leer);
			leer_thread.start();
			
			} catch(IOException e) {
				System.out.println("Error al conectar al servidor y/o inicializar los threads");
			}
		}
	}
	
	public class Leer implements Runnable {

		private ObjectInputStream in;
		private String msj;
		
		Leer(Socket socketCliente) throws IOException {
			//Inicializo el flujo de entrada del socket
			in = new ObjectInputStream(socketCliente.getInputStream());
		}
		
		@Override
		public void run() {
			do {
				if(in != null) {
					try {
						msj = (String) in.readObject();
					} catch (IOException | ClassNotFoundException e) {
						System.out.println("Error al leer. Puede que el cliente se haya desconectado");
					}
				} 
				
				if(!msj.equals("Salir")) {
					ventana.imprimir(msj);
//
//					try {
//						Thread.sleep(1000);
//					} catch (InterruptedException e) { }
				} else {
					in = null;
				}
			} while(in != null);
		}
	}

	public class Escribir implements Runnable {

		private ObjectOutputStream out;
		//private Scanner teclado;
		
		Escribir(Socket socketCliente, String nombreThread, Scanner sc) throws IOException {
			//Inicializo el flujo de salida del socket
			out = new ObjectOutputStream(socketCliente.getOutputStream());
			//teclado = sc;
		}
		
		@Override
		public void run() {
			
			String str = null;
			
			do {
				//System.out.println(mensajeDisponible());
				if(mensajeDisponible() == true) {
					System.out.println("TRUE");
					str = ventana.getMensaje();
					System.out.println(str);
					mensajesALeer = false;
					
					try {
						//System.out.println("Escribiendo al sv");
						out.writeObject(str);
						out.flush();
					} catch (IOException e) {
						System.out.println("Error al escribir en el servidor");
					}
				}
				//System.out.println();
//				if(!str.equals("Salir"))
//					str = nombre + " - " + str;

				
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} while(str == null || !str.equals("Salir"));
			//teclado.close();
		}
	}
	
	public static void main(String[] args) throws IOException {
		
		//Creo los clientes
		Cliente cl1 = new Cliente(10001);
		//Cliente cl2 = new Cliente(10001);
		
		//Conecto los clientes al servidor
		Thread cl1_con = new Thread(cl1.new Conectar());
		cl1_con.start();
		
//		Thread cl2_con = new Thread(cl2.new Conectar());
//		cl2_con.setName("CL2");
//		cl2_con.start();
	}
}
