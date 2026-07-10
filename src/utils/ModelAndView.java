package src.utils;

import java.util.HashMap;

public class ModelAndView {
    private String view;
    private HashMap<String , Object> value;

    public ModelAndView(String view) {
        this.view = view;
        this.value = new HashMap<>();
    }

    public String getView() {
        return view;
    }
    public void setView(String view) {
        this.view = view;
    }
    public HashMap<String, Object> getValue() {
        return value;
    }
    public void setValue(HashMap<String, Object> value) {
        this.value = value;
    }

    public void addAtribute(String key,Object o){
        value.put(key, o);
    }
}
