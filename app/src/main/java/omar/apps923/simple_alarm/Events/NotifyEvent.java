package omar.apps923.simple_alarm.Events;

public class NotifyEvent {
    private String event;
    private String value;
    private Object object;


    public NotifyEvent(String event) {
        this.event = event;
    }

    public NotifyEvent(String event, String value, Object object) {
        this.event = event;
        this.value = value;
        this.object = object;
    }

    public NotifyEvent(String event, String value) {
        this.event = event;
        this.value = value;
    }

    public String getValue() {
        return value;

    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
