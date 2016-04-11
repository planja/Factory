package factory.parser;

import com.google.gson.Gson;
import factory.ParserResultHandler;
import factory.db.manager.DatabaseManager;
import factory.model.*;
import factory.utils.Utils;
import parser.model.Award;
import parser.model.Flight;
import parser.model.Info;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

import static parser.Parser.*;

public class UA implements ParserResultHandler {

    @Override
    public List<IMTAward> handleResponse(List<Award> flights, String flightClass, String seats, IMTError error, String requestId, String userId, String from, String to) throws Exception {
        String result = "";
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy:MM:dd");

        SimpleDateFormat ua_sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");

        SimpleDateFormat time_format_pac = new SimpleDateFormat("h:mm a");
        SimpleDateFormat time_format_res = new SimpleDateFormat("HH:mm");

        SimpleDateFormat time_format = new SimpleDateFormat("yyyy:MM:dd HH:mm");
        List<IMTAward> awardList = new LinkedList<IMTAward>();

        DatabaseManager dm = new DatabaseManager();

        for (Award item : flights) {

            int info_id = 0;

            IMTAward award = new IMTAward();

//                    if (flightClass.equals(ALL)) {
//                        Info infoSE = item.getSaverEconomy();
//                        Info infoSTE = item.getStandartEconomy();
//
//                        if (infoSE != null || infoSTE != null) {
//
//                            info_id = getCabinClass(item.getSaverEconomy(), ECONOMY, IMTInfo.SAVER, info_id, award);
//                            info_id = getCabinClass(item.getStandartEconomy(), ECONOMY, IMTInfo.FULL, info_id, award);
//                        }
//
//                        Info infoSB = item.getSaverBusiness();
//                        Info infoSTB = item.getStandartBusiness();
//
//                        if (infoSB != null || infoSTB != null) {
//
//                            info_id = getCabinClass(item.getSaverBusiness(), BUSINESS, IMTInfo.SAVER, info_id, award);
//                            info_id = getCabinClass(item.getStandartBusiness(), BUSINESS, IMTInfo.FULL, info_id, award);
//                        }
//
//                        Info infoSF = item.getSaverFirst();
//                        Info infoSTF = item.getStandartFirst();
//
//                        if (infoSF != null || infoSTF != null) {
//
//                            info_id = getCabinClass(item.getSaverFirst(), FIRST, IMTInfo.SAVER, info_id, award);
//                            info_id = getCabinClass(item.getStandartFirst(), FIRST, IMTInfo.FULL, info_id, award);
//                        }
//                    }
            for (String cabinItem : Utils.getCabins(flightClass)) {

                if (cabinItem.equals(ECONOMY)) {

                    Info infoS = item.getSaverEconomy();
                    Info info = item.getStandartEconomy();

                    if (infoS != null || info != null) {

                        info_id = getCabinClass(item.getSaverEconomy(), ECONOMY, IMTInfo.SAVER, info_id, award, item.getFlights());
                        info_id = getCabinClass(item.getStandartEconomy(), ECONOMY, IMTInfo.FULL, info_id, award, item.getFlights());
                    }

                } else if (cabinItem.equals(BUSINESS)) {

                    Info infoS = item.getSaverBusiness();
                    Info info = item.getStandartBusiness();

                    if (infoS != null || info != null) {

                        info_id = getCabinClass(item.getSaverBusiness(), BUSINESS, IMTInfo.SAVER, info_id, award, item.getFlights());
                        info_id = getCabinClass(item.getStandartBusiness(), BUSINESS, IMTInfo.FULL, info_id, award, item.getFlights());
                    }

                } else if (cabinItem.equals(FIRST)) {

                    Info infoS = item.getSaverFirst();
                    Info info = item.getStandartFirst();

                    if (infoS != null || info != null) {

                        info_id = getCabinClass(item.getSaverFirst(), FIRST, IMTInfo.SAVER, info_id, award, item.getFlights());
                        info_id = getCabinClass(item.getStandartFirst(), FIRST, IMTInfo.FULL, info_id, award, item.getFlights());
                    }

                }
            }

            int flight_id = 0;

            Date previousDate = null;

            for (Flight flight : item.getFlights()) {

                IMTFlight imtf = new IMTFlight();

                imtf.setAircraft(flight.getAircraft());

                Date date1 = ua_sdf.parse(flight.getArriveDate().trim());

                //01/13/2016 22:55
                imtf.setArrive_date(sdf1.format(date1));
                imtf.setArrive_place(flight.getArrivePlace());
                imtf.setArrive_code(flight.getArriveAirport());
                imtf.setArrive_time(time_format_res.format(date1));

                Date date2 = ua_sdf.parse(flight.getDepartDate().trim());

                imtf.setDepart_date(sdf1.format(date2));
                imtf.setDepart_place(flight.getDepartPlace());
                imtf.setDepart_code(flight.getDepartAirport());
                imtf.setDepart_time(time_format_res.format(date2));
                imtf.setFlight_cabin(flight.getFlightCabin());
                imtf.setFlight_number(flight.getFlight());
                imtf.setAirline_company(Utils.getAirCompany(flight.getFlight()));
                imtf.setMeal("");

//                        String travelTime = flight.getTravelTime();
//                        imtf.setTravel_time(Utils.getHoursBetweenDays(date2, date1));
                imtf.setId(flight_id);

                Date arrDate = time_format.parse(imtf.getArrive_date() + " " + imtf.getArrive_time());
                Date depDate = time_format.parse(imtf.getDepart_date() + " " + imtf.getDepart_time());

                Calendar depCalendar = new GregorianCalendar();
                depCalendar.setTime(depDate);
                depCalendar.add(Calendar.SECOND, -dm.getTZByCode(imtf.getDepart_code()));

                Calendar arrCalendar = new GregorianCalendar();
                arrCalendar.setTime(arrDate);
                arrCalendar.add(Calendar.SECOND, -dm.getTZByCode(imtf.getArrive_code()));

                imtf.setTravel_time(Utils.getHoursBetweenDays(depCalendar.getTime(), arrCalendar.getTime()));

                imtf.setLayover_time(Utils.getHoursBetweenDays(previousDate, depDate));
                previousDate = arrDate;

                award.getFlight_list().add(imtf);

                flight_id++;
            }

            award.setTotal_duration(item.getTotalDuration());

            int i = 0;

            for (String conItem : item.getConnections()) {

                ExtraData extraData = new ExtraData();

                extraData.setField_name("connection");
                extraData.setField_type("string");
                extraData.setField_value(conItem);
                extraData.setField_lvl("flight_list");
                extraData.setField_id("flight_list_" + i);

                award.getExtra_data().add(extraData);

                i++;
            }

            if (award.getClass_list().size() > 0) {

                awardList.add(award);
            }
        }

        System.out.println("UA handler. Flight size after processing= [" + awardList.size() + "]");

        IMTDataObject dataObject = new IMTDataObject(awardList);

        Gson gson = new Gson();

        result = gson.toJson(dataObject);

        return awardList;
    }

    private Integer getCabinClass(Info info, String fCabin, String classDescription, int id, IMTAward award, List<Flight> flightList) throws SQLException {

        IMTInfo imti = new IMTInfo();

        if (info != null && info.getStatus() != Info.NA) {

            DatabaseManager dm = new DatabaseManager();

            boolean usa = false;

            for (Flight flight : flightList) {

                boolean depart = dm.isUSA(flight.getDepartAirport());
                boolean arrive = dm.isUSA(flight.getArriveAirport());

                if (depart && arrive) {

                    flight.setUsa(true);
                    usa = true;

                } else {

                    flight.setUsa(false);
                }
            }

            imti.setMileage(((Double) Double.parseDouble(info.getMileage())).intValue() + "");
            imti.setStatus(info.getStringStatus());
            imti.setTax(info.getTax());
            imti.setId(id);

            ExtraData extraData = new ExtraData();

            extraData.setField_name("class_description");
            extraData.setField_type("string");
            extraData.setField_value(classDescription);
            extraData.setField_lvl("class_list");
            extraData.setField_id("class_list_" + id);

            award.getExtra_data().add(extraData);

            int flight_id = 0;

            boolean mixed = false;

            String currCabin = fCabin;

            for (String mixedCabins : info.getMixedCabins()) {

                mixed = true;

                mixedCabins = Utils.getMixedCabinClass(mixedCabins.replaceAll("United", "").trim());

                extraData = new ExtraData();

                currCabin = Utils.getCabin(mixedCabins, currCabin);

                if (currCabin.equals(FIRST)) {

                    if (flightList.get(flight_id).isUsa()) {

                        mixedCabins = BUSINESS;

                        if (usa) {

                            currCabin = BUSINESS;
                        }

                    } else {

                        usa = false;
                        currCabin = FIRST;
                    }
                }

                extraData.setField_name("mixed_cabins");
                extraData.setField_type("string");
                extraData.setField_value(mixedCabins);
                extraData.setField_lvl("class_list");
                extraData.setField_id("class_list_" + id);
                extraData.setField_sub_lvl("flight_list_" + flight_id);

                award.getExtra_data().add(extraData);

                flight_id++;
            }

            imti.setName(fCabin);

            if (mixed) {

                extraData = new ExtraData();

                extraData.setField_name("mixed_description");
                extraData.setField_type("string");
                extraData.setField_value(currCabin);
                extraData.setField_lvl("class_list");
                extraData.setField_id("class_list_" + id);

                award.getExtra_data().add(extraData);

                if (usa) {

                    imti.setName(currCabin);
                }

            } else {

                if (usa && fCabin.equals(FIRST)) {

                    imti.setName(BUSINESS);
                }
            }

            award.getClass_list().add(imti);

            id++;
        }

        return id;
    }

}
