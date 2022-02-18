package ru.kontur.chartographer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kontur.chartographer.domain.Block;

/**
 * @author dzahbarov
 */

@Repository
public interface BlockRepository extends JpaRepository<Block, Long> {
}
