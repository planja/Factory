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
import parser.qf.QFParser;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static parser.Parser.*;

public class QF implements ParserResultHandler {

    @Override
    public List<IMTAward> processResult(List<Award> flights, String flightClass, String seats) throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat dt_format_or_time = new SimpleDateFormat("EEE dd MMM yy HH:mm", Locale.US);
        SimpleDateFormat date_format_res = new SimpleDateFormat("yyyy:MM:dd");

        String result;
        System.out.println("QF handler. Flight size before processing= [" + flights.size() + "]");
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

                Date arrDate = dt_format_or_time.parse(flight.getArriveDate().trim() + " " + flight.getArriveTime());

                imtf.setArrive_date(date_format_res.format(arrDate));
                imtf.setArrive_place(flight.getArriveAirport() == null || flight.getArriveAirport().trim().length() == 0 ? flight.getArrivePlace() : dm.getCityByCode(flight.getArriveAirport().trim()));
                imtf.setArrive_code(flight.getArriveAirport() == null || flight.getArriveAirport().trim().length() == 0 ? flight.getArrivePlace() : flight.getArriveAirport());

                imtf.setArrive_time(flight.getArriveTime());

                Date depDate = dt_format_or_time.parse(flight.getDepartDate().trim() + " " + flight.getDepartTime());

                imtf.setLayover_time(Utils.getHoursBetweenDays(previousDate, depDate));
                previousDate = arrDate;

                imtf.setDepart_date(date_format_res.format(depDate));

                imtf.setDepart_place(flight.getDepartAirport() == null || flight.getDepartAirport().trim().length() == 0 ? flight.getDepartPlace() : dm.getCityByCode(flight.getDepartAirport().trim()));
                imtf.setDepart_code(flight.getDepartAirport() == null || flight.getDepartAirport().trim().length() == 0 ? flight.getDepartPlace() : flight.getDepartAirport());

                imtf.setDepart_time(flight.getDepartTime());
                imtf.setFlight_number(flight.getFlight());
                imtf.setAirline_company(Utils.getAirCompany(flight.getFlight()));
                imtf.setMeal(flight.getMeal());
                imtf.setTravel_time(flight.getTravelTime());
                imtf.setId(flight_id);

                award.getFlight_list().add(imtf);

                flight_id++;
            }

            String totalDuration = item.getTotalDuration();

            if ((totalDuration == null || totalDuration.trim().length() == 0) && award.getFlight_list().size() < 2) {

                award.setTotal_duration(parser.utils.Utils.getTotalTime(award.getFlight_list().get(0).getTravel_time(), new QFParser()));

            } else {

                award.setTotal_duration(totalDuration);
            }

            for (String cabinItem : Utils.getCabins(flightClass)) {

                if (cabinItem.equals(ECONOMY)) {

                    Info infoSE = item.getSaverEconomy();

                    if (infoSE != null) {

                        info_id = getCabinClass(infoSE, ECONOMY, IMTInfo.SAVER, info_id, award, item.getFlights());
                    }

                } else if (cabinItem.equals(BUSINESS)) {

                    Info infoSB = item.getSaverBusiness();

                    if (infoSB != null) {

                        info_id = getCabinClass(infoSB, BUSINESS, IMTInfo.SAVER, info_id, award, item.getFlights());
                    }

                } else if (cabinItem.equals(FIRST)) {

                    Info infoSF = item.getSaverFirst();

                    if (infoSF != null) {

                        info_id = getCabinClass(infoSF, FIRST, IMTInfo.SAVER, info_id, award, item.getFlights());
                    }

                } else if (cabinItem.equals(PREMIUM_ECONOMY)) {

                    Info infoSP = item.getSaverPremium();

                    if (infoSP != null) {

                        info_id = getCabinClass(infoSP, PREMIUM_ECONOMY, IMTInfo.SAVER, info_id, award, item.getFlights());
                    }

                }
            }

            if (award.getClass_list().size() > 0) {

                awardList.add(award);
            }
        }

        System.out.println("QF handler. Flight size after processing= [" + awardList.size() + "]");

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

                System.out.println(depart);
                System.out.println(arrive);

                if (depart && arrive) {

                    flight.setUsa(true);
                    usa = true;

                } else {

                    flight.setUsa(false);
                }
            }

            imti.setMileage(info.getMileage().replaceAll("K", "000"));
//                imti.setName(fCabin);
            imti.setStatus(info.getStringStatus());
            imti.setTax(info.getTax());
            imti.setId(id);

//                award.getClass_list().add(imti);

            ExtraData extraData = new ExtraData();

            extraData.setField_name("class_description");
            extraData.setField_type("string");
            extraData.setField_value(classDescription);
            extraData.setField_lvl("class_list");
            extraData.setField_id("class_list_" + id);

            award.getExtra_data().add(extraData);

            String currCabin = fCabin;

            if (info.isMixed()) {

                int flight_id = 0;

                for (String mixedCabins : info.getMixedCabins()) {

                    Mixed mixed = checkMixed(mixedCabins);

                    int f_id = 0;

                    for (IMTFlight flight : award.getFlight_list()) {

                        System.out.println(flight.getDepart_place());
                        System.out.println(mixed.getFrom());

                        if (flight.getDepart_place().replaceAll("-", " ").toLowerCase().contains(mixed.getFrom().split(" ").length > 0 ? mixed.getFrom().toLowerCase().split(" ")[0].trim() : mixed.getFrom().toLowerCase())) {

                            System.out.println("FID = [" + f_id + "]");

                            break;
                        }

                        f_id++;
                    }

                    extraData = new ExtraData();

                    mixedCabins = Utils.getMixedCabinClass(mixed.getCabin());

                    currCabin = Utils.getCabin(mixedCabins, currCabin);

                    if (currCabin.equals(FIRST)) {

                        System.out.println("Curr Cabin " + currCabin);
                        System.out.println("Mixed Cabin " + mixedCabins);

                        System.out.println("Is USA " + fList.get(f_id).isUsa());

                        if (fList.get(f_id).isUsa() && mixedCabins.equals(FIRST)) {

                            mixedCabins = BUSINESS;

                            if (usa) {

                                currCabin = BUSINESS;
                            }

                        } else if (mixedCabins.equals(FIRST)) {

                            usa = false;
                            currCabin = FIRST;
                        }
                    }

                    System.out.println(currCabin);
                    System.out.println(mixedCabins);

                    extraData.setField_name("mixed_cabins");
                    extraData.setField_type("string");
                    extraData.setField_value(mixedCabins);
                    extraData.setField_lvl("class_list");
                    extraData.setField_id("class_list_" + id);
                    extraData.setField_sub_lvl("flight_list_" + f_id);

                    award.getExtra_data().add(extraData);

                    flight_id++;
                }

                if (currCabin.equals(FIRST) && usa) {

                    currCabin = BUSINESS;
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

    private Mixed checkMixed(String value) {

        Pattern pattern = Pattern.compile("(\\D*)\\sto\\s(\\D*):\\s(\\D*)");

        Matcher matcher = pattern.matcher(value);

        if (matcher.find()) {

            return new Mixed(matcher.group(1).trim(), matcher.group(2).trim(), matcher.group(3).trim());
        }

        return null;
    }

    private class Mixed {

        private String from;
        private String to;
        private String cabin;

        public Mixed(String from, String to, String cabin) {

            this.from = from;
            this.to = to;
            this.cabin = cabin;
        }

        /**
         * @return the from
         */
        public String getFrom() {
            return from;
        }

        /**
         * @param from the from to set
         */
        public void setFrom(String from) {
            this.from = from;
        }

        /**
         * @return the to
         */
        public String getTo() {
            return to;
        }

        /**
         * @param to the to to set
         */
        public void setTo(String to) {
            this.to = to;
        }

        /**
         * @return the cabin
         */
        public String getCabin() {
            return cabin;
        }

        /**
         * @param cabin the cabin to set
         */
        public void setCabin(String cabin) {
            this.cabin = cabin;
        }
    }

}

