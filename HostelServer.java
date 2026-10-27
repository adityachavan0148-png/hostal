import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class HostelServer {
    private static final int PORT = 8080;
    private static final List<Map<String, String>> bookings = new CopyOnWriteArrayList<>();
    private static final Path WEB_DIR = Paths.get("web").toAbsolutePath().normalize();

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/api/rooms", exchange -> {
            addCors(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) { exchange.sendResponseHeaders(204, -1); return; }
            send(exchange, 200, "application/json", "[{\"number\":\"101\",\"type\":\"Single\",\"capacity\":1,\"price\":5000},{\"number\":\"102\",\"type\":\"Double\",\"capacity\":2,\"price\":3500},{\"number\":\"201\",\"type\":\"Triple\",\"capacity\":3,\"price\":2800}]");
        });
        server.createContext("/api/bookings", exchange -> {
            addCors(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) { exchange.sendResponseHeaders(204, -1); return; }
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Map<String, String> data = parseForm(body);
                String name = data.getOrDefault("name", "").trim();
                String phone = data.getOrDefault("phone", "").trim();
                String room = data.getOrDefault("room", "").trim();
                String course = data.getOrDefault("course", "").trim();
                if (name.isEmpty() || phone.isEmpty() || room.isEmpty() || course.isEmpty()) {
                    send(exchange, 400, "application/json", "{\"error\":\"Please fill in all fields.\"}");
                    return;
                }
                Map<String, String> booking = new LinkedHashMap<>();
                booking.put("id", String.valueOf(bookings.size() + 1));
                booking.put("name", name); booking.put("phone", phone);
                booking.put("room", room); booking.put("course", course);
                bookings.add(booking);
                send(exchange, 201, "application/json", toJson(booking));
            } else if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                send(exchange, 200, "application/json", listToJson(bookings));
            } else {
                send(exchange, 405, "application/json", "{\"error\":\"Method not allowed\"}");
            }
        });
        server.createContext("/", exchange -> {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/")) path = "/index.html";
            Path file = WEB_DIR.resolve(path.substring(1)).normalize();
            if (!file.startsWith(WEB_DIR) || !Files.exists(file) || Files.isDirectory(file)) {
                send(exchange, 404, "text/plain; charset=utf-8", "File not found"); return;
            }
            String type = file.toString().endsWith(".css") ? "text/css; charset=utf-8" :
                          file.toString().endsWith(".js") ? "application/javascript; charset=utf-8" : "text/html; charset=utf-8";
            byte[] bytes = Files.readAllBytes(file);
            exchange.getResponseHeaders().set("Content-Type", type);
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream out = exchange.getResponseBody()) { out.write(bytes); }
        });
        server.setExecutor(null);
        server.start();
        System.out.println("Hostel Management System running at http://localhost:" + PORT);
        System.out.println("Press Ctrl+C to stop the server.");
    }

    private static Map<String, String> parseForm(String body) throws UnsupportedEncodingException {
        Map<String, String> map = new HashMap<>();
        for (String pair : body.split("&")) {
            String[] parts = pair.split("=", 2);
            if (parts.length == 2) map.put(URLDecoder.decode(parts[0], "UTF-8"), URLDecoder.decode(parts[1], "UTF-8"));
        }
        return map;
    }
    private static void addCors(HttpExchange e) {
        e.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        e.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        e.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
    }
    private static void send(HttpExchange e, int status, String type, String text) throws IOException {
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        e.getResponseHeaders().set("Content-Type", type + (type.contains("charset") ? "" : "; charset=utf-8"));
        e.sendResponseHeaders(status, bytes.length);
        try (OutputStream out = e.getResponseBody()) { out.write(bytes); }
    }
    private static String escape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "");
    }
    private static String toJson(Map<String, String> map) {
        StringJoiner j = new StringJoiner(",", "{", "}");
        map.forEach((k,v) -> j.add("\"" + escape(k) + "\":\"" + escape(v) + "\""));
        return j.toString();
    }
    private static String listToJson(List<Map<String, String>> list) {
        StringJoiner j = new StringJoiner(",", "[", "]");
        for (Map<String, String> item : list) j.add(toJson(item));
        return j.toString();
    }
}
