/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package factory.utils;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.lorecraft.phparser.SerializedPhpParser;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.*;

import static parser.Parser.BUSINESS;
import static parser.Parser.BUSINESS_FIRST;
import static parser.Parser.ECONOMY;
import static parser.Parser.ECONOMY_BUSINESS;
import static parser.Parser.FIRST;
import static parser.Parser.PREMIUM_ECONOMY;
import static parser.Parser.UPPER_CLASS;
import static parser.dl.DLParser.*;

/**
 * @author Gulya
 */
public class Utils {

    public static final int AC = 0;
    public static final int UA = 1;
    public static final int NH = 2;
    public static final int SQ = 3;
    public static final int BA = 4;
    public static final int QF = 5;
    public static final int CX = 6;
    public static final int JL = 7;
    public static final int QR = 8;
    public static final int DL = 9;
    public static final int AF = 10;
    public static final int VS = 11;
    public static final int EY = 12;
    public static final int EK = 13;
    public static final int MM = 14;

    public static List<String> getCabins(String cabinValue) {

        List<String> result = new LinkedList<>();

        if (cabinValue != null) {

            SerializedPhpParser serializedPhpParser = new SerializedPhpParser(cabinValue);

            Object serializedCabins = serializedPhpParser.parse();

            Map<Object, String> cabinMap = (Map<Object, String>) serializedCabins;

            Iterator<Object> www = cabinMap.keySet().iterator();

            while (www.hasNext()) {

                Object key = www.next();
                String value = cabinMap.get(key);

                if (value.equals(ECONOMY)) {

                    result.add(ECONOMY);

                } else if (value.equals("P")) {

                    result.add(PREMIUM_ECONOMY);

                } else if (value.equals(BUSINESS)) {

                    result.add(BUSINESS);

                } else if (value.equals(FIRST)) {

                    result.add(FIRST);
                }
            }
        }

        return result;
    }

    public static void main(String[] argc) {

        Set<String> zoneIds = ZoneId.getAvailableZoneIds();

        for (String zoneId : zoneIds) {

            ZoneId zone = ZoneId.of(zoneId);
            ZonedDateTime zonedDateTime = ZonedDateTime.now(zone);

            ZoneOffset offset = zonedDateTime.getOffset();
            String longName = zone.getDisplayName(TextStyle.FULL, Locale.ENGLISH);

            System.out.println("(" + offset + ") " + zoneId + ", " + longName);
        }
    }

    public static String getHoursBetweenDays(Date startDate, Date endDate) throws Exception {

        if (startDate == null) {

            startDate = endDate;
        }

        DateTime dt1 = new DateTime(startDate.getTime());
        DateTime dt2 = new DateTime(endDate.getTime());

        Period p = new Period(dt1, dt2);
        long hours = p.getDays() * 24 + p.getHours();
        long minutes = p.getMinutes();

        String format = String.format("%%0%dd", 2);

        return String.format(format, hours) + ":" + String.format(format, minutes);
    }

    public static String getAirCompany(String flight) {

        flight = flight.replaceAll(" ", "");

        String result = flight.substring(0, 2);

        if (result.contains("9E")) {

            result = "DL";
        }

        return result;
    }

    public static String getAward(String award, String currAward) {

        if (currAward == SAVER) {

            if (award == STANDART) {

                return award;

            } else if (award == FULL) {

                return award;

            } else if (award == SAVER_STANDART) {

                return award;

            } else if (award == STANDART_FULL) {

                return award;
            }

        } else if (currAward == STANDART) {

            if (award == FULL) {

                return award;

            } else if (award == STANDART_FULL) {

                return award;
            }

        } else if (currAward == SAVER_STANDART) {

            if (award == STANDART) {

                return award;

            } else if (award == FULL) {

                return award;

            } else if (award == STANDART_FULL) {

                return award;
            }

        } else if (currAward == STANDART_FULL) {

            if (award == FULL) {

                return award;
            }
        }

        return currAward;
    }

    //    public static final String ECONOMY = "E";
//    public static final String BUSINESS = "B";
//    public static final String FIRST = "F";
//    public static final String PREMIUM_ECONOMY = "PE";
//    public static final String UPPER_CLASS = "B";
//    public static final String ECONOMY_BUSINESS = "MB";
//    public static final String BUSINESS_FIRST = "MF";
    public static String getMixedCabinClass(String cabin) {

        if (cabin.toUpperCase().contains("BUSINESS") && cabin.toUpperCase().contains("ECONOMY")) {

            return BUSINESS;

        } else if (cabin.toUpperCase().contains("BUSINESS") && cabin.toUpperCase().contains("FIRST")) {

            return FIRST;

        } else if (cabin.toUpperCase().contains("ECONOMY") && !cabin.toUpperCase().contains("PREMIUM")) {

            return ECONOMY;

        } else if (cabin.toUpperCase().contains("BUSINESS")) {

            return BUSINESS;

        } else if (cabin.toUpperCase().contains("FIRST")) {

            return FIRST;

        } else if (cabin.toUpperCase().contains("PREMIUM")) {

            return PREMIUM_ECONOMY;

        } else if (cabin.toUpperCase().contains("UPPER")) {

            return UPPER_CLASS;

        }

        return cabin;
    }

    public static String getCabin(String cabin, String currCabin) {

        if (currCabin == ECONOMY) {

            if (cabin == BUSINESS) {

                return cabin;

            } else if (cabin == FIRST) {

                return cabin;

            } else if (cabin == PREMIUM_ECONOMY) {

                return cabin;

            } else if (cabin == UPPER_CLASS) {

                return cabin;

            } else if (cabin == ECONOMY_BUSINESS) {

                return cabin;

            } else if (cabin == BUSINESS_FIRST) {

                return cabin;
            }

        } else if (currCabin == BUSINESS) {

            if (cabin == FIRST) {

                return cabin;

            } else if (cabin == UPPER_CLASS) {

                return cabin;

            } else if (cabin == BUSINESS_FIRST) {

                return cabin;
            }

        } else if (currCabin == FIRST) {

            return currCabin;

        } else if (currCabin == PREMIUM_ECONOMY) {

            if (cabin == BUSINESS) {

                return cabin;

            } else if (cabin == FIRST) {

                return cabin;

            } else if (cabin == UPPER_CLASS) {

                return cabin;

            } else if (cabin == ECONOMY_BUSINESS) {

                return cabin;

            } else if (cabin == BUSINESS_FIRST) {

                return cabin;
            }

        } else if (currCabin == UPPER_CLASS) {

            return cabin;

        } else if (currCabin == ECONOMY_BUSINESS) {

            if (cabin == BUSINESS) {

                return cabin;

            } else if (cabin == FIRST) {

                return cabin;

            } else if (cabin == PREMIUM_ECONOMY) {

                return cabin;

            } else if (cabin == UPPER_CLASS) {

                return cabin;

            } else if (cabin == BUSINESS_FIRST) {

                return cabin;
            }

        } else if (currCabin == BUSINESS_FIRST) {

            if (cabin == FIRST) {

                return cabin;

            } else if (cabin == UPPER_CLASS) {

                return cabin;
            }
        }

        return currCabin;
    }

    public static String[] checkAirport(String airport, int parser) {

        // NYC - JFK, EWR,  PAR-CDG, LON-LHR, MOW-DME, TYO-NRT, MIL-MXP,BUE-EZE, RIO-GIG, WAS-IAD 
        if (parser == AC) {

            if (airport.contains("NYC")) {

                return new String[]{"JFK", "EWR"};

            } else if (airport.contains("PAR")) {

                return new String[]{"CDG"};

            } else if (airport.contains("LON")) {

                return new String[]{"LHR"};

            } else if (airport.contains("MOW")) {

                return new String[]{"DME"};

            } else if (airport.contains("TYO")) {

                return new String[]{"NRT"};

            } else if (airport.contains("MIL")) {

                return new String[]{"MXP"};

            } else if (airport.contains("BUE")) {

                return new String[]{"EZE"};

            } else if (airport.contains("RIO")) {

                return new String[]{"GIG"};

            } else if (airport.contains("WAS")) {

                return new String[]{"IAD"};
            }

            return new String[]{airport};

        } else if (parser == EK) {

            if (airport.contains("NYC")) {

                return new String[]{"JFK"};

            } else if (airport.contains("PAR")) {

                return new String[]{"CDG"};

            } else if (airport.contains("MOW")) {

                return new String[]{"DME"};

            } else if (airport.contains("MIL")) {

                return new String[]{"MXP"};

            } else if (airport.contains("BUE")) {

                return new String[]{"EZE"};

            } else if (airport.contains("WAS")) {

                return new String[]{"IAD"};

            } else if (airport.contains("TYO")) {

                return new String[]{"NRT"};

            } else if (airport.contains("LON")) {

                return new String[]{"LHR", "LGW"};
            }

            return new String[]{airport};

        } else if (parser == MM) {

//BUE - EZE
//LON - LHR
//MIL - MXP
//MOW - DME
//NYC - JFK,EWR
//PAR - CDG,ORY
//RIO - GIG
//TYO - NRT
//WAS - IAD
            if (airport.contains("BUE")) {

                return new String[]{"EZE"};

            } else if (airport.contains("LON")) {

                return new String[]{"LHR"};

            } else if (airport.contains("MIL")) {

                return new String[]{"MXP"};

            } else if (airport.contains("MOW")) {

                return new String[]{"DME"};

            } else if (airport.contains("NYC")) {

                return new String[]{"JFK", "EWR"};

            } else if (airport.contains("PAR")) {

                return new String[]{"CDG", " ORY"};

            } else if (airport.contains("RIO")) {

                return new String[]{"GIG"};

            } else if (airport.contains("TYO")) {

                return new String[]{"NRT"};

            } else if (airport.contains("WAS")) {

                return new String[]{"IAD"};

            }

//            else if(airport.contains("KBP")){
//                
//                return new String[]{"IEV"};
//            }
            return new String[]{airport};
        }

        //13)  EK - NYC-JFK, PAR-CDG, LON-LHR LGW, MOW-DME, TYO-NRT, MIL-MXP, BUE-EZE, WAS-IAD
        return new String[]{airport};
    }

    public static String replaceAirport(String airport, int parser) {

        if (parser == AC) {

            return airport;

        } else if (parser == UA) {

            if (airport.contains("PAR")) {

                return "CDG";
            }

            return airport;

        } else if (parser == NH) {

            if (airport.contains("MOW")) {

                return "DME";
            }

            return airport;

        } else if (parser == SQ) {

            if (airport.contains("NYC")) {

                return "JFK";

            } else if (airport.contains("PAR")) {

                return "CDG";

            } else if (airport.contains("LON")) {

                return "LHR";

            } else if (airport.contains("MOW")) {

                return "DME";

            } else if (airport.contains("MIL")) {

                return "MXP";

            } else if (airport.contains("TYO")) {

                return "NRT";
            }

            return airport;

        } else if (parser == BA) {

            if (airport.contains("MOW")) {

                return "SVO";
            }

            return airport;

        } else if (parser == QF) {

            //NYC- JFK, PAR-CDG, LON-LHR, MOW-DME, TYO-NRT, MIL-MXP,BUE-EZE, RIO-GIG,WAS-IAD
            if (airport.contains("MOW")) {

                return "DME";

            } else if (airport.contains("NYC")) {

                return "JFK";

            } else if (airport.contains("PAR")) {

                return "CDG";

            } else if (airport.contains("LON")) {

                return "LHR";

            } else if (airport.contains("TYO")) {

                return "NRT";

            } else if (airport.contains("MIL")) {

                return "MXP";

            } else if (airport.contains("BUE")) {

                return "EZE";

            } else if (airport.contains("RIO")) {

                return "GIG";

            } else if (airport.contains("WAS")) {

                return "IAD";
            }

            return airport;

        } else if (parser == CX) {

            //NYC - JFK, PAR-CDG, TYO - NRT
            if (airport.contains("LON")) {

                return "LHR";

            } else if (airport.contains("MOW")) {

                return "DME";

            } else if (airport.contains("MIL")) {

                return "MXP";

            } else if (airport.contains("NYC")) {

                return "JFK";

            } else if (airport.contains("PAR")) {

                return "CDG";

            } else if (airport.contains("TYO")) {

                return "NRT";
            }

            return airport;

        } else if (parser == JL) {

            if (airport.contains("NYC")) {

                return "JFK";

            } else if (airport.contains("PAR")) {

                return "CDG";

            } else if (airport.contains("LON")) {

                return "LHR";

            } else if (airport.contains("MOW")) {

                return "DME";

            } else if (airport.contains("TYO")) {

                return "NRT";
            }

            return airport;

        } else if (parser == QR) {

            if (airport.contains("NYC")) {

                return "JFK";

            } else if (airport.contains("PAR")) {

                return "CDG";

            } else if (airport.contains("LON")) {

                return "LHR";

            } else if (airport.contains("MOW")) {

                return "DME";

            } else if (airport.contains("MIL")) {

                return "MXP";

            } else if (airport.contains("BUE")) {

                return "EZE";

            } else if (airport.contains("WAS")) {

                return "IAD";

            } else if (airport.contains("TYO")) {

                return "NRT";
            }

            return airport;

        } else if (parser == DL) {

            if (airport.contains("PAR")) {

                return "CDG";

            } else if (airport.contains("MOW")) {

                return "SVO";

            } else if (airport.contains("MIL")) {

                return "MXP";

            } else if (airport.contains("RIO")) {

                return "GIG";
            }

            return airport;

        } else if (parser == AF) {

            if (airport.contains("NYC")) {

                return "EWR";

            } else if (airport.contains("LON")) {

                return "LGW";

            } else if (airport.contains("MOW")) {

                return "SVO";
            }

            return airport;

        } else if (parser == EY) {

            if (airport.contains("NYC")) {

                return "JFK";

            } else if (airport.contains("PAR")) {

                return "CDG";

            } else if (airport.contains("LON")) {

                return "LHR";

            } else if (airport.contains("MOW")) {

                return "DME";

            } else if (airport.contains("TYO")) {

                return "NRT";

            } else if (airport.contains("MIL")) {

                return "MXP";

            } else if (airport.contains("WAS")) {

                return "IAD";
            }

            return airport;

        } else if (parser == EK) {

            if (airport.contains("NYC")) {

                return "JFK";

            } else if (airport.contains("PAR")) {

                return "CDG";

            } else if (airport.contains("MOW")) {

                return "DME";

            } else if (airport.contains("MIL")) {

                return "MXP";

            } else if (airport.contains("BUE")) {

                return "EZE";

            } else if (airport.contains("WAS")) {

                return "IAD";
            }

            return airport;
        }

        return airport;
    }


}
