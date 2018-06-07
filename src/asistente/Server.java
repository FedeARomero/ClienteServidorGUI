package asistente;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Server {

	private HashMap<Socket,ObjectOutputStream> listaClientes;	//Lista de sockets de los clientes
	private ServerSocket listener;	//Socket del sv para escuchar a los nuevos clientes
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private int idCliente;
	
	public Server(int puerto) throws IOException {
		listaClientes = new HashMap<Socket,ObjectOutputStream>();
		listener = new ServerSocket(puerto);
		idCliente = 1;
	}
	
	public class Escuchar implements Runnable {
		
		@Override
		public void run() {
			System.out.println("SERVIDOR CREADO EN EL PUERTO " + listener.getLocalPort());
			try {
				for(int i=0; i<2; i++) {
					Socket socketCliente = listener.accept(); 
					System.out.println("Conexion de " + socketCliente.getInetAddress() + ":" + socketCliente.getPort());
					in = new ObjectInputStream(socketCliente.getInputStream());	//Inicializo flujo para leer data del cliente
					out = new ObjectOutputStream(socketCliente.getOutputStream());	//Inicializo flujo para escribir data al cliente
					listaClientes.put(socketCliente, out);	//Al cliente aceptado lo agrego en el hashmap
					
					//Thread para leer al cliente (uno por cada cliente)
					LeerCliente lc_thread = new LeerCliente(socketCliente, in, out, idCliente);
					Thread leer = new Thread(lc_thread);
					leer.start();
					out.writeObject("CLIENTE " + idCliente);
					idCliente++;
				}
				
				listener.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public class LeerCliente implements Runnable {
		
		private Socket socketCliente;
		private ObjectInputStream in;			
		private ObjectOutputStream out;	
		private String str;
		private int id;
		
		LeerCliente(Socket cliente, ObjectInputStream in, ObjectOutputStream out, int idCliente) throws IOException {
			//in = new ObjectInputStream(cliente.getInputStream());
			this.in = in;
			this.out = out;
			socketCliente = cliente;
			id = idCliente;
		}
		
		@Override
		public void run() {
			do {
				try {
					str = (String) in.readObject();
				} catch (IOException | ClassNotFoundException e) {
					System.out.println("Error al leer");
				}
					
				if(!str.equals("Salir")) {
					str = "Mensaje del cliente " + id + ": " + str;
					System.out.println(str);
					
					for(Socket cliente : listaClientes.keySet()) {
						if(cliente != socketCliente) {
							try {
								listaClientes.get(cliente).writeObject(str);
							} catch (IOException e) {
								System.out.println("Error al reenviar el mensaje al resto de los clientes");
							}
						}
					}
//					try {
//						Thread.sleep(1000);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
				} else {
					System.out.println("Desconectando cliente");
					
					try {
						out.writeObject(str);
					} catch (IOException e1) {
						System.out.println("Error al notificar al cliente");
					}
					
					try {
						in.close();
						out.close();
						socketCliente.close();
					} catch (IOException e) {
						System.out.println("Error al cerrar el socket y los flujos");
					}
					listaClientes.remove(socketCliente);
				}
			} while(!str.equals("Salir"));
		}
	}
	
	public static void main(String[] args) throws IOException {
		
		//Inicio el servidor
		Server servidor = new Server(10001);
		Thread ts = new Thread(servidor.new Escuchar());
		ts.start();
	}
}


