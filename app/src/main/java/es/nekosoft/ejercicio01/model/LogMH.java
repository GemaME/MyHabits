package es.nekosoft.ejercicio01.model;

import java.util.Date;

import es.nekosoft.ejercicio01.R;
import es.nekosoft.ejercicio01.utils.ConstGeofences;


public class LogMH {

    //---- Atributes ---//

    private long id;
    private int type;
    private String title;
    private Date date;


    //---- Constructors ----//

    public LogMH(long id, int type, String title, Date date) {
        this.id = id;
        this.type = type;
        this.title = title;
    }

    public LogMH(int type, String title, Date date) {
        this.type = type;
        this.title = title;
        this.date = date;
    }

    public LogMH() {
    }


    //---- Getter & Setters ----//

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    //---- Icons ---//

    public final static int TYPE_WEATHER = 1;
    public final static int TYPE_LOCATION = 2;
    public final static int TYPE_TRANSPORT = 3;
    public final static int TYPE_PLACE = 4;
    public final static int TYPE_UPDATE = 5;

    public static int getIdIcon(int iconType){

        int result = R.drawable.thermometre_color;

        switch (iconType){

            case TYPE_WEATHER:
                result = R.drawable.thermometre_color;
                break;
            case TYPE_LOCATION:
                result = R.drawable.location_color;
                break;
            case TYPE_TRANSPORT:
                result = R.drawable.car_color;
                break;
            case TYPE_PLACE:
                result = R.drawable.shop_color;
                break;
            case TYPE_UPDATE:
                result = R.drawable.clock_color;
                break;
        }

        return result;
    }


}
