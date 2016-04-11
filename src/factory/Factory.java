package factory;

import factory.parser.*;

/**
 * Created by Anton on 12.04.2016.
 */
public class Factory extends AbstractFactory {

    //   public enum ParserName{AA,AC,AF,ANA,AS,BA,BAM,CX,DL,EK,EY,JL,MM,QF,QR,SQ,UA,VS}

    @Override
    public ParserResultHandler getParserResultHandler(ParserName parser) {
        switch (parser) {
            case AA:
                return new AA();

            case AC:
                return new AC();

            case AF:
                return new AF();

            case ANA:
                return new ANA();

            case AS:
                return new AS();

            case BA:
                return new BA();

            case BAM:
                return new BAM();

            case CX:
                return new CX();

            case DL:
                return new DL();

            case EK:
                return new EK();

            case EY:
                return new EY();

            case JL:
                return new JL();

            case MM:
                return new MM();

            case QF:
                return new QF();

            case QR:
                return new QR();

            case SQ:
                return new SQ();

            case UA:
                return new UA();

            case VS:
                return new VS();

            default:
                return null;

        }
    }
}
