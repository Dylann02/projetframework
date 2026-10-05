package src.utils;

import java.util.Objects;

public class UrlMethod {
    private String url;
    private MethodHttp methodHttp;

    public UrlMethod() {}

    public UrlMethod(String url, MethodHttp methodHttp) {
        setUrl(url);
        this.methodHttp = methodHttp;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        if (url != null) {
            // S'assure que l'URL commence toujours par "/"
            this.url = url.startsWith("/") ? url : "/" + url;
        } else {
            this.url = "/";
        }
    }

    public MethodHttp getMethodHttp() {
        return methodHttp;
    }

    public void setMethodHttp(MethodHttp methodHttp) {
        this.methodHttp = methodHttp;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        UrlMethod urlMethod = (UrlMethod) obj;
        return Objects.equals(url, urlMethod.url) 
            && methodHttp == urlMethod.methodHttp;
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, methodHttp);
    } 
}