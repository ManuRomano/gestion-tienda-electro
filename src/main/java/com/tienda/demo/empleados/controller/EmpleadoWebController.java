package com.tienda.demo.empleados.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tienda.demo.empleados.dto.EmpleadoRequest;
import com.tienda.demo.empleados.service.EmpleadoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/empleados")
@RequiredArgsConstructor
public class EmpleadoWebController {

    private final EmpleadoService service;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("empleados", service.listarTodos());
        return "empleados/listarEmpleados";
    }

    @GetMapping("/nuevo")
    public String formularioNuevo(Model model) {
        model.addAttribute("empleadoRequest", new EmpleadoRequest());
        return "empleados/crearEmpleados";
    }

    @PostMapping
    public String crear(@Valid @ModelAttribute("empleadoRequest") EmpleadoRequest request,
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "empleados/crearEmpleados"; // si algo no valida, vuelve al formulario con los errores
        }
        service.crear(request);
        return "redirect:/empleados";
    }
}