package is.controller.net;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.channels.ClosedChannelException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ClientSocket {

	private Socket sock;
	private BufferedReader in;
	private DataOutputStream out;

	public ClientSocket(InetAddress addr) throws IOException {
		openConnexion(addr, 2013);
		makeRequest(new Request(ERequest.SPECTATOR,
				Collections.<String> emptyList()));
	}
	
	public ClientSocket(InetAddress addr, int port) throws IOException {
		openConnexion(addr, port);
		makeRequest(new Request(ERequest.SPECTATOR,
				Collections.<String> emptyList()));
	}

	public ClientSocket(InetAddress addr, int port, String pseudo) throws IOException {
		openConnexion(addr, port);
		makeRequest(new Request(ERequest.CONNECT, Arrays.asList(pseudo)));
	}

	public ClientSocket(InetAddress addr, int port, String pseudo, String pass,
			boolean isRegister) throws IOException {
		openConnexion(addr, port);
		makeRequest(new Request(
				isRegister ? ERequest.REGISTER : ERequest.LOGIN, Arrays.asList(
						pseudo, pass)));
	}

	private void openConnexion(InetAddress addr, int port) throws IOException {
		sock = new Socket(addr, port);
		in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		out = new DataOutputStream(sock.getOutputStream());
	}

	public void makeRequest(Request r) throws IOException {
		out.write((r.toString() + "\n").getBytes());
		out.flush();
	}

	public Response receiveResponse() throws ParseException,
			ClosedChannelException, IOException {
		return parseResponse(new String(in.readLine()).trim());
	}
	public void close() throws IOException {
		sock.close();
	}

	private static Response parseResponse(String s) throws ParseException {
		String[] command = s.split("(?<!\\\\)/");
		List<String> list = new ArrayList<String>();
		for (int i = 1; i < command.length; i++)
			list.add(command[i]);
		return new Response(command[0], list);
	}

}
