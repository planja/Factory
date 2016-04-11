package factory;

/**
 * Created by Anton on 11.04.2016.
 */
public abstract class AbstractFactory {
    public abstract ParserResultHandler getParserResultHandler(ParserName parserName);

    public enum ParserName {
        AA, AC, AF, ANA, AS, BA, BAM, CX, DL, EK, EY, JL, MM, QF, QR, SQ, UA, VS
    }
}
