package com.tomcvt.brickshop.zdemo;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({"dev"})
public class DevUsers {
    
}
