package vaadincrm.intfcs;

/**
 * Created by someone on 16-Aug-2015.
 */
public interface ConsumerInterface<T> {
    public void accept(T t) throws Exception;
}
