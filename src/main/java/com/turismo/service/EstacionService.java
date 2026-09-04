package com.turismo.service;

import com.turismo.model.Estacion;
import com.turismo.repository.EstacionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/** RF-02/RF-11: catalogo de estaciones (lectura por Travel Group Perú y usuario final). */
@Service
public class EstacionService {

    @Autowired 
    private EstacionRepository estacionRepository;

    /** RF-02: excluye del listado las estaciones marcadas como inactivas. */
    public List<Estacion> listarActivas() {
        return estacionRepository.findByEstado("Activa");
    }

    public List<Estacion> listarTodas() {
        return estacionRepository.findAll();
    }

    public Optional<Estacion> buscarPorId(Integer id) {
        return estacionRepository.findById(id);
    }

    public Estacion guardar(Estacion estacion) {
        return estacionRepository.save(estacion);
    }
}
