package com.tomcvt.brickshop.zdemo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
 /*
  * A stale object reference to make relationships with demo entities
  */
@Component
@Profile({"dev", "demo"})
public class DemoCache {
    protected List<Long> demoUsersIds = new ArrayList<>();
    protected List<Long> demoClosedCartsIds = new ArrayList<>();
    protected Long demoCartId = null;
    protected List<Long> demoOrderIds = new ArrayList<>();
    protected List<Long> demoShipmentIds = new ArrayList<>();
}
