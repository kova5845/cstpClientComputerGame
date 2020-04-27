package home.com.smarthome.sctp;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
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
                    switch (requestType) {
                        case "POST":
                            System.out.println("server handle post request");
                            break;
                        case "GET":
                            Path filePath = Path.of(this.directory + requestUrl);
                            switch (requestUrl) {
                                case "/index.html":
                                    this.index(filePath, requestType, cfg, output);
                                    break;
                                case "/view_game.html":
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
        Scanner reader = new Scanner(input);
        String line = "";
        HashMap<String, Object> map = new HashMap<>();
        String[] head = reader.nextLine().split(" ");
        map.put("requestType", head[0]);
        map.put("requestUrl", head[1]);
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
        root.put("games", "Alexey");
        Template template = cfg.getTemplate("index.html");
        String extension = this.getFileExtension(filePath);
        String type = CONTENT_TYPES.get(extension);

        byte[] fileBytes = Files.readAllBytes(filePath);
        this.sendHeader(output, 200, "OK", type, fileBytes.length, requestType);

        Writer writer = new OutputStreamWriter(output);
        template.process(root, writer);

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
        File file = new File("//home//alexey//kursach//Config.txt");
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