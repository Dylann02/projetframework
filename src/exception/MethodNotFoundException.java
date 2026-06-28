package src.exception;

import java.lang.reflect.Method;
import java.util.HashMap;

public class MethodNotFoundException extends Exception{
    public MethodNotFoundException (HashMap<String , Method> listeUrl){
        super("L'url n'est pas valide , voila la liste des url disponible : "+listeUrl);
    }
}
