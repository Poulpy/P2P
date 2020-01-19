package abstractions;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Yoda {

	protected BufferedReader reader;
	protected PrintWriter writer;

	protected String adresseIP = "127.0.0.1";
	protected String adresseIPServeur = "127.0.0.1";
	protected int port = 50000;
	protected Socket socket;

    protected Yoda() {
    }


	/**
	 * Ouvre une socket pour le client
	 * TODO: gérer plusieurs clients, méthode pour ouvrir une socket seulement
	 * pour le client ? Les sockets des clients seraient stockées dans un
	 * tableau
	 */
	protected void open() {
		try {
			socket = new Socket(adresseIPServeur, port);
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendFile(String file) throws IOException {
		DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
		FileInputStream fis = new FileInputStream(file);
		byte[] buffer = new byte[4096];

		while (fis.read(buffer) > 0) {
			dos.write(buffer);
		}

		fis.close();
		dos.close();
	}

	private void saveFile() throws IOException {
		DataInputStream dis = new DataInputStream(socket.getInputStream());
		FileOutputStream fos = new FileOutputStream("shared2/download.txt");
		byte[] buffer = new byte[4096];

		int filesize = 15123; // Send file size in separate msg
		int read = 0;
		int totalRead = 0;
		int remaining = filesize;
		while((read = dis.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
			totalRead += read;
			remaining -= read;
			System.out.println("read " + totalRead + " bytes.");
			fos.write(buffer, 0, read);
		}

		fos.close();
		dis.close();
	}
	protected void receiveFile() {
	}

	protected String lireMessage() throws IOException {
		String str;
		str = reader.readLine();
		return str;
	}

	protected void envoyerMessage(String msg) {
		System.out.println(msg);
		writer.println(msg);
	}

	/**
	 * Ferme la socket du client
	 * Après que le client ait envoyé la commande QUIT
	 */
	protected void close() {
		try {
			socket.close();
			reader.close();
			writer.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

