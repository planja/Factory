package factory.parser;

import com.google.gson.Gson;
import factory.ParserResultHandler;
import factory.db.manager.DatabaseManager;
import factory.model.*;
import factory.utils.Utils;
import parser.model.Award;
import parser.model.Flight;
import parser.model.Info;
import parser.model.Stop;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

import static factory.model.IMTInfo.SAVER;
import static parser.Parser.*;

public class DL implements ParserResultHandler {

    @Override
    public List<IMTAward> handleResponse(List<Award> flights, String flightClass, String seats, IMTError error, String requestId, String userId, String from, String to) throws Exception {
        String result = "";
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat date_format_res = new SimpleDateFormat("yyyy:MM:dd");
        SimpleDateFormat time_format_pac = new SimpleDateFormat("EEE MMM dd yyyy h:mma", Locale.US);
        SimpleDateFormat time_format_res = new SimpleDateFormat("HH:mm");

        SimpleDateFormat time_format = new SimpleDateFormat("yyyy:MM:dd HH:mm");
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
                imtf.setArrive_place(dm.getCityByCode(flight.getArriveAirport().trim()));
                imtf.setArrive_code(flight.getArriveAirport());

                Calendar c = new GregorianCalendar();
                //проверить
                c.setTime(sdf.parse(flight.getDepartDate()));

                Date arrTime = time_format_pac.parse(flight.getArriveDate() + " " + c.get(Calendar.YEAR) + " " + flight.getArriveTime());

                imtf.setArrive_date(date_format_res.format(arrTime));
                imtf.setArrive_time(time_format_res.format(arrTime));

                imtf.setDepart_place(dm.getCityByCode(flight.getDepartAirport().trim()));
                imtf.setDepart_code(flight.getDepartAirport());

                Date depTime = time_format_pac.parse(flight.getDepartDate() + " " + c.get(Calendar.YEAR) + " " + flight.getDepartTime());

                imtf.setDepart_date(date_format_res.format(depTime));
                imtf.setDepart_time(time_format_res.format(depTime));
                imtf.setFlight_number(flight.getFlight());
                imtf.setAirline_company(Utils.getAirCompany(flight.getFlight()));
                imtf.setMeal(flight.getMeal());
                imtf.setTravel_time(flight.getTravelTime());
                imtf.setId(flight_id);

                Date depDate = time_format.parse(imtf.getDepart_date() + " " + imtf.getDepart_time());

                imtf.setLayover_time(Utils.getHoursBetweenDays(previousDate, depDate));
                previousDate = arrTime;

                award.getFlight_list().add(imtf);

                flight_id++;
            }

            int fId = 0;

            for (Flight flight : item.getFlights()) {

                for (Stop stop : flight.getHiddenStopList()) {

                    ExtraData extraData = new ExtraData();

                    extraData.setField_name("hidden_stop");
                    extraData.setField_type("string");
                    extraData.setField_value("(" + stop.getAirport() + ")" + " " + stop.getDuration());
                    extraData.setField_lvl("flight_list");
                    extraData.setField_id("flight_list_" + fId);

                    award.getExtra_data().add(extraData);
                }

                fId++;
            }

            award.setTotal_duration(item.getTotalDuration());

            for (String cabinItem : Utils.getCabins(flightClass)) {

                if (cabinItem.equals(ECONOMY)) {

                    Info info = item.getEconomy();

                    if (info != null) {

                        info_id = getCabinClass(info, ECONOMY, info_id, award, item.getFlights());
                    }

                } else if (cabinItem.equals(BUSINESS)) {

                    Info info = item.getBusiness();

                    if (info != null) {

                        info_id = getCabinClass(info, BUSINESS, info_id, award, item.getFlights());
                    }

                } else if (cabinItem.equals(FIRST)) {

                    Info info = item.getFirst();

                    if (info != null) {

                        info_id = getCabinClass(info, FIRST, info_id, award, item.getFlights());
                    }

                }
            }

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

        System.out.println("DL handler. Flight size after processing= [" + awardList.size() + "]");

        List<IMTAward> resultList = new LinkedList<>();

        for (IMTAward award : awardList) {

            if (award.split) {

                int i = 0;

                for (IMTInfo info : award.getClass_list()) {

                    IMTAward imta = new IMTAward();

                    imta.getClass_list().add(info);

                    for (ExtraData ed : award.getExtra_data()) {

                        if (ed.getField_id().contains("class_list_" + i)) {

                            ed.setField_id("class_list_0");

                            imta.getExtra_data().add(ed);

                        } else if (!ed.getField_id().contains("class_list_")) {

                            imta.getExtra_data().add(ed);
                        }
                    }

                    imta.getFlight_list().addAll(award.getFlight_list());
                    imta.setTotal_duration(award.getTotal_duration());
                    info.setId(0);
                    resultList.add(imta);

                    i++;
                }

            } else {

                resultList.add(award);
            }
        }

        IMTDataObject dataObject = new IMTDataObject(resultList);

        Gson gson = new Gson();

        result = gson.toJson(dataObject);
        return resultList;

    }

    private Integer getCabinClass(Info info, String fCabin, int id, IMTAward award, List<Flight> fList) throws SQLException {

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

            imti.setMileage(info.getMileage().replaceAll(" ", ""));
            imti.setStatus(info.getStringStatus());
            imti.setTax(info.getTax().replaceAll(",", ""));
            imti.setCurrency(info.getCurrency());
            imti.setId(id);

            int flight_id = 0;

            String currAward = SAVER;

            for (String awardName : info.getAwardNames()) {

                currAward = Utils.getAward(awardName, currAward);

                ExtraData extraData = new ExtraData();

                extraData.setField_name("class_description");
                extraData.setField_type("string");
                extraData.setField_value(awardName);
                extraData.setField_lvl("class_list");
                extraData.setField_id("class_list_" + id);
                extraData.setField_sub_lvl("flight_list_" + flight_id);

                award.getExtra_data().add(extraData);

                flight_id++;
            }

            ExtraData extraData = new ExtraData();

            extraData.setField_name("award_description");
            extraData.setField_type("string");
            extraData.setField_value(currAward);
            extraData.setField_lvl("class_list");
            extraData.setField_id("class_list_" + id);

            award.getExtra_data().add(extraData);

            String currCabin = fCabin;

            if (info.isMixed()) {

                award.split = true;

                flight_id = 0;

                for (String mixedCabins : info.getMixedCabins()) {

                    mixedCabins = Utils.getMixedCabinClass(mixedCabins);

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

                    extraData = new ExtraData();

                    extraData.setField_name("mixed_cabins");
                    extraData.setField_type("string");
                    extraData.setField_value(mixedCabins);
                    extraData.setField_lvl("class_list");
                    extraData.setField_id("class_list_" + id);
                    extraData.setField_sub_lvl("flight_list_" + flight_id);

                    award.getExtra_data().add(extraData);

                    flight_id++;
                }

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
