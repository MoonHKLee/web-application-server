package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

public class HttpRequest {
    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

    private String method;
    private String path;
    private Map<String,String> header;
    private Map<String,String> params;

    public HttpRequest(InputStream in){
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String line = br.readLine();

            if (line == null) {
                return;
            }
            processRequestLine(line);

        }catch (IOException e){

        }


    }

    private void processRequestLine(String requestLine){
        log.debug("request line : {}", requestLine);

        //http 리퀘스트 첫번째 줄에서 " "을 기준으로 스플릿하면 0번째 문장은 method가 된다.
        String[] tokens = requestLine.split(" ");
        this.method=tokens[0];

        //http 메소드가 POST라면 " "를 기준으로 우측에 있는 String이 path에 해당한다.
        if ("POST".equals(method)){
            path = tokens[1];
            //method와 path가 정해졌다면 더이상 현재 메소드를 진행할 이유가 없으므로 return 한다.
            return;
        }

        //http 리퀘스트에서 GET 메소드를 사용하지 않는다면  token

        this.path =tokens[1].split("\\?")[0];
    }

    public String getMethod(){
        return method;
    }

    public String getPath(){
        return path;
    }
    public Map getHeader(){
        return header;
    }
    public Map getParameter(){
        return params;
    }
}
