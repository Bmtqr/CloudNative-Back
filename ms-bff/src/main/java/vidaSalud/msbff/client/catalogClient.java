package vidaSalud.msbff.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient (name = "catalog-client", url = "${catalog.url}")
public interface catalogClient {

    @GetMapping ("api/catalog/services")
    Object getAllCatalog();

    @GetMapping ("api/catalog/services/{id}")
    Object getCatalogById(@PathVariable ("id") Long id);

    @PostMapping ("api/catalog/services")
    Object createCatalog(@RequestBody Object request);

    @PutMapping ("api/catalog/services/{id}") /*Pendiente */
    Object updateCatalog(@PathVariable ("id") Long id, @RequestBody Object request);

    @DeleteMapping ("api/catalog/services/{id}")
    void deleteCatalog(@PathVariable ("id") Long id);
}
