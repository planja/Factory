/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package factory.model;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Gulya
 */
public class IMTAward {

    public boolean split = false;
    private List<IMTInfo> class_list = new LinkedList<IMTInfo>();
    private List<IMTFlight> flight_list = new LinkedList<IMTFlight>();
    private String total_duration;
    private List<ExtraData> extra_data = new LinkedList<ExtraData>();

    /**
     * @return the class_list
     */
    public List<IMTInfo> getClass_list() {
        return class_list;
    }

    /**
     * @param class_list the class_list to set
     */
    public void setClass_list(List<IMTInfo> class_list) {
        this.class_list = class_list;
    }

    /**
     * @return the flight_list
     */
    public List<IMTFlight> getFlight_list() {
        return flight_list;
    }

    /**
     * @param flight_list the flight_list to set
     */
    public void setFlight_list(List<IMTFlight> flight_list) {
        this.flight_list = flight_list;
    }

    /**
     * @return the total_duration
     */
    public String getTotal_duration() {
        return total_duration == null ? "" : total_duration;
    }

    /**
     * @param total_duration the total_duration to set
     */
    public void setTotal_duration(String total_duration) {
        this.total_duration = total_duration;
    }

    /**
     * @return the extra_data
     */
    public List<ExtraData> getExtra_data() {
        return extra_data;
    }

    /**
     * @param extra_data the extra_data to set
     */
    public void setExtra_data(List<ExtraData> extra_data) {
        this.extra_data = extra_data;
    }
}
