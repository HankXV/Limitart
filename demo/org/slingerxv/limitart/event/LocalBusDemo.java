package org.slingerxv.limitart.event;

/**
 * @author hank
 * @version 2018/4/12 0012 21:47
 */
public class LocalBusDemo {
    public static void main(String[] args) {
        Bus bus = Bus.create();
        bus.addListener(RoleEvent.class, (e) -> System.out.println(e.roleID));
        bus.addListener(RoleEvent.class, e -> System.out.println(e.roleID + "="));
        RoleEvent roleEvent = new RoleEvent();
        roleEvent.roleID = 888;
        bus.postEvent(roleEvent);
        roleEvent.roleID = 999;
        bus.postEvent(roleEvent);
    }

    public static class RoleEvent implements Event {
        private long roleID;
    }
}
