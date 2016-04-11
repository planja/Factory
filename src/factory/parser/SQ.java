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

import static parser.Parser.*;

public class SQ implements ParserResultHandler {

    @Override
    public List<IMTAward> handleResponse(List<Award> flights, String flightClass, String seats, IMTError error, String requestId, String userId, String from, String to) throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy:MM:dd");
        SimpleDateFormat sq_sdf = new SimpleDateFormat("dd MMM (EEE) yyyy", Locale.US);//15 Mar (Tue) 2016
        SimpleDateFormat time_format_res = new SimpleDateFormat("yyyy:MM:dd HH:mm");

        String result;

        System.out.println("SQ handler. Flight size before processing= [" + flights.size() + "]");

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

                Date date1 = sq_sdf.parse(flight.getArriveDate().trim());

                imtf.setArrive_date(sdf1.format(date1));
                imtf.setArrive_place(dm.getCityByCode(flight.getArriveAirport().trim()));
                imtf.setArrive_code(flight.getArriveAirport());
                imtf.setArrive_time(flight.getArriveTime().trim());

                Date date2 = sq_sdf.parse(flight.getDepartDate().trim());

                imtf.setDepart_date(sdf1.format(date2));
                imtf.setDepart_place(dm.getCityByCode(flight.getDepartAirport().trim()));
                imtf.setDepart_code(flight.getDepartAirport());
                imtf.setDepart_time(flight.getDepartTime().trim());
                imtf.setFlight_number(flight.getfNumber());
                imtf.setAirline_company(Utils.getAirCompany(flight.getfNumber()));
                imtf.setMeal(flight.getMeal());
                imtf.setId(flight_id);

                Date arrDate = time_format_res.parse(imtf.getArrive_date() + " " + imtf.getArrive_time());
                Date depDate = time_format_res.parse(imtf.getDepart_date() + " " + imtf.getDepart_time());

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

//                if (flightClass.equals(ALL)) {

            Info infoSE = item.getSaverEconomy();
            Info infoSTE = item.getStandartEconomy();
            Info infoFE = item.getFullEconomy();

            if (infoSE != null || infoSTE != null || infoFE != null) {

                info_id = getCabinClass(infoSE, ECONOMY, IMTInfo.SAVER, info_id, award, Integer.parseInt(seats));
                info_id = getCabinClass(infoSTE, ECONOMY, IMTInfo.STANDART, info_id, award, Integer.parseInt(seats));
                info_id = getCabinClass(infoFE, ECONOMY, IMTInfo.FULL, info_id, award, Integer.parseInt(seats));
            }

            Info infoSB = item.getSaverBusiness();
            Info infoSTB = item.getStandartBusiness();
            Info infoFB = item.getFullBusiness();

            if (infoSB != null || infoSTB != null || infoFB != null) {

                info_id = getCabinClass(infoSB, BUSINESS, IMTInfo.SAVER, info_id, award, Integer.parseInt(seats));
                info_id = getCabinClass(infoSTB, BUSINESS, IMTInfo.STANDART, info_id, award, Integer.parseInt(seats));
                info_id = getCabinClass(infoFB, BUSINESS, IMTInfo.FULL, info_id, award, Integer.parseInt(seats));
            }

            Info infoSF = item.getSaverFirst();
            Info infoSTF = item.getStandartFirst();
            Info infoFF = item.getFullFirst();

            if (infoSF != null || infoSTF != null || infoFF != null) {

                info_id = getCabinClass(infoSF, FIRST, IMTInfo.SAVER, info_id, award, Integer.parseInt(seats));
                info_id = getCabinClass(infoSTF, FIRST, IMTInfo.STANDART, info_id, award, Integer.parseInt(seats));
                info_id = getCabinClass(infoFF, FIRST, IMTInfo.FULL, info_id, award, Integer.parseInt(seats));
            }

//                }

//                else if (flightClass.equals(ECONOMY)) {
//
//                    Info info1 = item.getSaverEconomy();
//                    Info info2 = item.getStandartEconomy();
//                    Info info3 = item.getFullEconomy();
//
//                    if (info1 != null || info2 != null || info3 != null) {
//
//                        info_id = getCabinClass(info1, ECONOMY, IMTInfo.SAVER, info_id, award, Integer.parseInt(seats), type);
//                        info_id = getCabinClass(info2, ECONOMY, IMTInfo.STANDART, info_id, award, Integer.parseInt(seats), type);
//                        info_id = getCabinClass(info3, ECONOMY, IMTInfo.FULL, info_id, award, Integer.parseInt(seats), type);
//                    }
//
//                } else if (flightClass.equals(BUSINESS)) {
//
//                    Info info1 = item.getSaverBusiness();
//                    Info info2 = item.getStandartBusiness();
//                    Info info3 = item.getFullBusiness();
//
//                    if (info1 != null || info2 != null || info3 != null) {
//
//                        info_id = getCabinClass(info1, BUSINESS, IMTInfo.SAVER, info_id, award, Integer.parseInt(seats), type);
//                        info_id = getCabinClass(info2, BUSINESS, IMTInfo.STANDART, info_id, award, Integer.parseInt(seats), type);
//                        info_id = getCabinClass(info3, BUSINESS, IMTInfo.FULL, info_id, award, Integer.parseInt(seats), type);
//                    }
//
//                } else if (flightClass.equals(FIRST)) {
//
//                    Info info1 = item.getSaverFirst();
//                    Info info2 = item.getStandartFirst();
//                    Info info3 = item.getFullFirst();
//
//                    if (info1 != null || info2 != null || info3 != null) {
//
//                        info_id = getCabinClass(info1, FIRST, IMTInfo.SAVER, info_id, award, Integer.parseInt(seats), type);
//                        info_id = getCabinClass(info2, FIRST, IMTInfo.STANDART, info_id, award, Integer.parseInt(seats), type);
//                        info_id = getCabinClass(info3, FIRST, IMTInfo.FULL, info_id, award, Integer.parseInt(seats), type);
//                    }
//
//                }

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

        System.out.println("SQ handler. Flight size after processing= [" + awardList.size() + "]");

        IMTDataObject dataObject = new IMTDataObject(awardList);

        Gson gson = new Gson();
        result = gson.toJson(dataObject);

        String callback = Utils.postFlights(requestId, userId, result, "SQ", from, to, seats);
        return awardList;
    }

    private Integer getCabinClass(Info info, String fCabin, String classDescription, int id, IMTAward award, int seats) {

        IMTInfo imti = new IMTInfo();

        if (info != null && info.getStatus() != Info.NA && info.getStatus() != Info.WAITLIST) {

            Integer mileage = Integer.parseInt(info.getMileage().replaceAll(",", "").trim());

            mileage = (mileage - (int) (mileage * 0.15));

            if (info.getTax() != null && info.getTax().trim().length() > 0) {

                Double tax = Double.parseDouble(info.getTax());

                imti.setTax(tax / seats + "");

            } else {

                System.out.println("Empty tax");
            }

//                if(SQParser.TYPE_RT.equals(type)){
//
//                       mileage = mileage / 2;
//                }

            imti.setMileage(mileage + "");
            imti.setName(fCabin);
            imti.setStatus(info.getStringStatus());

            imti.setCurrency(info.getCurrency());
            imti.setId(id);

            System.out.println(fCabin);
            System.out.println(imti.getTax());
            System.out.println(imti.getCurrency());

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
