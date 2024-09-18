package org.grupo1.markapbe.controller;

import org.grupo1.markapbe.controller.dto.ProductDTO;
import org.grupo1.markapbe.persistence.entity.UserEntity;
import org.grupo1.markapbe.persistence.repository.UserRepository;
import org.grupo1.markapbe.service.ProductService;
import org.grupo1.markapbe.service.VisitedProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;


import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/productos")
public class ProductController {

    @Autowired
    private ProductService productoService;

    @Autowired
    private UserRepository repositoryUsuario;

    @Autowired
    private VisitedProductService visitedProductService;

    @GetMapping
    public List<ProductDTO> getAllProductos() {
        return productoService.getAllProductos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductoById(@PathVariable Long id) {
        Optional<ProductDTO> producto = productoService.getProductoById(id);
        return producto.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    //espero que funcione
    @GetMapping("/categoria/{id}")
    public ResponseEntity<List<ProductDTO>> getProductoByIdCategoria(@PathVariable Long id) {
        List<ProductDTO> productos = productoService.getProductosByIdCategoria(id);

        if (productos.isEmpty()) {
            return ResponseEntity.notFound().build(); // Devuelve 404 si no hay productos
        } else {
            return ResponseEntity.ok(productos); // Devuelve 200 con la lista de productos
        }
    }


    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDTO> createProducto(@RequestBody ProductDTO productoRequestDTO, Principal principal) {
        UserEntity user = repositoryUsuario.findUserEntityByUsername(principal.getName()).orElseThrow(() -> new UsernameNotFoundException("El usuario no fue encontrado"));
        ProductDTO producto = productoService.createProducto(productoRequestDTO, user);
        return ResponseEntity.ok(producto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Solo admin puede actualizar productos
    public ResponseEntity<ProductDTO> updateProducto(@PathVariable Long id, @RequestBody ProductDTO productoRequestDTO) {
        Optional<ProductDTO> updatedProducto = productoService.updateProducto(id, productoRequestDTO);
        return updatedProducto.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Solo admin puede eliminar productos
    public ResponseEntity<Void> deleteProducto(@PathVariable Long id) {
        if (productoService.deleteProducto(id)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
