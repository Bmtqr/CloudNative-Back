package vidaSalud.msbff.controller;

import vidaSalud.msbff.client.catalogClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

@RestController 
@RequestMapping ("/api/bff/catalog")
public class catalogController {
    @Autowired 
    private catalogClient catalogClient;

    @GetMapping ("/services")
    public ResponseEntity<Object> getAllServices() {
        Object response = catalogClient.getAllCatalog();
        return ResponseEntity.ok(response);
    }

    @GetMapping ("/services/{id}")
    public ResponseEntity<Object> getServiceById(@PathVariable ("id") Long id) {
        Object response = catalogClient.getCatalogById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping ("/services")
    public ResponseEntity<Object> createService(@RequestBody Object request) {
        Object response = catalogClient.createCatalog(request);
        return ResponseEntity.status(201).body(response);
    }

    @PutMapping ("/services/{id}")
    public ResponseEntity<Object> updateService(@PathVariable ("id") Long id, @RequestBody Object request) {
        Object response = catalogClient.updateCatalog(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping ("/services/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable ("id") Long id) {
        catalogClient.deleteCatalog(id);
        return ResponseEntity.noContent().build();
    }


}
