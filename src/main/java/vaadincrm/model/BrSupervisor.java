package vaadincrm.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by someone on 14-Jul-2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BrSupervisor extends Employee implements Model {
    public static final String distributionHouse = "distributionHouse";
    BrSupervisor() {}
}
