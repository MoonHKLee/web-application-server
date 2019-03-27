package webserver;

import java.io.*;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.Map;
//what is InputStreamReader????

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

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
            int contentLength = 0;
            while(!line.equals("")){//빈 문자열이 나올 때 까지 요청헤더를 읽어들인다.
                log.debug("header : {}",line);
                line = br.readLine();
                if(line.contains("Content-Length")){
                    contentLength = getContentLength(line);
                }
            }

//            //(get)회원가입 요청url이 있을 시 파싱해서 map에 저장
//            String url = tokens[1];
//            if(url.startsWith("/user/create")){
//                int index = url.indexOf("?");
//                String queryString = url.substring(index+1);
//                Map<String, String>params = HttpRequestUtils.parseQueryString(queryString);
//                User user = new User(params.get("userId"),params.get("password"),params.get("name"),params.get("email"));
//                log.debug("User : {}", user);
//            }else{
//                //요청본문 -- 공백 문자열 다음 줄부터 원하는 경로에서 읽어들이고자 하는 문서를 한 줄씩 읽어들이면 된다.
//                DataOutputStream dos = new DataOutputStream(out);
//                byte[] body = Files.readAllBytes(new File("./webapp" + tokens[1]).toPath());
//
//                //서버에서 클라이언트에 보내는 response
//                // 이것 또한 분석해보면 1줄은상태라인/2줄부터 공백라인까지 응답헤더, 그 이후부터 응답 본문이 적혀있다.
//                response200Header(dos, body.length);
//                responseBody(dos,body);
//
//            }

            //(post)회원가입 요청url이 있을 시 파싱해서 map에 저장
            String url = tokens[1];
            if(("/user/create".equals(url))) {
                String body = IOUtils.readData(br, contentLength);
                Map<String, String> params = HttpRequestUtils.parseQueryString(body);
                User user = new User(params.get("userId"), params.get("password"), params.get("name"), params.get("email"));
                DataBase.addUser(user);
                log.debug("User : {}", user);
                DataOutputStream dos = new DataOutputStream(out);
                response302Header(dos,"/index.html");
            }else if ("/user/login".equals(url)){
                String body = IOUtils.readData(br, contentLength);
                Map<String, String> params = HttpRequestUtils.parseQueryString(body);
                User user = DataBase.findUserById(params.get("userId"));
                if (user == null){
                    responseResource(out, "/user/login_failed.html");
                    return;
                }

                if(user.getPassword().equals(params.get("password"))){
                    DataOutputStream dos = new DataOutputStream(out);
                    response302LoginSuccessHeader(dos);
                }else{
                    responseResource(out, "/user/login_failed.html");
                }
            }else{
                responseResource(out,url);
            }

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseResource(OutputStream out ,String url)throws IOException{
        DataOutputStream dos = new DataOutputStream(out);
        byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());

        //서버에서 클라이언트에 보내는 response
        // 이것 또한 분석해보면 1줄은상태라인/2줄부터 공백라인까지 응답헤더, 그 이후부터 응답 본문이 적혀있다.
        response200Header(dos, body.length);
        responseBody(dos, body);
    }

    private void response302LoginSuccessHeader(DataOutputStream dos){
        try {
            dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
            dos.writeBytes("Set-Cookie: logined=true \r\n");
            dos.writeBytes("Location: /index.html \r\n");
            dos.writeBytes("\r\n");
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

    private void response302Header(DataOutputStream dos, String url){
        try{
            dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
            dos.writeBytes("Location: " + url + " \r\n");
            dos.writeBytes("\r\n");
        }catch(IOException e){
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

    private int getContentLength(String line){
        String[] headerTokens = line.split(":");
        return Integer.parseInt(headerTokens[1].trim());
    }
}