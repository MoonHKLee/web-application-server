package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

    private HttpMethod method;
    private String path;
    private Map<String,String> headers = new HashMap<String,String>();
    private Map<String,String> params = new HashMap<String,String>();
    private RequestLine requestLine;

    public HttpRequest(InputStream in){
        try{
            //퍼버 리더를 통해서 HTTP 문서의 내용을 첫줄만 문자열로 읽어들인다.
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String line = br.readLine();

            // HTTP request 만 불러들인다.
            if (line == null) {
                return;
            }

            //HttpRequest 라인에서 method 종류, path, parameter 를 파악하고 저장한다.
            requestLine = new RequestLine(line);

            //http 헤더파일을 끝까지 불러들여 읽는다. 동시에 ":"를 기준으로 헤더에 Map 형태로 key:value를 집어넣는다.
            line = br.readLine();
            while(!line.equals("")){
                log.debug("header : {}", line);
                String[] tokens = line.split(":");
                headers.put(tokens[0].trim(),tokens[1].trim());
                line = br.readLine();
            }

            //만약에 메소드가 POST형태 였다면 parameter가 헤더파일 공백 이후에 나타나므로 이곳에서 params를 대입한다.
            if(requestLine.getMethod().isPost()){
                String body = IOUtils.readData(br,Integer.parseInt(headers.get("Content-Length")));
                params = HttpRequestUtils.parseQueryString(body);
            }else{
                params = requestLine.getParams();
            }
        }catch (IOException io){
            log.error(io.getMessage());
        }
    }

    public HttpMethod getMethod(){
        return requestLine.getMethod();
    }

    public String getPath(){
        return requestLine.getPath();
    }
    public String getHeader(String key){
        return headers.get(key);
    }
    public String getParameter(String key){
        return params.get(key);
    }
}
