package home.com.smarthome.sctp;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ComputerGame extends Thread{

    private static final Map<String, String> CONTENT_TYPES = new HashMap<>(){{
        put("png", "image/png");
        put("html", "text/html");
        put("css", "text/css");
        put("txt", "text/plain");
        put("js", "text/javascript");
        put("svg", "image/svg+xml");
        put("", "text/plain");
    }};

    private static final String NOT_FOUND_MESSAGE = "NOT FOUND";
    private Socket socket;
    private String directory;
    private SctpClient sctpClient;

    public ComputerGame(Socket socket, String directory) {
        this.socket = socket;
        this.directory = directory;
        this.sctpClient = new SctpClient();
        if(this.sctpClient.connect("localhost", 55770)){
            System.out.println("Connect Success");
        }
    }

    @Override
    public void run() {
        try {
            InputStream input = this.socket.getInputStream();
            OutputStream output = this.socket.getOutputStream();
            while (true){
                if(input.available() != 0){
                    HashMap<String, Object> requestText = this.getRequestText(input);
                    System.out.println(requestText);
                    String requestType = (String) requestText.get("requestType");
                    String requestUrl = (String) requestText.get("requestUrl");
                    Configuration cfg = new Configuration(Configuration.VERSION_2_3_30);
                    try {
                        cfg.setDirectoryForTemplateLoading(new File("files"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    cfg.setDefaultEncoding("UTF-8");
                    cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
                    Path filePath = Path.of(this.directory + requestUrl);
                    switch (requestType) {
                        case "POST":
                            switch (requestUrl) {
                                case "/add_game_game.html":
                                    System.out.println("boyyyyy" + requestText.get("requestBody"));
                                    System.out.println("add_game_game");
                                    break;
                                default:
                                    String type = CONTENT_TYPES.get("text");
                                    this.sendHeader(output, 404, "Not Found", type, NOT_FOUND_MESSAGE.length(), requestType);
                                    output.write(NOT_FOUND_MESSAGE.getBytes());
                            }
                            break;
                        case "GET":
                            switch (requestUrl) {
                                case "/index.html":
                                    this.index(filePath, requestType, cfg, output);
                                    break;
                                case "/view_game.html":
                                    this.view_game(filePath, requestType, cfg, output);
                                    break;
                                case "/add_game.html":
                                    this.add_game(filePath, requestType, cfg, output);
                                    break;
                                case "/view_game_id.html":
                                    this.view_game_id(filePath, requestType, cfg, output);
                                    break;
                                case "/edit_game_id.html":
                                    this.edit_game_id(filePath, requestType, cfg, output);
                                    break;
                                case "/delete_game_id.html":
                                    this.delete_game_id(filePath, requestType, cfg, output);
                                    break;
                                default:
                                    String type = CONTENT_TYPES.get("text");
                                    this.sendHeader(output, 404, "Not Found", type, NOT_FOUND_MESSAGE.length(), requestType);
                                    output.write(NOT_FOUND_MESSAGE.getBytes());
                            }
                            break;
                        case "OPTIONS":
                            System.out.println("server handle option request");
                            break;
                        default:
                            System.out.println("DEFAULT");
                    }
                }

            }
        } catch (IOException e){
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }
    }

    private HashMap<String, Object> getRequestText(InputStream input) throws IOException {
        System.out.println(input.available() + "qwe");
        byte[] arr = new byte[input.available()];
        input.read(arr);
        String request = new String(arr);
        System.out.println(request);
        HashMap<String, Object> map = new HashMap<>();
        String[] head = request.split("\n")[0].split(" ");
        map.put("requestType", head[0]);
        map.put("requestUrl", head[1]);
        if(head[0].equals("POST")){
            map.put("requestBody", request.split("\r\n\r\n")[1]);
        }
        return map;
    }

    private void index(Path filePath, String requestType, Configuration cfg, OutputStream output) throws IOException, TemplateException {
        Map<String, Object> root = new HashMap<>();
        root.put("user", "Alexey");
        Template template = cfg.getTemplate("index.html");
        String extension = this.getFileExtension(filePath);
        String type = CONTENT_TYPES.get(extension);

        byte[] fileBytes = Files.readAllBytes(filePath);
        this.sendHeader(output, 200, "OK", type, fileBytes.length, requestType);

        Writer writer = new OutputStreamWriter(output);
        template.process(root, writer);

    }

    private void view_game(Path filePath, String requestType, Configuration cfg, OutputStream output) throws IOException, TemplateException {
        Map<String, Object> root = new HashMap<>();
        ArrayList<Game> games = new ArrayList<>();
        games.add(new Game("Dota 2", "MOBA", "Fantasy", "Valve", "Valve", "Source 2", "Windows"));
        games.add(new Game("CS:GO", "MOBA", "Fantasy", "Valve", "Valve", "Source 2", "Windows"));
        root.put("games", games);
        Template template = cfg.getTemplate("view_game.html");
        String extension = this.getFileExtension(filePath);
        String type = CONTENT_TYPES.get(extension);

        byte[] fileBytes = Files.readAllBytes(filePath);
        this.sendHeader(output, 200, "OK", type, fileBytes.length, requestType);

        Writer writer = new OutputStreamWriter(output);
        template.process(root, writer);
        writer.flush();

    }

    private void add_game(Path filePath, String requestType, Configuration cfg, OutputStream output) throws IOException, TemplateException {
        Map<String, Object> root = new HashMap<>();
        Template template = cfg.getTemplate("add_game.html");
        String extension = this.getFileExtension(filePath);
        String type = CONTENT_TYPES.get(extension);

        byte[] fileBytes = Files.readAllBytes(filePath);
        this.sendHeader(output, 200, "OK", type, fileBytes.length, requestType);

        Writer writer = new OutputStreamWriter(output);
        template.process(root, writer);
        writer.flush();
    }

    private void view_game_id(Path filePath, String requestType, Configuration cfg, OutputStream output) throws IOException, TemplateException {
    }

    private void edit_game_id(Path filePath, String requestType, Configuration cfg, OutputStream output) throws IOException, TemplateException {
    }

    private void delete_game_id(Path filePath, String requestType, Configuration cfg, OutputStream output) throws IOException, TemplateException {
    }
        private String getRequestType(String input){
        return input.split(" ")[0];
    }

    private String getRequestUrl(String input){
        return input.split(" ")[1];
    }

    private String getFileExtension(Path path){
        String name = path.getFileName().toString();
        int extensionStart = name.lastIndexOf(".");
        return  extensionStart == -1 ? "" : name.substring(extensionStart + 1);
    }

    private String sendHeader(OutputStream output, int statusCode, String statusText, String type, long length, String requestType){
        PrintStream ps = new PrintStream(output);
        String answer = "";
        File file = new File("Config.txt");
        try (FileInputStream fin = new FileInputStream(file)) {
            answer = new String(fin.readAllBytes());
            answer = String.format(answer, statusCode, statusText, type, length);
            System.out.println(answer);
            output.write(answer.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return answer;
    }

    private void writeLog(String requestText, String answerText) {
        try{
            File file = new File("//home//alexey//kursach//file.log");
            file.createNewFile();
            FileOutputStream fout = new FileOutputStream(file, false);
            byte[] bufferRequest = requestText.getBytes();
            fout.write(bufferRequest, 0, bufferRequest.length);
            fout.write("\n\n".getBytes());
            byte[] bufferAnswer = answerText.getBytes();
            fout.write(bufferAnswer, 0, bufferAnswer.length);


        } catch(IOException e){
            System.out.println(e);
        }

    }
}