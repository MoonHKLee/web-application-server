package webserver;

import java.io.*;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
//what is InputStreamReader????

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            BufferedReader br = new BufferedReader(new InputStreamReader(in,"UTF-8"));

            //요청라인 -- http 규약에서 맨 첫 번째 줄은 무조건 요청 라인이다.
            String line = br.readLine();
            log.debug("request line : {}",line);
            if (line ==null){
                return;
            }

            //요청헤더 -- 2번째 줄부터 빈 문자열이 나올때 까지가 전부 http 요청 헤더에 포함된다
            //여기서는 클라이언트가 서버에 보내는 http 요청 헤더이다.
            String[] tokens = line.split(" ");
            while(!line.equals("")){//빈 문자열이 나올 때 까지 요청헤더를 읽어들인다.
                line = br.readLine();
                log.debug("header : {}",line);
            }

            //요청본문 -- 공백 문자열 다음 줄부터 원하는 경로에서 읽어들이고자 하는 문서를 한 줄씩 읽어들이면 된다.
            DataOutputStream dos = new DataOutputStream(out);
            byte[] body = Files.readAllBytes(new File("./webapp" + tokens[1]).toPath());

            //서버에서 클라이언트에 보내는 response
            // 이것 또한 분석해보면 1줄은상태라인/2줄부터 공백라인까지 응답헤더, 그 이후부터 응답 본문이 적혀있다.
            response200Header(dos, body.length);
            responseBody(dos,body);

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}