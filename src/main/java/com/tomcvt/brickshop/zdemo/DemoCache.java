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
    public List<Long> demoUsersIds = new ArrayList<>();
    public List<Long> demoClosedCartsIds = new ArrayList<>();
    public Long demoCartId = null;
    public List<Long> demoOrderIds = new ArrayList<>();
    public List<Long> demoShipmentIds = new ArrayList<>();
}
