/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
package com.proyecto.service;

import com.proyecto.domain.ActividadEconomica;
import com.proyecto.repository.ActividadEconomicaRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ActividadEconomicaService {

    @Autowired
    private ActividadEconomicaRepository actividadRepository;

    public List<ActividadEconomica> getTodas() {
        return actividadRepository.findAll();
    }
}
 */

