package example.repositories;

import example.entities.ResourceEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ResourceRepository extends JpaRepository<ResourceEntity, Long> {
}
