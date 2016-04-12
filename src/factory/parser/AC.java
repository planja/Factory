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

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static factory.model.IMTInfo.SAVER;
import static parser.Parser.*;


public class AC implements ParserResultHandler {

    @Override
    public List<IMTAward> processResult(List<Award> flights, String flightClass, String seats) throws Exception {

        String result = "";

        SimpleDateFormat dt_format_or = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat date_format_res = new SimpleDateFormat("yyyy:MM:dd");
        SimpleDateFormat time_format_res = new SimpleDateFormat("HH:mm");

        DatabaseManager dm = new DatabaseManager();

        List<IMTAward> awardList = new LinkedList<IMTAward>();

        for (Award item : flights) {

            int info_id = 0;

            IMTAward award = new IMTAward();

            int flight_id = 0;

            Date previousDate = null;

            List<String> cabinList = new LinkedList<>();

            String previousCabin = null;

            boolean mixed = false;

            for (Flight flight : item.getFlights()) {

                IMTFlight imtf = new IMTFlight();

                imtf.setAircraft(flight.getAircraft());

                Date arrDate = dt_format_or.parse(flight.getArriveDate() + " " + flight.getArriveTime());

                imtf.setArrive_date(date_format_res.format(arrDate));
                imtf.setArrive_place(dm.getCityByCode(flight.getArriveAirport().trim()));
                imtf.setArrive_code(flight.getArriveAirport());
                imtf.setArrive_time(time_format_res.format(arrDate));

                Date depDate = dt_format_or.parse(flight.getDepartDate() + " " + flight.getDepartTime());

                imtf.setLayover_time(Utils.getHoursBetweenDays(previousDate, depDate));
                previousDate = arrDate;

                imtf.setDepart_date(date_format_res.format(depDate));
                imtf.setDepart_place(dm.getCityByCode(flight.getDepartAirport().trim()));
                imtf.setDepart_code(flight.getDepartAirport());
                imtf.setDepart_time(time_format_res.format(depDate));
                imtf.setFlight_number(flight.getFlight());
                imtf.setAirline_company(Utils.getAirCompany(flight.getFlight()));
                imtf.setMeal(flight.getMeal());
                imtf.setTravel_time(flight.getTravelTime());
                imtf.setId(flight_id);

                if (previousCabin != null && !previousCabin.equals(flight.getFlightCabin())) {

                    mixed = true;
                }

                previousCabin = flight.getFlightCabin();
                cabinList.add(flight.getFlightCabin());

                award.getFlight_list().add(imtf);

                flight_id++;
            }

//                if (flightClass.equals(ALL)) {
//                Info infoE = item.getEconomy();
//                Info infoB = item.getBusiness();
//                Info infoPE = item.getPremiumEconomy();
//                Info infoF = item.getFirst();
//
//                if (infoE != null) {
//
//                    info_id = getCabinClass(infoE, ECONOMY, info_id, award, cabinList, mixed, Integer.parseInt(seats));
//                }
//
//                if (infoB != null) {
//
//                    info_id = getCabinClass(infoB, BUSINESS, info_id, award, cabinList, mixed, Integer.parseInt(seats));
//                }
//
//                if (infoPE != null) {
//
//                    info_id = getCabinClass(infoPE, PREMIUM_ECONOMY, info_id, award, cabinList, mixed, Integer.parseInt(seats));
//                }
//
//                if (infoF != null) {
//
//                    info_id = getCabinClass(infoF, FIRST, info_id, award, cabinList, mixed, Integer.parseInt(seats));
//                }
            for (String cabinItem : Utils.getCabins(flightClass)) {

                if (cabinItem.equals(ECONOMY)) {

                    Info info = item.getEconomy();

                    if (info != null) {

                        info_id = getCabinClass(info, ECONOMY, info_id, award, cabinList, mixed, Integer.parseInt(seats), item.getFlights());
                    }

                } else if (cabinItem.equals(BUSINESS)) {

                    Info info = item.getBusiness();

                    if (info != null) {

                        info_id = getCabinClass(info, BUSINESS, info_id, award, cabinList, mixed, Integer.parseInt(seats), item.getFlights());
                    }

                } else if (cabinItem.equals(FIRST)) {

                    Info info = item.getFirst();

                    if (info != null) {

                        info_id = getCabinClass(info, FIRST, info_id, award, cabinList, mixed, Integer.parseInt(seats), item.getFlights());
                    }

                } else if (cabinItem.equals(PREMIUM_ECONOMY)) {

                    Info info = item.getPremiumEconomy();

                    if (info != null) {

                        info_id = getCabinClass(info, PREMIUM_ECONOMY, info_id, award, cabinList, mixed, Integer.parseInt(seats), item.getFlights());
                    }
                }
            }

//                }
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

        System.out.println("AC handler. Flight size after processing= [" + awardList.size() + "]");

        return awardList;
    }

    private Integer getCabinClass(Info info, String fCabin, int id, IMTAward award, List<String> cabinList, boolean mixed, int seats, List<Flight> flightList) throws SQLException {

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

            String mil = info.getMileage();

            imti.setMileage(Integer.parseInt(mil) / seats + "");
//                imti.setName(fCabin);
            imti.setStatus(info.getStringStatus());
            imti.setTax(info.getTax() != null && info.getTax().trim().length() > 0 ? Double.parseDouble(info.getTax().replaceAll(",", "").trim()) / seats + "" : "");
            imti.setCurrency(info.getCurrency());
            imti.setId(id);

            System.out.println("Tax = [" + info.getTax() + "]");

            ExtraData extraData = new ExtraData();

            extraData.setField_name("class_description");
            extraData.setField_type("string");
            extraData.setField_value(SAVER);
            extraData.setField_lvl("class_list");
            extraData.setField_id("class_list_" + id);

            award.getExtra_data().add(extraData);

            int flight_id = 0;

            String currCabin = fCabin;

//                if (mixed) {
            for (String mixedCabins : cabinList) {

                mixed = true;

                extraData = new ExtraData();

                currCabin = Utils.getCabin(mixedCabins, currCabin);

                if (currCabin.equals(FIRST)) {

                    if (flightList.get(flight_id).isUsa() && mixedCabins.equals(FIRST)) {

                        mixedCabins = BUSINESS;

                        if (usa) {

                            currCabin = BUSINESS;
                        }

                    } else if (mixedCabins.equals(FIRST)) {

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

            if (mixed) {

                extraData = new ExtraData();

                extraData.setField_name("mixed_description");
                extraData.setField_type("string");
                extraData.setField_value(currCabin);
                extraData.setField_lvl("class_list");
                extraData.setField_id("class_list_" + id);

                award.getExtra_data().add(extraData);
            }
//                }

            imti.setName(fCabin);

            if (mixed) {

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

