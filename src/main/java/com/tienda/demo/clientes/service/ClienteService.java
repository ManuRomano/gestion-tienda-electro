package com.tienda.demo.clientes.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.tienda.demo.clientes.dto.ClienteRequest;
import com.tienda.demo.clientes.model.Cliente;
import com.tienda.demo.clientes.repository.ClienteRepository;

import lombok.RequiredArgsConstructor;

/**
 * Lógica de negocio del módulo de clientes.
 *
 * <p>Es el único punto por el que otros módulos (por ejemplo {@code pedidos})
 * deben acceder a los clientes: nunca tocan {@link ClienteRepository}
 * directamente. Así se mantienen las fronteras entre módulos, lo que facilitará
 * una futura migración a microservicios.</p>
 */
@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository repository;

    /**
     * Devuelve todos los clientes registrados, activos o no.
     *
     * @return lista completa de clientes para mostrar en pantalla.
     */
    public List<Cliente> listarTodos() {
        return repository.findAll();
    }

    /**
     * Devuelve solo los clientes que pueden comprar (no dados de baja).
     *
     * <p>Lo usa el asistente de compra: no tiene sentido ofrecer en la venta a
     * un cliente que luego sería rechazado por {@link #buscarActivo(Long)}.</p>
     *
     * @return clientes con {@code activo = true}.
     */
    public List<Cliente> listarActivos() {
        return repository.findAll().stream()
                .filter(Cliente::isActivo)
                .toList();
    }

    /**
     * Da de alta un nuevo cliente a partir de los datos del formulario.
     *
     * <p>El cliente nace {@code activo} (puede comprar) y sin
     * {@code fechaPrimeraCompra}: esa fecha se rellenará sola cuando realice
     * su primer pedido, no al registrarse.</p>
     *
     * @param request datos validados que llegan desde la vista.
     * @return el cliente ya persistido, con su id generado.
     */
    public Cliente crear(ClienteRequest request) {
        Cliente cliente = new Cliente();
        cliente.setNombre(request.getNombre());
        cliente.setApellidos(request.getApellidos());
        cliente.setNif(request.getNif());
        cliente.setEmail(request.getEmail());
        cliente.setTelefono(request.getTelefono());
        cliente.setDireccion(request.getDireccion());
        cliente.setActivo(true);
        cliente.setFechaPrimeraCompra(null);
        return repository.save(cliente);
    }

    /**
     * Busca un cliente que pueda comprar (existe y está activo).
     *
     * <p>Lo usa el proceso de pedido para garantizar que no se venda a un
     * cliente inexistente o dado de baja.</p>
     *
     * @param id identificador del cliente.
     * @return el cliente activo encontrado.
     * @throws IllegalArgumentException si no existe.
     * @throws IllegalStateException    si existe pero está dado de baja.
     */
    public Cliente buscarActivo(Long id) {
        Cliente cliente = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("No existe el cliente con id " + id));
        if (!cliente.isActivo()) {
            throw new IllegalStateException("El cliente " + id + " está dado de baja y no puede comprar");
        }
        return cliente;
    }

    /**
     * Marca la fecha de la primera compra del cliente si aún no la tenía.
     *
     * <p>Se invoca al crear un pedido. Si el cliente ya había comprado antes
     * no se modifica, de modo que el campo conserva siempre la fecha de la
     * compra más antigua.</p>
     *
     * @param cliente cliente que acaba de realizar un pedido.
     */
    public void registrarPrimeraCompra(Cliente cliente) {
        if (cliente.getFechaPrimeraCompra() == null) {
            cliente.setFechaPrimeraCompra(LocalDate.now());
            repository.save(cliente);
        }
    }
}
