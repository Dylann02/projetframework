package src.utils;

import java.util.Objects;

public class UrlMethod {
    private String url;
    private MethodHttp methodHttp;

    public UrlMethod(){}
    public UrlMethod(String url, MethodHttp methodHttp) {
        this.url = url;
        this.methodHttp = methodHttp;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public MethodHttp getMethodHttp() {
        return methodHttp;
    }

    public void setMethodHttp(MethodHttp methodHttp) {
        this.methodHttp = methodHttp;
    }

    @Override
    public boolean equals(Object obj){
        UrlMethod urlMethod = (UrlMethod) obj;
        return url.equals(urlMethod.getUrl())
                && methodHttp == urlMethod.getMethodHttp();
    }
    @Override
    public int hashCode(){
        return Objects.hash(url,methodHttp);
    }
}
