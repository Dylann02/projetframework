package src.exception;

import src.utils.UrlMethod;

public class UrlException extends RuntimeException {
    public UrlException(UrlMethod urlMethod) {
        super("L'url "+urlMethod.getUrl()+" / "+urlMethod.getMethodHttp()+" est deja present");
    }
}