package factory.parser;

import com.google.gson.Gson;
import factory.ParserResultHandler;
import factory.db.manager.DatabaseManager;
import factory.model.*;
import factory.utils.Utils;
import parser.model.Award;
import parser.model.Flight;
import parser.model.Info;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static factory.model.IMTInfo.SAVER;
import static parser.Parser.*;

public class JL implements ParserResultHandler {

    @Override
    public List<IMTAward> handleResponse(List<Award> flights, String flightClass, String seats, IMTError error, String requestId, String userId, String from, String to) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat time_format_res = new SimpleDateFormat("yyyy:MM:dd HH:mm");
        DatabaseManager dm = new DatabaseManager();
        String result = "";
        List<IMTAward> awardList = new LinkedList<IMTAward>();

        for (Award item : flights) {

            int info_id = 0;

            IMTAward award = new IMTAward();

//                    if (flightClass.equals(ALL)) {

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

                info_id = getCabinClass(infoPE, PREMIUM_ECONOMY, info_id, award);
            }

//                    }

//                    else if (flightClass.equals(ECONOMY)) {
//
//                        Info info = item.getEconomy();
//
//                        if (info != null) {
//
//                            info_id = getCabinClass(info, ECONOMY, info_id, award);
//                        }
//
//                    } else if (flightClass.equals(BUSINESS)) {
//
//                        Info info = item.getBusiness();
//
//                        if (info != null) {
//
//                            info_id = getCabinClass(info, BUSINESS, info_id, award);
//                        }
//
//                    } else if (flightClass.equals(FIRST)) {
//
//                        Info info = item.getFirst();
//
//                        if (info != null) {
//
//                            info_id = getCabinClass(info, FIRST, info_id, award);
//                        }
//
//                    } else if (flightClass.equals(PREMIUM_ECONOMY)) {
//
//                        Info info = item.getPremiumEconomy();
//
//                        if (info != null) {
//
//                            info_id = getCabinClass(info, PREMIUM_ECONOMY, info_id, award);
//                        }
//                    }

            int flight_id = 0;

            Date previousDate = null;

            for (Flight flight : item.getFlights()) {

                IMTFlight imtf = new IMTFlight();

                imtf.setAircraft(flight.getAircraft());
                imtf.setArrive_date(flight.getArriveDate());

                imtf.setArrive_place(dm.getCityByCode(flight.getArriveAirport().trim()));
                imtf.setArrive_code(flight.getArriveAirport());
                imtf.setArrive_time(flight.getArriveTime());
                imtf.setDepart_date(flight.getDepartDate());

                imtf.setDepart_place(dm.getCityByCode(flight.getDepartAirport().trim()));
                imtf.setDepart_code(flight.getDepartAirport());
                imtf.setDepart_time(flight.getDepartTime());
                imtf.setFlight_number(flight.getFlight());
                imtf.setAirline_company(Utils.getAirCompany(flight.getFlight()));
                imtf.setMeal(flight.getMeal());

                Date depDate = time_format_res.parse(flight.getDepartDate() + " " + flight.getDepartTime());
                Date arrDate = time_format_res.parse(flight.getArriveDate() + " " + flight.getArriveTime());

                imtf.setTravel_time(Utils.getHoursBetweenDays(depDate, arrDate));

                imtf.setLayover_time(Utils.getHoursBetweenDays(previousDate, depDate));
                previousDate = arrDate;

                imtf.setId(flight_id);

                award.getFlight_list().add(imtf);

                flight_id++;
            }

            award.setTotal_duration(item.getTotalDuration());

            if (award.getClass_list().size() > 0) {

                awardList.add(award);
            }
        }

        System.out.println("JL handler. Flight size after processing= [" + awardList.size() + "]");

        IMTDataObject dataObject = new IMTDataObject(awardList);
        dataObject.setError(error);

        Gson gson = new Gson();

        result = gson.toJson(dataObject);

        return awardList;
    }

    private Integer getCabinClass(Info info, String fCabin, int id, IMTAward award) throws ParseException {

        NumberFormat nf_in = NumberFormat.getNumberInstance(Locale.UK);

        IMTInfo imti = new IMTInfo();

        if (info.isNa()) {

            info.setStatus(Info.NA);
        }

        if (info != null && info.getStatus() != Info.NA && info.getStatus() != Info.WAITLIST && !info.isNa()) {

            Integer mileage = nf_in.parse(info.getMileage()).intValue();

            String tax = info.getTax();

            Pattern pattern = Pattern.compile("(\\d+\\.\\d*)(\\D+)");

            Matcher matcher = pattern.matcher(tax);

            if (matcher.find()) {

                imti.setTax(Double.parseDouble(matcher.group(1).trim()) / 2 + "");
                imti.setCurrency(matcher.group(2).trim().replaceAll(" ", ""));
            }

            imti.setMileage(nf_in.format(mileage / 2));
            imti.setName(fCabin);
            imti.setStatus(info.getStringStatus());
//                imti.setTax(info.getTax());
            imti.setId(id);

            ExtraData extraData = new ExtraData();

            extraData.setField_name("class_description");
            extraData.setField_type("string");
            extraData.setField_value(SAVER);
            extraData.setField_lvl("class_list");
            extraData.setField_id("class_list_" + id);

            award.getExtra_data().add(extraData);

            id++;

            award.getClass_list().add(imti);
        }

        return id;
    }

}
