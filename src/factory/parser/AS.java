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

import static parser.Parser.*;

public class AS implements ParserResultHandler {

    @Override
    public List<IMTAward> processResult(List<Award> flights, String flightClass, String seats) throws Exception {
        String result = "";

        SimpleDateFormat dt_format_or = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat date_format_res = new SimpleDateFormat("yyyy:MM:dd");
        SimpleDateFormat time_format_res = new SimpleDateFormat("HH:mm");


        List<IMTAward> awardList = new LinkedList<IMTAward>();

        DatabaseManager dm = new DatabaseManager();

        for (Award item : flights) {

            int info_id = 0;

            IMTAward award = new IMTAward();

            for (String cabinItem : Utils.getCabins(flightClass)) {

                if (cabinItem.equals(ECONOMY)) {

                    Info infoSE = item.getSaverEconomy();
                    Info infoFE = item.getFullEconomy();

                    if (infoSE != null) {

                        info_id = getCabinClass(infoSE, ECONOMY, IMTInfo.SAVER, info_id, award, item.getFlights());
                    }

                    if (infoFE != null) {

                        info_id = getCabinClass(infoFE, ECONOMY, IMTInfo.FULL, info_id, award, item.getFlights());
                    }

                } else if (cabinItem.equals(BUSINESS)) {

                    Info infoSB = item.getSaverBusiness();

                    if (infoSB != null) {

                        info_id = getCabinClass(infoSB, BUSINESS, IMTInfo.SAVER, info_id, award, item.getFlights());
                    }

                } else if (cabinItem.equals(FIRST)) {

                    Info infoSF = item.getSaverFirst();
                    Info infoFF = item.getFullFirst();

                    if (infoSF != null) {

                        info_id = getCabinClass(infoSF, FIRST, IMTInfo.SAVER, info_id, award, item.getFlights());
                    }

                    if (infoFF != null) {

                        info_id = getCabinClass(infoFF, FIRST, IMTInfo.FULL, info_id, award, item.getFlights());
                    }

                } else if (cabinItem.equals(PREMIUM_ECONOMY)) {

                    Info infoSP = item.getSaverPremium();

                    if (infoSP != null) {

                        info_id = getCabinClass(infoSP, PREMIUM_ECONOMY, IMTInfo.SAVER, info_id, award, item.getFlights());
                    }
                }
            }

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
                imtf.setTravel_time(Utils.getHoursBetweenDays(depDate, arrDate));
                imtf.setId(flight_id);

                award.getFlight_list().add(imtf);

                flight_id++;
            }

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

            if (award.getClass_list().size() > 0) {

                awardList.add(award);
            }
        }

        System.out.println("AS handler. Flight size after processing= [" + awardList.size() + "]");

        return awardList;
    }

    private Integer getCabinClass(Info info, String fCabin, String classDescription, int id, IMTAward award, List<Flight> fList) throws SQLException {

        IMTInfo imti = new IMTInfo();

        if (info != null && info.getStatus() != Info.NA) {

            DatabaseManager dm = new DatabaseManager();

            boolean usa = false;

            for (Flight flight : fList) {

                boolean depart = dm.isUSA(flight.getDepartAirport());
                boolean arrive = dm.isUSA(flight.getArriveAirport());

                if (depart && arrive) {

                    flight.setUsa(true);
                    usa = true;

                } else {

                    flight.setUsa(false);
                }
            }


            Double dMileage = Double.parseDouble(info.getMileage().replaceAll("k", "").trim()) * 1000;

            imti.setMileage(dMileage.intValue() + "");
//                imti.setName(fCabin);
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

//                award.getClass_list().add(imti);

            int flight_id = 0;

            boolean mixed = false;

            String currCabin = fCabin;

            for (String mixedCabins : info.getMixedCabins()) {

                mixed = true;

                extraData = new ExtraData();

                currCabin = Utils.getCabin(mixedCabins, currCabin);

                if (currCabin.equals(FIRST)) {

                    if (fList.get(flight_id).isUsa() && mixedCabins.equals(FIRST)) {

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

            imti.setName(fCabin);

            if (info.isMixed()) {

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
