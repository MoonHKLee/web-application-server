package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class RequestLine {
    private static final Logger log = LoggerFactory.getLogger(RequestLine.class);

    private HttpMethod method;
    private String path;
    private Map<String,String> params = new HashMap<String,String>();

    public RequestLine(String requestLine){
        log.debug("request line : {}", requestLine);

        //http 리퀘스트 첫번째 줄에서 " "을 기준으로 스플릿하면 0번째 문장은 method가 된다.
        String[] tokens = requestLine.split(" ");

        //스플릿을 했을때 3부분으로 나누어져있어야 한다. 아니라면 throw던져서 예외처리.
        if (tokens.length!=3){
            throw new IllegalArgumentException(requestLine + "이 형식에 맞지 않습니다.");
        }
        method = HttpMethod.valueOf(tokens[0]);

        if (method.isPost()){
            path = tokens[1];
            return;
        }

        // "?"의 인덱스를 파악한다.
        int index = tokens[1].indexOf("?");

        //GET메소드를 통해 전달되는 파라미터가 없다면 index == -1 이므로 token[1]이 path가 된다.
        if(index == -1){
            path = tokens[1];
        }else{
            //GET메소드를 통해 전달되는 파라미터가 있다면 "?" 이전의 문자열은 path에, 이후의 문자열들은 파싱해서 Map에 저장해준다.
            path = tokens[1].substring(0,index);
            params = HttpRequestUtils.parseQueryString(tokens[1].substring(index+1));
        }
    }

    public HttpMethod getMethod(){
        return method;
    }

    public String getPath(){
        return path;
    }

    public Map<String,String> getParams(){
        return params;
    }
}
