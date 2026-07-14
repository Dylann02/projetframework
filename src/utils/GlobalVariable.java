package src.utils;

public  class GlobalVariable {
    private static String prefix;
    private static String suffix;
    public static String getPrefix() {
        return prefix;
    }

    
   
    public static String getSuffix() {
        return suffix;
    }



    public static void setPrefix(String prefix) {
        GlobalVariable.prefix = prefix;
    }



    public static void setSuffix(String suffix) {
        GlobalVariable.suffix = suffix;
    }
}
