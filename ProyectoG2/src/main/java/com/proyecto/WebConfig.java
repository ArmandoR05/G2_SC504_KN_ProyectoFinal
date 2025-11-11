/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.proyecto;

 

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry r) {
        r.addViewController("/index").setViewName("index");
        r.addViewController("/").setViewName("index");                 
        r.addViewController("/productos").setViewName("productos/lista"); 
        r.addViewController("/productos/crear").setViewName("productos/crear");
    }
}

