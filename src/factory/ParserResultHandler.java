package factory;

import factory.model.IMTAward;
import factory.model.IMTError;
import parser.model.Award;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

/**
 * Created by Anton on 11.04.2016.
 */
public interface ParserResultHandler {
    List<IMTAward> handleResponse(List<Award> flights, String flightClass, String seats, IMTError error, String requestId, String userId, String from, String to) throws ParseException, SQLException, Exception;
}