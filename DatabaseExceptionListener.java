/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mili
 */
public interface DatabaseExceptionListener {
    public List<ExceptionListener> listeners = new ArrayList<ExceptionListener>();
    public default void addListener(ExceptionListener o){
        listeners.add(o);
    }
    
    public default void notify(String selfmsg,String sqlmsg){
        for(ExceptionListener l: listeners){
            l.onException(selfmsg,sqlmsg);
    }
    }
}
