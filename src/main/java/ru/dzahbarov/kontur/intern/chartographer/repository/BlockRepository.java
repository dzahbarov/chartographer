package ru.dzahbarov.kontur.intern.chartographer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.dzahbarov.kontur.intern.chartographer.domain.Block;

/**
 * @author dzahbarov
 */

@Repository
public interface BlockRepository extends JpaRepository<Block, Long> {
}
