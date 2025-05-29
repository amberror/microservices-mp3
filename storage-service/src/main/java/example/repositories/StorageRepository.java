package example.repositories;

import example.entities.StorageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface StorageRepository extends JpaRepository<StorageEntity, Long> {
	boolean existsByBucketAndPath(String bucket, String path);
}
