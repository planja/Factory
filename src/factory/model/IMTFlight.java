/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package factory.model;

/**
 * @author Gulya
 */
public class IMTFlight {

    private String depart_time;
    private String depart_date;
    private String depart_place;
    private String depart_code;

    private String arrive_time;
    private String arrive_date;
    private String arrive_place;
    private String arrive_code;

    private String travel_time;

    private String flight_number;
    private String airline_company;
    private String aircraft;
    private String meal;
    private String flight_cabin;
    private Integer id;

    private String layover_time;
//    private String flight_award;

//    private String connection;

    /**
     * @return the depart_time
     */
    public String getDepart_time() {
        return depart_time == null ? "" : depart_time;
    }

    /**
     * @param depart_time the depart_time to set
     */
    public void setDepart_time(String depart_time) {
        this.depart_time = depart_time;
    }

    /**
     * @return the depart_Date
     */
    public String getDepart_date() {
        return depart_date == null ? "" : depart_date;
    }

    /**
     * @param depart_Date the depart_Date to set
     */
    public void setDepart_date(String depart_date) {
        this.depart_date = depart_date;
    }

    /**
     * @return the depart_place
     */
    public String getDepart_place() {
        return depart_place == null ? "" : depart_place;
    }

    /**
     * @param depart_place the depart_place to set
     */
    public void setDepart_place(String depart_place) {
        this.depart_place = depart_place;
    }

    /**
     * @return the arrive_time
     */
    public String getArrive_time() {
        return arrive_time == null ? "" : arrive_time;
    }

    /**
     * @param arrive_time the arrive_time to set
     */
    public void setArrive_time(String arrive_time) {
        this.arrive_time = arrive_time;
    }

    /**
     * @return the arrive_date
     */
    public String getArrive_date() {
        return arrive_date == null ? "" : arrive_date;
    }

    /**
     * @param arrive_date the arrive_date to set
     */
    public void setArrive_date(String arrive_date) {
        this.arrive_date = arrive_date;
    }

    /**
     * @return the arrive_place
     */
    public String getArrive_place() {
        return arrive_place == null ? "" : arrive_place;
    }

    /**
     * @param arrive_place the arrive_place to set
     */
    public void setArrive_place(String arrive_place) {
        this.arrive_place = arrive_place;
    }

    /**
     * @return the travel_time
     */
    public String getTravel_time() {
        return travel_time == null ? "" : travel_time;
    }

    /**
     * @param travel_time the travel_time to set
     */
    public void setTravel_time(String travel_time) {
        this.travel_time = travel_time;
    }

    /**
     * @return the flight_number
     */
    public String getFlight_number() {
        return flight_number == null ? "" : flight_number;
    }

    /**
     * @param flight_number the flight_number to set
     */
    public void setFlight_number(String flight_number) {
        this.flight_number = flight_number;
    }

    /**
     * @return the aircraft
     */
    public String getAircraft() {
        return aircraft == null ? "" : aircraft;
    }

    /**
     * @param aircraft the aircraft to set
     */
    public void setAircraft(String aircraft) {
        this.aircraft = aircraft;
    }

    /**
     * @return the meal
     */
    public String getMeal() {
        return meal == null ? "" : meal;
    }

    /**
     * @param meal the meal to set
     */
    public void setMeal(String meal) {
        this.meal = meal;
    }

    /**
     * @return the flight_cabin
     */
    public String getFlight_cabin() {
        return flight_cabin == null ? "" : flight_cabin;
    }

    /**
     * @param flight_cabin the flight_cabin to set
     */
    public void setFlight_cabin(String flight_cabin) {
        this.flight_cabin = flight_cabin;
    }

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return the layover_time
     */
    public String getLayover_time() {
        return layover_time;
    }

    /**
     * @param layover_time the layover_time to set
     */
    public void setLayover_time(String layover_time) {
        this.layover_time = layover_time;
    }

    /**
     * @return the airline_company
     */
    public String getAirline_company() {
        return airline_company;
    }

    /**
     * @param airline_company the airline_company to set
     */
    public void setAirline_company(String airline_company) {
        this.airline_company = airline_company;
    }

    /**
     * @return the depart_code
     */
    public String getDepart_code() {
        return depart_code;
    }

    /**
     * @param depart_code the depart_code to set
     */
    public void setDepart_code(String depart_code) {
        this.depart_code = depart_code;
    }

    /**
     * @return the arrive_code
     */
    public String getArrive_code() {
        return arrive_code;
    }

    /**
     * @param arrive_code the arrive_code to set
     */
    public void setArrive_code(String arrive_code) {
        this.arrive_code = arrive_code;
    }

    /**
     * @return the connection
     */
//    public String getConnection() {
//        return connection;
//    }
//
//    /**
//     * @param connection the connection to set
//     */
//    public void setConnection(String connection) {
//        this.connection = connection;
//    }
//
//    /**
//     * @return the flight_award
//     */
//    public String getFlight_award() {
//        return flight_award;
//    }
//
//    /**
//     * @param flight_award the flight_award to set
//     */
//    public void setFlight_award(String flight_award) {
//        this.flight_award = flight_award;
//    }
}
