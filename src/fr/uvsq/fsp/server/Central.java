package fr.uvsq.fsp.server;

import fr.uvsq.fsp.server.FSPCentral;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Central {

	public static void main(String[] args) {
		int port;
		Scanner scan;

		if (args.length == 1) {
			port = Integer.parseInt(args[0]);
		} else {
			scan = new Scanner(System.in);
			System.out.print("Port : ");
			port = Integer.parseInt(scan.nextLine());
		}


		new Thread(new WaitingServer(port)).start();

		boolean loop = true;

		System.out.println("Tapez QUIT pour quitter le programme.");
		scan = new Scanner(System.in);

		while (loop) {
			System.out.print("> ");

			if (scan.nextLine().equals("QUIT")) {
				loop = false;
			}
		}
	}
}

