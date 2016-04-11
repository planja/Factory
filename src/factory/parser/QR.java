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
import java.util.*;

import static factory.model.IMTInfo.SAVER;
import static parser.Parser.*;


public class QR implements ParserResultHandler {

    @Override
    public List<IMTAward> handleResponse(List<Award> flights, String flightClass, String seats, IMTError error, String requestId, String userId, String from, String to) throws Exception {
        DatabaseManager dm = new DatabaseManager();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat dt_format_or = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.US);
        SimpleDateFormat date_format_res = new SimpleDateFormat("yyyy:MM:dd");
        SimpleDateFormat time_format_res = new SimpleDateFormat("HH:mm");
        String result = "";
        List<IMTAward> awardList = new LinkedList<IMTAward>();

        for (Award item : flights) {

            int info_id = 0;

            IMTAward award = new IMTAward();

            int flight_id = 0;

            Date previousDate = null;

            for (Flight flight : item.getFlights()) {

                IMTFlight imtf = new IMTFlight();

                imtf.setAircraft(flight.getAircraft());

                Calendar arrCalendar = new GregorianCalendar();
                arrCalendar.setTimeZone(TimeZone.getTimeZone("UTC"));
                arrCalendar.setTime(flight.getArrDT());

//                        System.out.println(arrCalendar.getTime());
                arrCalendar.add(Calendar.SECOND, dm.getTZByCode(flight.getArriveAirport().trim()));

                Calendar depCalendar = new GregorianCalendar();
                depCalendar.setTimeZone(TimeZone.getTimeZone("UTC"));
                depCalendar.setTime(flight.getDepDT());

//                        System.out.println(depCalendar.getTime());
                depCalendar.add(Calendar.SECOND, dm.getTZByCode(flight.getDepartAirport().trim()));

                imtf.setArrive_date(date_format_res.format(arrCalendar.getTime()));
                imtf.setArrive_place(dm.getCityByCode(Utils.replaceAirport(flight.getArriveAirport(), Utils.QR)));
                imtf.setArrive_code(flight.getArriveAirport());
                imtf.setArrive_time(time_format_res.format(arrCalendar.getTime()));

                imtf.setLayover_time(flight.getLayover());

                imtf.setDepart_date(date_format_res.format(depCalendar.getTime()));
                imtf.setDepart_place(dm.getCityByCode(Utils.replaceAirport(flight.getDepartAirport(), Utils.QR)));
                imtf.setDepart_code(flight.getDepartAirport());
                imtf.setDepart_time(time_format_res.format(depCalendar.getTime()));
                imtf.setFlight_number(flight.getFlight());
                imtf.setAirline_company(Utils.getAirCompany(flight.getFlight()));
                imtf.setMeal(flight.getMeal());
                imtf.setTravel_time(flight.getTravelTime());
                imtf.setId(flight_id);

                award.getFlight_list().add(imtf);

                flight_id++;
            }

//                    if (flightClass.equals(ALL)) {
//                        Info infoSE = item.getSaverEconomy();
//                        Info infoSB = item.getSaverBusiness();
//                        Info infoSF = item.getSaverFirst();
//
//                        if (infoSE != null) {
//
//                            info_id = getCabinClass(infoSE, ECONOMY, SAVER, info_id, award);
//                        }
//
//                        if (infoSB != null) {
//
//                            info_id = getCabinClass(infoSB, BUSINESS, SAVER, info_id, award);
//                        }
//
//                        if (infoSF != null) {
//
//                            info_id = getCabinClass(infoSF, FIRST, SAVER, info_id, award);
//                        }
//                    }
            for (String cabinItem : Utils.getCabins(flightClass)) {

                if (cabinItem.equals(ECONOMY)) {

                    Info infoS = item.getSaverEconomy();

                    if (infoS != null) {

                        info_id = getCabinClass(infoS, ECONOMY, SAVER, info_id, award);
                    }

                } else if (cabinItem.equals(BUSINESS)) {

                    Info infoS = item.getSaverBusiness();

                    if (infoS != null) {

                        info_id = getCabinClass(infoS, BUSINESS, SAVER, info_id, award);
                    }

                } else if (cabinItem.equals(FIRST)) {

                    Info infoS = item.getSaverFirst();

                    if (infoS != null) {

                        info_id = getCabinClass(infoS, FIRST, SAVER, info_id, award);
                    }
                }
            }

            award.setTotal_duration(item.getTotalDuration());

            if (award.getClass_list().size() > 0) {

                awardList.add(award);
            }
        }

        System.out.println("QR handler. Flight size after processing= [" + awardList.size() + "]");

        IMTDataObject dataObject = new IMTDataObject(awardList);
        dataObject.setError(error);

        Gson gson = new Gson();

        result = gson.toJson(dataObject);

        return awardList;
    }

    private Integer getCabinClass(Info info, String fCabin, String classDescription, int id, IMTAward award) {

        IMTInfo imti = new IMTInfo();

        if (info != null && info.getStatus() != Info.NA) {

            imti.setMileage(info.getMileage());
            imti.setName(fCabin);
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

            award.getClass_list().add(imti);

            id++;
        }

        return id;
    }
}
