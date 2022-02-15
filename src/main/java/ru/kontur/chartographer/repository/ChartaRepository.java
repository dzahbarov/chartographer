package ru.kontur.chartographer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kontur.chartographer.domain.Charta;

/**
 * @author dzahbarov
 */

@Repository
public interface ChartaRepository extends JpaRepository<Charta, Long> {

}
