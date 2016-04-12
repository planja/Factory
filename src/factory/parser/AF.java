package factory.parser;

import factory.ParserResultHandler;
import factory.db.manager.DatabaseManager;
import factory.model.ExtraData;
import factory.model.IMTAward;
import factory.model.IMTFlight;
import factory.model.IMTInfo;
import factory.utils.Utils;
import parser.model.Award;
import parser.model.Flight;
import parser.model.Info;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static parser.Parser.*;

public class AF implements ParserResultHandler {

    @Override
    public List<IMTAward> processResult(List<Award> flights, String flightClass, String seats) throws Exception {

        SimpleDateFormat time_format_pac = new SimpleDateFormat("yyyy:MM:dd h:mm a");
        SimpleDateFormat time_format_res = new SimpleDateFormat("HH:mm");

        String result;

        System.out.println("AF handler. Flight size before processing= [" + flights.size() + "]");

        DatabaseManager dm = new DatabaseManager();

        List<IMTAward> awardList = new LinkedList<IMTAward>();

        for (Award item : flights) {

            int info_id = 0;

            IMTAward award = new IMTAward();

//                if (flightClass.equals(Parser.ALL)) {
//                    Info infoSE = item.getSaverEconomy();
//                    Info infoFE = item.getFullEconomy();
//
//                    if (infoSE != null || infoFE != null) {
//
//                        info_id = getCabinClass(infoSE, ECONOMY, IMTInfo.SAVER, info_id, award);
//                        info_id = getCabinClass(infoFE, ECONOMY, IMTInfo.FULL, info_id, award);
//                    }
//
//                    Info infoSB = item.getSaverBusiness();
//                    Info infoFB = item.getFullBusiness();
//
//                    if (infoSB != null || infoFB != null) {
//
//                        info_id = getCabinClass(infoSB, BUSINESS, IMTInfo.SAVER, info_id, award);
//                        info_id = getCabinClass(infoFB, BUSINESS, IMTInfo.FULL, info_id, award);
//                    }
//
//                    Info infoSF = item.getSaverFirst();
//                    Info infoFF = item.getFullFirst();
//
//                    if (infoSF != null || infoFF != null) {
//
//                        info_id = getCabinClass(infoSF, FIRST, IMTInfo.SAVER, info_id, award);
//                        info_id = getCabinClass(infoFF, FIRST, IMTInfo.FULL, info_id, award);
//                    }
//
//                    Info infoSP = item.getSaverPremium();
//                    Info infoFP = item.getFullPremium();
//
//                    if (infoSP != null || infoFP != null) {
//
//                        info_id = getCabinClass(infoSP, PREMIUM_ECONOMY, IMTInfo.SAVER, info_id, award);
//                        info_id = getCabinClass(infoFP, PREMIUM_ECONOMY, IMTInfo.FULL, info_id, award);
//                    }
//                }
            for (String cabinItem : Utils.getCabins(flightClass)) {

                if (cabinItem.equals(ECONOMY)) {

                    Info infoSE = item.getSaverEconomy();
                    Info infoFE = item.getFullEconomy();

                    if (infoSE != null || infoFE != null) {

                        info_id = getCabinClass(infoSE, ECONOMY, IMTInfo.SAVER, info_id, award);
                        info_id = getCabinClass(infoFE, ECONOMY, IMTInfo.FULL, info_id, award);
                    }

                } else if (cabinItem.equals(BUSINESS)) {

                    Info infoSB = item.getSaverBusiness();
                    Info infoFB = item.getFullBusiness();

                    if (infoSB != null || infoFB != null) {

                        info_id = getCabinClass(infoSB, BUSINESS, IMTInfo.SAVER, info_id, award);
                        info_id = getCabinClass(infoFB, BUSINESS, IMTInfo.FULL, info_id, award);
                    }

                } else if (cabinItem.equals(FIRST)) {

                    Info infoSF = item.getSaverFirst();
                    Info infoFF = item.getFullFirst();

                    if (infoSF != null || infoFF != null) {

                        info_id = getCabinClass(infoSF, FIRST, IMTInfo.SAVER, info_id, award);
                        info_id = getCabinClass(infoFF, FIRST, IMTInfo.FULL, info_id, award);
                    }

                } else if (cabinItem.equals(PREMIUM_ECONOMY)) {

                    Info infoSP = item.getSaverPremium();
                    Info infoFP = item.getFullPremium();

                    if (infoSP != null || infoFP != null) {

                        info_id = getCabinClass(infoSP, PREMIUM_ECONOMY, IMTInfo.SAVER, info_id, award);
                        info_id = getCabinClass(infoFP, PREMIUM_ECONOMY, IMTInfo.FULL, info_id, award);
                    }
                }
            }

            int flight_id = 0;

            Date previousDate = null;

            Date depDate = null;
            Date arrDate = null;

            for (Flight flight : item.getFlights()) {

                IMTFlight imtf = new IMTFlight();

                imtf.setAircraft(flight.getAircraft());
                imtf.setArrive_date(flight.getArriveDate());
                imtf.setArrive_place(dm.getCityByCode(flight.getArriveAirport().trim()));
                imtf.setArrive_code(flight.getArriveAirport());

                Date arrTime = time_format_pac.parse(flight.getArriveDate() + " " + flight.getArriveTime());

                imtf.setArrive_time(time_format_res.format(arrTime));
                imtf.setDepart_date(flight.getDepartDate());
                imtf.setDepart_place(dm.getCityByCode(flight.getDepartAirport().trim()));
                imtf.setDepart_code(flight.getDepartAirport());

                Date depTime = time_format_pac.parse(flight.getDepartDate() + " " + flight.getDepartTime());

                if (flight_id == 0) {

                    depDate = depTime;
                }

                if (flight_id == (item.getFlights().size() - 1)) {

                    arrDate = arrTime;
                }

                imtf.setLayover_time(Utils.getHoursBetweenDays(previousDate, depTime));
                previousDate = arrTime;

                imtf.setDepart_time(time_format_res.format(depTime));
                imtf.setFlight_number(flight.getFlight());
                imtf.setAirline_company(Utils.getAirCompany(flight.getFlight()));

                Calendar depCalendar = new GregorianCalendar();
                depCalendar.setTime(depTime);
                depCalendar.add(Calendar.SECOND, -dm.getTZByCode(imtf.getDepart_code()));

                Calendar arrCalendar = new GregorianCalendar();
                arrCalendar.setTime(arrTime);
                arrCalendar.add(Calendar.SECOND, -dm.getTZByCode(imtf.getArrive_code()));

                imtf.setTravel_time(Utils.getHoursBetweenDays(depCalendar.getTime(), arrCalendar.getTime()));

                imtf.setMeal(flight.getMeal());
//                    imtf.setTravel_time(flight.getTravelTime());
                imtf.setId(flight_id);

                award.getFlight_list().add(imtf);

                flight_id++;
            }

            award.setTotal_duration(item.getTotalDuration());

            if (award.getClass_list().size() > 0) {

                awardList.add(award);
            }
        }
        System.out.println("AF handler. Flight size after processing= [" + awardList.size() + "]");

        return awardList;
    }

    private Integer getCabinClass(Info info, String fCabin, String classDescription, int id, IMTAward award) {

        IMTInfo imti = new IMTInfo();

        if (info != null && info.getStatus() != Info.NA) {

            String tax = info.getTax().replaceAll("\\+", "").replaceAll("\\*", "");

            Pattern pattern = Pattern.compile("(\\d+\\.\\d*).{1}(\\D+)");

            Matcher matcher = pattern.matcher(tax);

            if (matcher.find()) {

                imti.setTax(matcher.group(1).trim());
                imti.setCurrency(matcher.group(2).trim().replaceAll(" ", ""));
            }

            pattern = Pattern.compile("(\\d+)(.+)");

            matcher = pattern.matcher(info.getMileage().replaceAll(" ", ""));

            if (matcher.find()) {

                imti.setMileage(matcher.group(1).trim());
            }

            imti.setName(fCabin);
            imti.setStatus(info.getStringStatus());

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
