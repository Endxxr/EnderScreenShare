package dev.endxxr.enderss.api.enums;

public enum PluginMessageType {

    START("start"),
    END("end"),
    RELOAD("reload");

    private final String name;
    PluginMessageType(String s) {
        this.name = s;
    }

    public String getString() {
        return name;
    }

}
