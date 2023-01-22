package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    private static Scanner inputPlayerOne, inputPlayerTwo;
    private static PrintWriter outputPlayerOne, outputPlayerTwo;

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(4445);

            Socket clientSocketOne = serverSocket.accept();
            Socket clientSocketTwo = serverSocket.accept();

            PlayerColor colorPlayerOne, colorPlayerTwo;
            if ((int) (Math.random() * 2) == 0) {
                colorPlayerOne = PlayerColor.WHITE;
                colorPlayerTwo = PlayerColor.BLACK;
            } else {
                colorPlayerOne = PlayerColor.BLACK;
                colorPlayerTwo = PlayerColor.WHITE;
            }

            ClientHandler server = new ClientHandler(clientSocketOne, clientSocketTwo, colorPlayerOne, colorPlayerTwo);

            boolean playing = true;

            if (colorPlayerTwo == PlayerColor.WHITE) {
                System.out.println("Sivalletto(?)");
                playing = server.send(inputPlayerTwo, outputPlayerOne);
            }
            while (playing) {
                playing = server.send(inputPlayerOne, outputPlayerTwo);

                if (!playing) break;

                playing = server.send(inputPlayerTwo, outputPlayerOne);
            }
        } finally {
            if (serverSocket == null)
                System.exit(-1);

            serverSocket.close();
            inputPlayerOne.close();
            inputPlayerTwo.close();
            outputPlayerOne.close();
            outputPlayerTwo.close();
        }
    }

    private static class ClientHandler {
        public ClientHandler(Socket clientPlayerOne, Socket clientPlayerTwo, PlayerColor colorPlayerOne, PlayerColor colorPlayerTwo) {
//            Scanner inputPlayerOne, inputPlayerTwo;
//            PrintWriter outputPlayerOne, outputPlayerTwo;
            try {
                inputPlayerOne = new Scanner(clientPlayerOne.getInputStream());
                outputPlayerOne = new PrintWriter(clientPlayerOne.getOutputStream(), true);

                inputPlayerTwo = new Scanner(clientPlayerTwo.getInputStream());
                outputPlayerTwo = new PrintWriter(clientPlayerTwo.getOutputStream(), true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            outputPlayerOne.println(colorPlayerOne);
            outputPlayerTwo.println(colorPlayerTwo);
        }

        public boolean send(Scanner sender, PrintWriter receiver) {
            String serializedMove;
            // Read from sender client
            serializedMove = sender.nextLine();
            System.out.println("Read " + serializedMove);

            Packet packet = null;
            try {
                packet = Packet.fromString(serializedMove);
            } catch (Exception e) {
                System.err.println("Error serializing the packet");
                System.exit(-1);
            }

            packet.from.y = 7 - packet.from.y;
            packet.to.y = 7 - packet.to.y;

            try {
                serializedMove = packet.serializeToString();
            } catch (IOException e) {
                System.err.println("Error serializing the packet");
                System.exit(-1);
            }

            // Send to other client
            receiver.println(serializedMove);

            return packet.endGame;
        }
    }
}
