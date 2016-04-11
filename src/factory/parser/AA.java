package factory.parser;

import com.google.gson.Gson;
import db.manager.DatabaseManager;
import factory.ParserResultHandler;
import factory.model.*;
import factory.utils.Utils;
import parser.model.Award;
import parser.model.Flight;
import parser.model.Info;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

import static factory.model.IMTInfo.FULL;
import static factory.model.IMTInfo.SAVER;
import static parser.Parser.*;

/**
 * Created by Anton on 11.04.2016.
 */
public class AA implements ParserResultHandler {

    @Override
    public List<IMTAward> handleResponse(List<Award> flights, String flightClass, String seats, IMTError error, String requestId, String userId, String from, String to) throws ParseException, SQLException, Exception {
        System.out.println("AA");
        String result = "";

        SimpleDateFormat dt_format_or = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat date_format_res = new SimpleDateFormat("yyyy:MM:dd");
        SimpleDateFormat time_format_res = new SimpleDateFormat("HH:mm");

        date_format_res.setTimeZone(TimeZone.getTimeZone("UTC"));
        time_format_res.setTimeZone(TimeZone.getTimeZone("UTC"));

        DatabaseManager dm = new DatabaseManager();

        List<IMTAward> awardList = new LinkedList<IMTAward>();

        for (Award item : flights) {

            int info_id = 0;

            IMTAward award = new IMTAward();

            int flight_id = 0;

            Date firstDate = null;
            Date lastDate = null;

            Date previousDate = null;

            List<String> cabinList = new LinkedList<>();

            String previousCabin = null;

            boolean mixed = false;

            for (Flight flight : item.getFlights()) {

                IMTFlight imtf = new IMTFlight();

                imtf.setAircraft(flight.getAircraft());

                Date arrDate = flight.getArrDT();

                imtf.setArrive_date(date_format_res.format(arrDate));
                imtf.setArrive_place(flight.getArriveAirport().trim());
                imtf.setArrive_code(flight.getArrivePlace());
                imtf.setArrive_time(time_format_res.format(arrDate));

                Date depDate = flight.getDepDT();

                if (firstDate == null) {

                    firstDate = depDate;
                }

                imtf.setLayover_time(Utils.getHoursBetweenDays(previousDate, depDate));
                previousDate = arrDate;

                imtf.setDepart_date(date_format_res.format(depDate));
                imtf.setDepart_place(flight.getDepartAirport().trim());
                imtf.setDepart_code(flight.getDepartPlace());
                imtf.setDepart_time(time_format_res.format(depDate));
                imtf.setFlight_number(flight.getfNumber());
                imtf.setAirline_company(Utils.getAirCompany(flight.getfNumber()));
                imtf.setMeal(flight.getMeal());
                imtf.setTravel_time(Utils.getHoursBetweenDays(depDate, arrDate));
                imtf.setId(flight_id);

//                    if (previousCabin != null && !previousCabin.equals(flight.getFlightCabin())) {
//
//                        mixed = true;
//                    }

//                    previousCabin = flight.getFlightCabin();
//                    cabinList.add(flight.getFlightCabin());

                award.getFlight_list().add(imtf);

                flight_id++;
            }

            Flight flight = item.getFlights().size() > 0 ? item.getFlights().get(item.getFlights().size() - 1) : null;

            lastDate = flight == null ? new Date() : flight.getArrDT();

            for (String cabinItem : Utils.getCabins(flightClass)) {

                if (cabinItem.equals(ECONOMY)) {

                    Info infoS = item.getSaverEconomy();
                    Info infoF = item.getFullEconomy();

                    if (infoS != null) {

                        info_id = getCabinClass(infoS, ECONOMY, SAVER, info_id, award, cabinList, Integer.parseInt(seats), item.getFlights());
                    }

                    if (infoF != null) {

                        info_id = getCabinClass(infoF, ECONOMY, FULL, info_id, award, cabinList, Integer.parseInt(seats), item.getFlights());
                    }

                } else if (cabinItem.equals(BUSINESS)) {

                    Info infoS = item.getSaverBusiness();
                    Info infoF = item.getFullBusiness();

                    if (infoS != null) {

                        info_id = getCabinClass(infoS, BUSINESS, SAVER, info_id, award, cabinList, Integer.parseInt(seats), item.getFlights());
                    }

                    if (infoF != null) {

                        info_id = getCabinClass(infoF, BUSINESS, FULL, info_id, award, cabinList, Integer.parseInt(seats), item.getFlights());
                    }

                } else if (cabinItem.equals(FIRST)) {

                    Info infoS = item.getSaverFirst();
                    Info infoF = item.getFullFirst();

                    if (infoS != null) {

                        info_id = getCabinClass(infoS, FIRST, SAVER, info_id, award, cabinList, Integer.parseInt(seats), item.getFlights());
                    }

                    if (infoF != null) {

                        info_id = getCabinClass(infoF, FIRST, FULL, info_id, award, cabinList, Integer.parseInt(seats), item.getFlights());
                    }
                }
            }

            award.setTotal_duration(Utils.getHoursBetweenDays(firstDate, lastDate));

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

            int j = 0;

            for (IMTInfo imti : award.getClass_list()) {

                if (imti.getName().equals(BUSINESS) && !Utils.getCabins(flightClass).contains(BUSINESS)) {

                    award.getClass_list().remove(j);
                    break;
                }

                j++;
            }

            if (award.getClass_list().size() > 0) {

                awardList.add(award);
            }
        }

        System.out.println("AA handler. Flight size after processing= [" + awardList.size() + "]");

        IMTDataObject dataObject = new IMTDataObject(awardList);
        dataObject.setError(error);

        Gson gson = new Gson();

        result = gson.toJson(dataObject);

        String callback = Utils.postFlights(requestId, userId, result, "AA", from, to, seats);
        return awardList;
    }

    private Integer getCabinClass(Info info, String fCabin, String classDescription, int id, IMTAward award, List<String> cabinList, int seats, List<Flight> flightList) throws SQLException {

        IMTInfo imti = new IMTInfo();

        if (info != null && info.getStatus() != Info.NA) {

//                DatabaseManager dm = new DatabaseManager();
//
//                boolean usa = false;
//
//                for (Flight flight : flightList) {
//
//                    boolean depart = dm.isUSA(flight.getDepartAirport());
//                    boolean arrive = dm.isUSA(flight.getArriveAirport());
//
//                    if (depart && arrive) {
//
//                        flight.setUsa(true);
//                        usa = true;
//
//                    } else {
//
//                        flight.setUsa(false);
//                    }
//                }

            String mil = info.getMileage();

            imti.setMileage(mil);
//                imti.setMileage(Integer.parseInt(mil) / seats + "");
//                imti.setName(fCabin);
            imti.setStatus(info.getStringStatus());
            imti.setTax(info.getTax().replaceAll("$", ""));
            imti.setCurrency("USD");
            imti.setId(id);

            ExtraData extraData = new ExtraData();

            extraData.setField_name("class_description");
            extraData.setField_type("string");
            extraData.setField_value(classDescription);
            extraData.setField_lvl("class_list");
            extraData.setField_id("class_list_" + id);

            award.getExtra_data().add(extraData);

            int flight_id = 0;

            String currCabin = fCabin;

//                if (mixed) {
//
//                    extraData = new ExtraData();
//
//                    extraData.setField_name("mixed_description");
//                    extraData.setField_type("string");
//                    extraData.setField_value(currCabin);
//                    extraData.setField_lvl("class_list");
//                    extraData.setField_id("class_list_" + id);
//
//                    award.getExtra_data().add(extraData);
//                }
//                }

            imti.setName(fCabin);

//                if (mixed) {
//
//                    if (usa) {
//
//                        imti.setName(currCabin);
//                    }
//
//                } else {
//
//                    if (usa && fCabin.equals(FIRST)) {
//
//                        imti.setName(BUSINESS);
//                    }
//                }

            award.getClass_list().add(imti);

            id++;
        }

        return id;
    }
}
