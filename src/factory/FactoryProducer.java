package factory;

/**
 * Created by Anton on 12.04.2016.
 */
public class FactoryProducer {
    public static AbstractFactory getFactory() {
        return new Factory();
    }
}
