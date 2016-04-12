package factory;

import factory.model.IMTAward;
import parser.model.Award;

import java.util.List;

/**
 * Created by Anton on 11.04.2016.
 */
public interface ParserResultHandler {
    List<IMTAward> processResult(List<Award> flights, String flightClass, String seats) throws Exception;
}