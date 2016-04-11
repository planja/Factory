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
import java.util.Locale;

import static factory.model.IMTInfo.SAVER;
import static parser.Parser.*;

public class CX implements ParserResultHandler {

    @Override
    public List<IMTAward> handleResponse(List<Award> flights, String flightClass, String seats, IMTError error, String requestId, String userId, String from, String to) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat dt_format_or = new SimpleDateFormat("yyyy:MM:dd HH:mm", Locale.US);
        SimpleDateFormat date_format_res = new SimpleDateFormat("yyyy:MM:dd");
        SimpleDateFormat time_format_res = new SimpleDateFormat("HH:mm");

        System.out.println("CX handler. Flight size before processing= [" + flights.size() + "]");

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

                Date arrDate = dt_format_or.parse(flight.getArriveDate() + " " + flight.getArriveTime());

                imtf.setArrive_date(date_format_res.format(arrDate));
                imtf.setArrive_place(dm.getCityByCode(flight.getArriveAirport()));
                imtf.setArrive_code(flight.getArriveAirport());
                imtf.setArrive_time(time_format_res.format(arrDate));

                Date depDate = dt_format_or.parse(flight.getDepartDate() + " " + flight.getDepartTime());

                imtf.setLayover_time(Utils.getHoursBetweenDays(previousDate, depDate));
                previousDate = arrDate;

                imtf.setDepart_date(date_format_res.format(depDate));
                imtf.setDepart_place(dm.getCityByCode(flight.getDepartAirport()));
                imtf.setDepart_code(flight.getDepartAirport());
                imtf.setDepart_time(time_format_res.format(depDate));
                imtf.setFlight_number(flight.getFlight());
                imtf.setAirline_company(Utils.getAirCompany(flight.getFlight()));
                imtf.setMeal(flight.getMeal());
                imtf.setTravel_time(flight.getTravelTime());
                imtf.setId(flight_id);

                award.getFlight_list().add(imtf);

                flight_id++;
            }

//                if (flightClass.equals(ALL)) {

            Info infoE = item.getEconomy();
            Info infoB = item.getBusiness();
            Info infoF = item.getFirst();
            Info infoPE = item.getPremiumEconomy();

            if (infoE != null) {

                info_id = getCabinClass(infoE, ECONOMY, info_id, award);
            }

            if (infoB != null) {

                info_id = getCabinClass(infoB, BUSINESS, info_id, award);
            }

            if (infoF != null) {

                info_id = getCabinClass(infoF, FIRST, info_id, award);
            }

            if (infoPE != null) {

                info_id = getCabinClass(infoF, PREMIUM_ECONOMY, info_id, award);
            }

//                }

//                else if (flightClass.equals(ECONOMY)) {
//
//                    Info info = item.getEconomy();
//
//                    if (info != null) {
//
//                        info_id = getCabinClass(info, ECONOMY, info_id, award);
//                    }
//
//                } else if (flightClass.equals(BUSINESS)) {
//
//                    Info info = item.getBusiness();
//
//                    if (info != null) {
//
//                        info_id = getCabinClass(info, BUSINESS, info_id, award);
//                    }
//
//                } else if (flightClass.equals(FIRST)) {
//
//                    Info info = item.getFirst();
//
//                    if (info != null) {
//
//                        info_id = getCabinClass(info, FIRST, info_id, award);
//                    }
//                } else if (flightClass.equals(PREMIUM_ECONOMY)) {
//
//                    Info info = item.getPremiumEconomy();
//
//                    if (info != null) {
//
//                        info_id = getCabinClass(info, PREMIUM_ECONOMY, info_id, award);
//                    }
//                }

            award.setTotal_duration(item.getTotalDuration());

            if (award.getClass_list().size() > 0) {

                awardList.add(award);
            }
        }

        System.out.println("CX handler. Flight size after processing= [" + awardList.size() + "]");

        IMTDataObject dataObject = new IMTDataObject(awardList);
        Gson gson = new Gson();

        String result = gson.toJson(dataObject);

        String callback = Utils.postFlights(requestId, userId, result, "CX", from, to, seats);
        return awardList;
    }

    private Integer getCabinClass(Info info, String fCabin, int id, IMTAward award) {

        IMTInfo imti = new IMTInfo();

        if (info != null && info.getStatus() != Info.NA) {

            imti.setMileage(info.getMileage());
            imti.setName(fCabin);
            imti.setStatus(info.getStringStatus());
            imti.setTax(info.getTax().substring(0, info.getTax().indexOf(".")));
            imti.setCurrency(info.getCurrency());
            imti.setId(id);

            System.out.println("CX Tax = [" + imti.getTax() + "]");
            System.out.println("CX Currency = [" + imti.getCurrency() + "]");
            System.out.println("CX Miles = [" + imti.getMileage() + "]");

            ExtraData extraData = new ExtraData();

            extraData.setField_name("class_description");
            extraData.setField_type("string");
            extraData.setField_value(SAVER);
            extraData.setField_lvl("class_list");
            extraData.setField_id("class_list_" + id);

            award.getExtra_data().add(extraData);

            award.getClass_list().add(imti);

            id++;
        }

        return id;
    }
}
