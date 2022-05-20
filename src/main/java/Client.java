import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client extends Socket {

    static Socket socket;
    private final int port;

    public Client(int port) {
        this.port = port;
        this.start();
    }

    private void start() {
        try {
            socket = new Socket("localhost", port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() throws IOException {
        try (var out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Введите слово для поиска");
            System.out.println("Для завершения работы програмы введите `end`");
            String input = scanner.nextLine();
            if ("end".equals(input)) {
                input = "end";
            }
            out.write(input);
            out.flush();
        }
    }
}