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

import static parser.Parser.*;

public class VS implements ParserResultHandler {

    @Override
    public List<IMTAward> processResult(List<Award> flights, String flightClass, String seats) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy:MM:dd");
        SimpleDateFormat vs_sdf = new SimpleDateFormat("EEEE d MMMM yyyy", Locale.US);
        SimpleDateFormat time_format_res = new SimpleDateFormat("yyyy:MM:dd HH:mm");
        List<IMTAward> awardList = new LinkedList<IMTAward>();
        String result = "";

        DatabaseManager dm = new DatabaseManager();

        for (Award item : flights) {

            int info_id = 0;

            IMTAward award = new IMTAward();

//                    if (flightClass.equals(ALL)) {
//                        Info infoE = item.getEconomy();
//                        Info infoB = item.getBusiness();
//                        Info infoPE = item.getPremiumEconomy();
//
//                        if (infoE != null) {
//
//                            info_id = getCabinClass(infoE, ECONOMY, IMTInfo.SAVER, info_id, award);
//                        }
//
//                        if (infoB != null) {
//
//                            info_id = getCabinClass(infoB, BUSINESS, IMTInfo.SAVER, info_id, award);
//                        }
//
//                        if (infoPE != null) {
//
//                            info_id = getCabinClass(infoPE, PREMIUM_ECONOMY, IMTInfo.SAVER, info_id, award);
//                        }
//                    }

            for (String cabinItem : Utils.getCabins(flightClass)) {

                if (cabinItem.equals(ECONOMY)) {

                    Info info = item.getEconomy();

                    if (info != null) {

                        info_id = getCabinClass(info, ECONOMY, IMTInfo.SAVER, info_id, award);
                    }

                } else if (cabinItem.equals(BUSINESS)) {

                    Info info = item.getBusiness();

                    if (info != null) {

                        info_id = getCabinClass(info, BUSINESS, IMTInfo.SAVER, info_id, award);
                    }

                } else if (cabinItem.equals(PREMIUM_ECONOMY)) {

                    Info info = item.getPremiumEconomy();

                    if (info != null) {

                        info_id = getCabinClass(info, PREMIUM_ECONOMY, IMTInfo.SAVER, info_id, award);
                    }
                }
            }

            int flight_id = 0;

            Date previousDate = null;

            for (Flight flight : item.getFlights()) {

                IMTFlight imtf = new IMTFlight();

                imtf.setAircraft(flight.getAircraft());

                String arrTime = flight.getArriveDate();

                Date arrDate = vs_sdf.parse(flight.getDepartDate().trim());

                if (arrTime.contains("+")) {

                    String dayDiff = arrTime.substring(arrTime.indexOf("+") + 1, arrTime.length());

                    dayDiff = dayDiff.substring(0, dayDiff.replaceAll(" ", " ").indexOf(" "));

                    Calendar calendar = new GregorianCalendar();
                    calendar.setTime(arrDate);
                    calendar.add(Calendar.DAY_OF_YEAR, Integer.parseInt(dayDiff));

                    flight.setArriveDate(sdf1.format(calendar.getTime()));

                } else {

                    flight.setArriveDate(sdf1.format(arrDate));
                }

                imtf.setArrive_date(flight.getArriveDate());
                imtf.setArrive_place(dm.getCityByCode(flight.getArriveAirport().trim()));
                imtf.setArrive_code(flight.getArriveAirport());
                imtf.setArrive_time(flight.getArriveTime());

                Date date1 = vs_sdf.parse(flight.getDepartDate().trim());

                imtf.setDepart_date(sdf1.format(date1));
                imtf.setDepart_place(dm.getCityByCode(flight.getDepartAirport().trim()));
                imtf.setDepart_code(flight.getDepartAirport());
                imtf.setDepart_time(flight.getDepartTime());
                imtf.setFlight_number(flight.getFlight());
                imtf.setAirline_company(Utils.getAirCompany(flight.getFlight()));
                imtf.setMeal(flight.getMeal());
                imtf.setId(flight_id);

                Date depDateT = time_format_res.parse(imtf.getDepart_date() + " " + imtf.getDepart_time());
                Date arrDateT = time_format_res.parse(imtf.getArrive_date() + " " + imtf.getArrive_time());

                Calendar depCalendar = new GregorianCalendar();
                depCalendar.setTime(depDateT);
                depCalendar.add(Calendar.SECOND, -dm.getTZByCode(imtf.getDepart_code()));

                Calendar arrCalendar = new GregorianCalendar();
                arrCalendar.setTime(arrDateT);
                arrCalendar.add(Calendar.SECOND, -dm.getTZByCode(imtf.getArrive_code()));

                imtf.setLayover_time(Utils.getHoursBetweenDays(previousDate, depCalendar.getTime()));
                imtf.setTravel_time(Utils.getHoursBetweenDays(depCalendar.getTime(), arrCalendar.getTime()));

                previousDate = arrCalendar.getTime();

                award.getFlight_list().add(imtf);

                flight_id++;
            }

            award.setTotal_duration(item.getTotalDuration());

            if (award.getClass_list().size() > 0) {

                awardList.add(award);
            }
        }

        System.out.println("VS handler. Flight size after processing= [" + awardList.size() + "]");

        return awardList;
    }

    private Integer getCabinClass(Info info, String fCabin, String classDescription, int id, IMTAward award) {

        IMTInfo imti = new IMTInfo();

        if (info != null && info.getStatus() != Info.NA) {

            imti.setMileage(info.getMileage().trim());
            imti.setName(fCabin);
            imti.setStatus(info.getStringStatus());

            String[] taxArray = info.getTax().trim().split(" ");

            imti.setCurrency(taxArray[0].trim());
            imti.setTax(taxArray[1].replaceAll(",", "").trim());
            imti.setId(id);

            ExtraData extraData = new ExtraData();

            extraData.setField_name("class_description");
            extraData.setField_type("string");
            extraData.setField_value(classDescription);
            extraData.setField_lvl("class_list");
            extraData.setField_id("class_list_" + id);

            award.getExtra_data().add(extraData);

            id++;

            award.getClass_list().add(imti);
        }

        return id;
    }

}

