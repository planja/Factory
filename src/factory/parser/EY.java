package factory.parser;

import com.google.gson.Gson;
import factory.ParserResultHandler;
import factory.db.manager.DatabaseManager;
import factory.model.*;
import factory.utils.Utils;
import parser.model.Award;
import parser.model.Flight;
import parser.model.Info;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static parser.Parser.*;

public class EY implements ParserResultHandler {

    @Override
    public List<IMTAward> handleResponse(List<Award> flights, String flightClass, String seats, IMTError error, String requestId, String userId, String from, String to) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

        SimpleDateFormat dt_format_or = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        SimpleDateFormat date_format_res = new SimpleDateFormat("yyyy:MM:dd");
        SimpleDateFormat time_format_res = new SimpleDateFormat("HH:mm");
        String result = "";
        List<IMTAward> awardList = new LinkedList<IMTAward>();

        DatabaseManager dm = new DatabaseManager();

        for (Award item : flights) {

            int info_id = 0;

            IMTAward award = new IMTAward();

            int flight_id = 0;

            Date previousDate = null;

            for (Flight flight : item.getFlights()) {

                IMTFlight imtf = new IMTFlight();

                imtf.setAircraft(flight.getAircraft());

                Date arrDate = dt_format_or.parse(flight.getArriveDate().trim() + " " + flight.getArriveTime());

                imtf.setArrive_date(date_format_res.format(arrDate));
                imtf.setArrive_time(time_format_res.format(arrDate));

                imtf.setArrive_place(dm.getCityByCode(flight.getArrivePlace()));
                imtf.setArrive_code(flight.getArrivePlace());
                imtf.setDepart_place(dm.getCityByCode(flight.getDepartPlace()));
                imtf.setDepart_code(flight.getDepartPlace());

                Date depDate = dt_format_or.parse(flight.getDepartDate().trim() + " " + flight.getDepartTime());

                imtf.setDepart_date(date_format_res.format(depDate));
                imtf.setDepart_time(time_format_res.format(depDate));

                imtf.setFlight_number(flight.getFlight());
                imtf.setAirline_company(Utils.getAirCompany(flight.getFlight()));
                imtf.setMeal(flight.getMeal());
                imtf.setTravel_time(flight.getTravelTime());
                imtf.setId(flight_id);

                imtf.setLayover_time(Utils.getHoursBetweenDays(previousDate, depDate));
                previousDate = arrDate;

                award.getFlight_list().add(imtf);

                flight_id++;
            }

            award.setTotal_duration(item.getTotalDuration());

//                    if (flightClass.equals(ALL)) {

            Info infoE = item.getEconomy();
            Info infoSTE = item.getStandartEconomy();
            Info infoFE = item.getFullEconomy();
            Info infoSE = item.getSaverEconomy();
//
            if (infoE != null) {

                info_id = getCabinClass(infoE, ECONOMY, IMTInfo.SAVER, IMTInfo.SAVER, info_id, award);
            }

//                    if (infoSE != null) {
//
//                        info_id = getCabinClass(infoSE, ECONOMY, IMTInfo.SAVER,  IMTInfo.SAVER, info_id, award);
//                    }
//                    if (infoSTE != null) {
//
//                        info_id = getCabinClass(infoSTE, ECONOMY, IMTInfo.STANDART,  IMTInfo.STANDART, info_id, award);
//                    }
//
//                    if (infoFE != null) {
//
//                        info_id = getCabinClass(infoFE, ECONOMY, IMTInfo.FULL,  IMTInfo.FULL, info_id, award);
//                    }
            Info infoB = item.getBusiness();
            Info infoSB = item.getSaverBusiness();
            Info infoSTB = item.getStandartBusiness();
            Info infoFB = item.getFullBusiness();

            if (infoB != null) {

                info_id = getCabinClass(infoB, BUSINESS, IMTInfo.SAVER, IMTInfo.SAVER, info_id, award);
            }

//                        if (infoSB != null) {
//
//                            info_id = getCabinClass(infoSB, BUSINESS, IMTInfo.SAVER, IMTInfo.SAVER, info_id, award);
//                        }
////
//                    if (infoSTB != null) {
//
//                        info_id = getCabinClass(infoSTB, BUSINESS, IMTInfo.STANDART,  IMTInfo.STANDART, info_id, award);
//                    }
//
//                    if (infoFB != null) {
//
//                        info_id = getCabinClass(infoFB, BUSINESS, IMTInfo.FULL,  IMTInfo.FULL, info_id, award);
//                    }

            Info infoF = item.getFirst();
            Info infoSF = item.getSaverFirst();
            Info infoFF = item.getFullFirst();

            if (infoF != null) {

                info_id = getCabinClass(infoF, FIRST, IMTInfo.SAVER, IMTInfo.SAVER, info_id, award);
            }

//                    if (infoFF != null) {
//
//                        info_id = getCabinClass(infoFF, FIRST, IMTInfo.FULL, IMTInfo.FULL, info_id, award);
//                    }
//                    }

//                    else if (flightClass.equals(ECONOMY)) {
//
//                        Info infoE = item.getEconomy();
//                        Info infoSTE = item.getStandartEconomy();
//                        Info infoFE = item.getFullEconomy();
//                        Info infoSE = item.getSaverEconomy();
//
//                        if (infoE != null) {
//
//                            info_id = getCabinClass(infoE, ECONOMY, IMTInfo.SAVER, IMTInfo.SAVER, info_id, award);
//                        }
//
////                    if (infoSE != null) {
////
////                        info_id = getCabinClass(infoSE, ECONOMY, IMTInfo.SAVER, IMTInfo.SAVER, info_id, award);
////                    }
////                    if (infoSTE != null) {
////
////                        info_id = getCabinClass(infoSTE, ECONOMY, IMTInfo.STANDART, IMTInfo.STANDART, info_id, award);
////                    }
////
////                    if (infoFE != null) {
////
////                        info_id = getCabinClass(infoFE, ECONOMY, IMTInfo.FULL, IMTInfo.FULL, info_id, award);
////                    }
//                    } else if (flightClass.equals(BUSINESS)) {
//
//                        Info infoSB = item.getSaverBusiness();
//                        Info infoSTB = item.getStandartBusiness();
//                        Info infoFB = item.getFullBusiness();
//
//                        if (infoSB != null) {
//
//                            info_id = getCabinClass(infoSB, BUSINESS, IMTInfo.SAVER, IMTInfo.SAVER, info_id, award);
//                        }
////
////                    if (infoSTB != null) {
////
////                        info_id = getCabinClass(infoSTB, BUSINESS, IMTInfo.STANDART, IMTInfo.STANDART, info_id, award);
////                    }
////
////                    if (infoFB != null) {
////
////                        info_id = getCabinClass(infoFB, BUSINESS, IMTInfo.FULL, IMTInfo.FULL, info_id, award);
////                    }
//
//                    } else if (flightClass.equals(FIRST)) {
//
//                        Info infoF = item.getFirst();
//                        Info infoSF = item.getSaverFirst();
//                        Info infoFF = item.getFullFirst();
//
//                        if (infoF != null) {
//
//                            info_id = getCabinClass(infoF, FIRST, IMTInfo.SAVER, IMTInfo.SAVER, info_id, award);
//                        }
//
////                        if (infoSF != null) {
////
////                            info_id = getCabinClass(infoSF, FIRST, IMTInfo.SAVER, IMTInfo.SAVER, info_id, award);
////                        }
////
////                    if (infoFF != null) {
////
////                        info_id = getCabinClass(infoFF, FIRST, IMTInfo.FULL, IMTInfo.FULL, info_id, award);
////                    }
//
//                    }

//                int i = 0;
//
//                for (String conItem : item.getConnections()) {
//
//                    ExtraData extraData = new ExtraData();
//
//                    extraData.setField_name("connection");
//                    extraData.setField_type("string");
//                    extraData.setField_value(conItem);
//                    extraData.setField_lvl("flight_list");
//                    extraData.setField_id("flight_list_" + i);
//
//                    award.getExtra_data().add(extraData);
//
//                    i++;
//                }
            if (award.getClass_list().size() > 0) {

                awardList.add(award);
            }
        }

        System.out.println("EY handler. Flight size after processing= [" + awardList.size() + "]");

        IMTDataObject dataObject = new IMTDataObject(awardList);

        Gson gson = new Gson();

        result = gson.toJson(dataObject);


        return awardList;
    }

    private Integer getCabinClass(Info info, String fCabin, String classDescription, String currAward, int id, IMTAward award) {

        IMTInfo imti = new IMTInfo();

        if (info != null && info.getStatus() != Info.NA) {

            String[] taxArray = info.getTax().split(" ");

            imti.setMileage(info.getMileage());
            imti.setName(fCabin);
            imti.setStatus(info.getStringStatus());
            imti.setTax(taxArray[0]);
            imti.setCurrency(taxArray[1]);
            imti.setId(id);

            award.getClass_list().add(imti);

            ExtraData extraData = new ExtraData();

            extraData.setField_name("class_description");
            extraData.setField_type("string");
            extraData.setField_value(classDescription);
            extraData.setField_lvl("class_list");
            extraData.setField_id("class_list_" + id);

            award.getExtra_data().add(extraData);

            extraData = new ExtraData();

            extraData.setField_name("award_description");
            extraData.setField_type("string");
            extraData.setField_value(currAward);
            extraData.setField_lvl("class_list");
            extraData.setField_id("class_list_" + id);

            award.getExtra_data().add(extraData);

            id++;
        }

        return id;
    }
}
