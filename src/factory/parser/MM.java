package factory.parser;

import com.google.gson.Gson;
import factory.ParserResultHandler;
import factory.db.manager.DatabaseManager;
import factory.model.*;
import factory.utils.Utils;
import org.jsoup.Jsoup;
import parser.model.Award;
import parser.model.Flight;
import parser.model.Info;

import java.text.SimpleDateFormat;
import java.util.*;

import static parser.Parser.*;

public class MM implements ParserResultHandler {

    @Override
    public List<IMTAward> handleResponse(List<Award> flights, String flightClass, String seats, IMTError error, String requestId, String userId, String from, String to) throws Exception {

        SimpleDateFormat date_format_res = new SimpleDateFormat("yyyy:MM:dd HH:mm");
        date_format_res.setTimeZone(TimeZone.getTimeZone("UTC"));

        System.out.println("MM handler. Flight size before processing= [" + flights.size() + "]");

        DatabaseManager dm = new DatabaseManager();

        List<IMTAward> awardList = new LinkedList<IMTAward>();

        for (Award item : flights) {

            int info_id = 0;

            IMTAward award = new IMTAward();

            int flight_id = 0;

            Date previousDate = null;

            for (Flight flight : item.getFlights()) {

                IMTFlight imtf = new IMTFlight();

                imtf.setAircraft(flight.getAircraft());

                Date arrDate = date_format_res.parse(flight.getArriveDate().trim() + " " + flight.getArriveTime().trim());

                imtf.setArrive_date(flight.getArriveDate().trim());
                imtf.setArrive_place(dm.getCityByCode(flight.getArrivePlace()));
                imtf.setArrive_code(flight.getArrivePlace());

                imtf.setArrive_time(flight.getArriveTime());

                Date depDate = date_format_res.parse(flight.getDepartDate().trim() + " " + flight.getDepartTime().trim());

                imtf.setLayover_time(Utils.getHoursBetweenDays(previousDate, depDate));
                previousDate = arrDate;

                imtf.setDepart_date(flight.getDepartDate().trim());
                imtf.setDepart_place(dm.getCityByCode(flight.getDepartPlace()));
                imtf.setDepart_code(flight.getDepartPlace());

                imtf.setDepart_time(flight.getDepartTime());
                imtf.setFlight_number(flight.getFlight());
                imtf.setAirline_company(Utils.getAirCompany(flight.getFlight()));
                imtf.setMeal("");

                Calendar depCalendar = new GregorianCalendar();
                depCalendar.setTime(depDate);
                depCalendar.add(Calendar.SECOND, -dm.getTZByCode(imtf.getDepart_code()));

                Calendar arrCalendar = new GregorianCalendar();
                arrCalendar.setTime(arrDate);
                arrCalendar.add(Calendar.SECOND, -dm.getTZByCode(imtf.getArrive_code()));

                imtf.setTravel_time(Utils.getHoursBetweenDays(depCalendar.getTime(), arrCalendar.getTime()));
                imtf.setId(flight_id);

                award.getFlight_list().add(imtf);

                flight_id++;
            }

            award.setTotal_duration(item.getTotalDuration());

//                if (flightClass.equals(ALL)) {

            Info infoSE = item.getEconomy();

            if (infoSE != null) {

                info_id = getCabinClass(infoSE, ECONOMY, IMTInfo.SAVER, info_id, award);
            }

            Info infoSB = item.getBusiness();

            if (infoSB != null) {

                info_id = getCabinClass(infoSB, BUSINESS, IMTInfo.SAVER, info_id, award);
            }

            Info infoSF = item.getFirst();

            if (infoSF != null) {

                info_id = getCabinClass(infoSF, FIRST, IMTInfo.SAVER, info_id, award);
            }

//                }

//                else if (flightClass.equals(ECONOMY)) {
//
//                    Info infoSE = item.getEconomy();
//
//                    if (infoSE != null) {
//
//                        info_id = getCabinClass(infoSE, ECONOMY, IMTInfo.SAVER, info_id, award);
//                    }
//
//                } else if (flightClass.equals(BUSINESS)) {
//
//                    Info infoSB = item.getBusiness();
//
//                    if (infoSB != null) {
//
//                        info_id = getCabinClass(infoSB, BUSINESS, IMTInfo.SAVER, info_id, award);
//                    }
//
//                } else if (flightClass.equals(FIRST)) {
//
//                    Info infoSF = item.getFirst();
//
//                    if (infoSF != null) {
//
//                        info_id = getCabinClass(infoSF, FIRST, IMTInfo.SAVER, info_id, award);
//                    }
//
//                }

            if (award.getClass_list().size() > 0) {

                awardList.add(award);
            }
        }

        System.out.println("MM handler. Flight size after processing= [" + awardList.size() + "]");

        IMTDataObject dataObject = new IMTDataObject(awardList);

        Gson gson = new Gson();

        String result = gson.toJson(dataObject);

        String callback = Utils.postFlights(requestId, userId, result, "LH", from, to, seats);
        return awardList;
    }

    private Integer getCabinClass(Info info, String fCabin, String classDescription, int id, IMTAward award) {

        IMTInfo imti = new IMTInfo();

        if (info != null && info.getStatus() != Info.NA) {

            imti.setMileage(Jsoup.parse(info.getMileage()).text());
            imti.setName(fCabin);
            imti.setStatus(info.getStringStatus());
            imti.setTax(info.getTax());
            imti.setId(id);

            award.getClass_list().add(imti);

            ExtraData extraData = new ExtraData();

            extraData.setField_name("class_description");
            extraData.setField_type("string");
            extraData.setField_value(classDescription);
            extraData.setField_lvl("class_list");
            extraData.setField_id("class_list_" + id);

            award.getExtra_data().add(extraData);

            id++;
        }

        return id;
    }
}
