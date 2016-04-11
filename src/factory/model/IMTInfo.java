/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package factory.model;

/**
 * @author Gulya
 */
public class IMTInfo {

    public static final String ECONOMY = "E";
    public static final String BUSINESS = "B";
    public static final String FIRST = "F";
    public static final String PREMIUM_ECONOMY = "PE";

    public static final String SAVER = "SAVER";
    public static final String STANDART = "STANDART";
    public static final String FULL = "FULL";
    public static final String SAVER_STANDART = "SAVER STANDART";
    public static final String STANDART_FULL = "STANDART FULL";

    public static final String NA = "NA";
    public static final String AVAILABLE = "AVAILABLE";
    public static final String WAITLIST = "WAITLIST";

    private String name;
    private String status;
    private String mileage;
    private String tax;
    private String currency;
    private Integer id;

//    private String class_description;

//    private List<String> mixed_cabins = new LinkedList<String>();

    /**
     * @return the name
     */
    public String getName() {
        return name == null ? "" : name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status == null ? NA : status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the mileage
     */
    public String getMileage() {
        return mileage == null ? "" : mileage;
    }

    /**
     * @param mileage the mileage to set
     */
    public void setMileage(String mileage) {
        this.mileage = mileage;
    }

    /**
     * @return the tax
     */
    public String getTax() {
        return tax == null ? "" : tax;
    }

    /**
     * @param tax the tax to set
     */
    public void setTax(String tax) {
        this.tax = tax;
    }

//    /**
//     * @return the class_description
//     */
//    public String getClass_description() {
//        return class_description;
//    }
//
//    /**
//     * @param class_description the class_description to set
//     */
//    public void setClass_description(String class_description) {
//        this.class_description = class_description;
//    }
//
//    /**
//     * @return the mixed_cabins
//     */
//    public List<String> getMixed_cabins() {
//        return mixed_cabins;
//    }
//
//    /**
//     * @param mixed_cabins the mixed_cabins to set
//     */
//    public void setMixed_cabins(List<String> mixed_cabins) {
//        this.mixed_cabins = mixed_cabins;
//    }

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
     * @return the currency
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * @param currency the currency to set
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
