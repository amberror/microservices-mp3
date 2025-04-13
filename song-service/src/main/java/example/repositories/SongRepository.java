package example.repositories;

import example.entities.SongEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SongRepository extends JpaRepository<SongEntity, Long> {
}
