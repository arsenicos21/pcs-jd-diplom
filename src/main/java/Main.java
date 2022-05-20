import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        BooleanSearchEngine engine = new BooleanSearchEngine(new File("pcs-jd-diplom//pdfs"));
        int port = 8989;
        start(engine, port);

    }

    public static void start(BooleanSearchEngine engine, int port) {
        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("Сервер запущен");
            while (true) {
                Client client = new Client(port);
                client.run();
                try (Socket socket = server.accept();
                     var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                     PrintWriter out = new PrintWriter(socket.getOutputStream())) {
                    var word = "";
                    while (in.ready()) {
                        word = in.readLine();
                        if (word.equals("end")) {
                            break;
                        }
                        String listJson = search(engine, word);
                        out.write(listJson);
                        out.flush();
                        out.close();
                        PrintOut(listJson);
                    }
                    if (word.equals("end")) {
                        System.out.println("Программа завершена по требованию пользователя");
                        break;
                    }
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        } catch (IOException exception) {
            System.out.println("Сервер не может быть запущен");
            exception.printStackTrace();
        }
    }

    private static void PrintOut(String listJson) {
        var printWriter = new PrintStream(System.out);
        printWriter.println(listJson);
    }

    private static String listToJson(List<PageEntry> list) {
        Type listType = new TypeToken<List<PageEntry>>() {
        }.getType();
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        return gson.toJson(list, listType);
    }

    public static String search(BooleanSearchEngine engine, String word) {
        String listJson = "";
        try {
            var listPageEntry = engine.search(word);
            listJson = listToJson(listPageEntry);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return listJson;
    }
}