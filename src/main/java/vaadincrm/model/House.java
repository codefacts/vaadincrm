package vaadincrm.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class House implements Serializable, Model {
    public static final String name = "name";
    public static final String area = "area";
    public static final String locations = "location";
    public static final String active = "active";

    House() {
    }
}
